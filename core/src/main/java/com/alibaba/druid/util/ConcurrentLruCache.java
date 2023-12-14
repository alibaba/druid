package com.alibaba.druid.util;

import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Simple LRU (Least Recently Used) cache, bounded by a specified cache capacity.
 * This is a simplified, opinionated implementation of an LRU cache
 * It is inspired from
 * <a href="https://github.com/spring-projects/spring-framework/blob/main/spring-core/src/main/java/org/springframework/util/ConcurrentLruCache.java">ConcurrentLruCache</a>.
 */
@SuppressWarnings("unchecked")
public final class ConcurrentLruCache<K, V> {
    private final int capacity;

    private final AtomicInteger currentSize = new AtomicInteger();

    private final ConcurrentMap<K, Node<K, V>> cache;

    private final ReadOperations<K, V> readOperations;

    private final WriteOperations writeOperations;

    private final Lock evictionLock = new ReentrantLock();

    /*
     * Queue that contains all ACTIVE cache entries, ordered with least recently used entries first.
     * Read and write operations are buffered and periodically processed to reorder the queue.
     */
    private final EvictionQueue<K, V> evictionQueue = new EvictionQueue<>();

    private final AtomicReference<DrainStatus> drainStatus = new AtomicReference<>(DrainStatus.IDLE);

    public ConcurrentLruCache(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be > 0");
        }
        this.capacity = capacity;
        this.cache = new ConcurrentHashMap<>(16, 0.75f, 16);
        this.readOperations = new ReadOperations<>(this.evictionQueue);
        this.writeOperations = new WriteOperations();
    }

    public V get(K key) {
        final Node<K, V> node = this.cache.get(key);
        if (node == null) {
            return null;
        }
        processRead(node);
        return node.getValue();
    }

    public V computeIfAbsent(K key, Function<K, V> generator) {
        if (key == null) {
            throw new IllegalArgumentException("key must not be null");
        }

        final AtomicBoolean write = new AtomicBoolean(false);

        Node<K, V> node =
                cache.computeIfAbsent(key, k -> {
                    V value = generator.apply(k);
                    if (value == null) {
                        throw new IllegalArgumentException("value must not be null");
                    }
                    final CacheEntry<V> cacheEntry = new CacheEntry<>(value, CacheEntryState.ACTIVE);
                    final Node<K, V> newNode = new Node<>(key, cacheEntry);
                    write.set(true);
                    return newNode;
                });

        if (write.get()) {
            processWrite(new AddTask(node));
        } else {
            processRead(node);
        }

        return node.getValue();
    }

    public void forEach(BiConsumer<K, V> action) {
        this.cache.forEach((k, kvNode) -> action.accept(k, kvNode.getValue()));
    }

    private void processRead(Node<K, V> node) {
        boolean drainRequested = this.readOperations.recordRead(node);
        final DrainStatus status = this.drainStatus.get();
        if (status.shouldDrainBuffers(drainRequested)) {
            drainOperations();
        }
    }

    private void processWrite(Runnable task) {
        this.writeOperations.add(task);
        this.drainStatus.lazySet(DrainStatus.REQUIRED);
        drainOperations();
    }

    private void drainOperations() {
        if (this.evictionLock.tryLock()) {
            try {
                this.drainStatus.lazySet(DrainStatus.PROCESSING);
                this.readOperations.drain();
                this.writeOperations.drain();
            } finally {
                this.drainStatus.compareAndSet(DrainStatus.PROCESSING, DrainStatus.IDLE);
                this.evictionLock.unlock();
            }
        }
    }

    public Set<K> keys() {
        return cache.keySet();
    }

    public int size() {
        return this.cache.size();
    }

    public void clear() {
        this.evictionLock.lock();
        try {
            Node<K, V> node;
            while ((node = this.evictionQueue.poll()) != null) {
                this.cache.remove(node.key, node);
                markAsRemoved(node);
            }
            this.readOperations.clear();
            this.writeOperations.drainAll();
        } finally {
            this.evictionLock.unlock();
        }
    }

    /*
     * Transition the node to the {@code removed} state and decrement the current size of the cache.
     */
    private void markAsRemoved(Node<K, V> node) {
        for (; ; ) {
            CacheEntry<V> current = node.get();
            CacheEntry<V> removed = new CacheEntry<>(current.value, CacheEntryState.REMOVED);
            if (node.compareAndSet(current, removed)) {
                this.currentSize.lazySet(this.currentSize.get() - 1);
                return;
            }
        }
    }

    /**
     * Determine whether the given key is present in this cache.
     *
     * @param key the key to check for
     * @return {@code true} if the key is present, {@code false} if there was no matching key
     */
    public boolean contains(K key) {
        return this.cache.containsKey(key);
    }

    /**
     * Immediately remove the given key and any associated value.
     *
     * @param key the key to evict the entry for
     * @return {@code true} if the key was present before,
     * {@code false} if there was no matching key
     */
    public boolean remove(K key) {
        final Node<K, V> node = this.cache.remove(key);
        if (node == null) {
            return false;
        }
        markForRemoval(node);
        processWrite(new RemovalTask(node));
        return true;
    }

    /*
     * Transition the node from the {@code active} state to the {@code pending removal} state,
     * if the transition is valid.
     */
    private void markForRemoval(Node<K, V> node) {
        for (; ; ) {
            final CacheEntry<V> current = node.get();
            if (!current.isActive()) {
                return;
            }
            final CacheEntry<V> pendingRemoval = new CacheEntry<>(current.value, CacheEntryState.PENDING_REMOVAL);
            if (node.compareAndSet(current, pendingRemoval)) {
                return;
            }
        }
    }

    /**
     * Write operation recorded when a new entry is added to the cache.
     */
    private final class AddTask implements Runnable {
        final Node<K, V> node;

        AddTask(Node<K, V> node) {
            this.node = node;
        }

        @Override
        public void run() {
            currentSize.lazySet(currentSize.get() + 1);
            if (this.node.get().isActive()) {
                evictionQueue.add(this.node);
                evictEntries();
            }
        }

        private void evictEntries() {
            while (currentSize.get() > capacity) {
                final Node<K, V> node = evictionQueue.poll();
                if (node == null) {
                    return;
                }
                cache.remove(node.key, node);
                markAsRemoved(node);
            }
        }
    }

    /**
     * Write operation recorded when an entry is removed to the cache.
     */
    private final class RemovalTask implements Runnable {
        final Node<K, V> node;

        RemovalTask(Node<K, V> node) {
            this.node = node;
        }

        @Override
        public void run() {
            evictionQueue.remove(this.node);
            markAsRemoved(this.node);
        }
    }

    /*
     * Draining status for the read/write buffers.
     */
    private enum DrainStatus {
        /*
         * No drain operation currently running.
         */
        IDLE {
            @Override
            boolean shouldDrainBuffers(boolean delayable) {
                return !delayable;
            }
        },

        /*
         * A drain operation is required due to a pending write modification.
         */
        REQUIRED {
            @Override
            boolean shouldDrainBuffers(boolean delayable) {
                return true;
            }
        },

        /*
         * A drain operation is in progress.
         */
        PROCESSING {
            @Override
            boolean shouldDrainBuffers(boolean delayable) {
                return false;
            }
        };

        /**
         * Determine whether the buffers should be drained.
         *
         * @param delayable if a drain should be delayed until required
         * @return if a drain should be attempted
         */
        abstract boolean shouldDrainBuffers(boolean delayable);
    }

    private enum CacheEntryState {
        ACTIVE, PENDING_REMOVAL, REMOVED
    }

    private static class CacheEntry<V> {
        private final V value;
        private final CacheEntryState state;

        public CacheEntry(V value, CacheEntryState state) {
            this.value = value;
            this.state = state;
        }

        boolean isActive() {
            return this.state == CacheEntryState.ACTIVE;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            CacheEntry<?> that = (CacheEntry<?>) o;
            return Objects.equals(value, that.value) && state == that.state;
        }

        @Override
        public int hashCode() {
            return Objects.hash(value, state);
        }

        @Override
        public String toString() {
            return "CacheEntry{" +
                    "value=" + value +
                    ", state=" + state +
                    '}';
        }
    }

    private static final class ReadOperations<K, V> {
        private static final int BUFFER_COUNT = detectNumberOfBuffers();

        private static int detectNumberOfBuffers() {
            int availableProcessors = Runtime.getRuntime().availableProcessors();
            int nextPowerOfTwo = 1 << (Integer.SIZE - Integer.numberOfLeadingZeros(availableProcessors - 1));
            return Math.min(4, nextPowerOfTwo);
        }

        private static final int BUFFERS_MASK = BUFFER_COUNT - 1;

        private static final int MAX_PENDING_OPERATIONS = 32;

        private static final int MAX_DRAIN_COUNT = 2 * MAX_PENDING_OPERATIONS;

        private static final int BUFFER_SIZE = 2 * MAX_DRAIN_COUNT;

        private static final int BUFFER_INDEX_MASK = BUFFER_SIZE - 1;

        /*
         * Number of operations recorded, for each buffer
         */
        private final AtomicLongArray recordedCount = new AtomicLongArray(BUFFER_COUNT);

        /*
         * Number of operations read, for each buffer
         */
        private final long[] readCount = new long[BUFFER_COUNT];

        /*
         * Number of operations processed, for each buffer
         */
        private final AtomicLongArray processedCount = new AtomicLongArray(BUFFER_COUNT);

        private final AtomicReferenceArray<Node<K, V>>[] buffers = new AtomicReferenceArray[BUFFER_COUNT];

        private final EvictionQueue<K, V> evictionQueue;

        ReadOperations(EvictionQueue<K, V> evictionQueue) {
            this.evictionQueue = evictionQueue;
            for (int i = 0; i < BUFFER_COUNT; i++) {
                this.buffers[i] = new AtomicReferenceArray<>(BUFFER_SIZE);
            }
        }

        private static int getBufferIndex() {
            return ((int) Thread.currentThread().getId()) & BUFFERS_MASK;
        }

        boolean recordRead(Node<K, V> node) {
            int bufferIndex = getBufferIndex();
            final long writeCount = this.recordedCount.get(bufferIndex);
            this.recordedCount.lazySet(bufferIndex, writeCount + 1);
            final int index = (int) (writeCount & BUFFER_INDEX_MASK);
            this.buffers[bufferIndex].lazySet(index, node);
            final long pending = (writeCount - this.processedCount.get(bufferIndex));
            return (pending < MAX_PENDING_OPERATIONS);
        }

        void drain() {
            final int start = (int) Thread.currentThread().getId();
            final int end = start + BUFFER_COUNT;
            for (int i = start; i < end; i++) {
                drainReadBuffer(i & BUFFERS_MASK);
            }
        }

        void clear() {
            for (int i = 0; i < BUFFER_COUNT; i++) {
                AtomicReferenceArray<Node<K, V>> buffer = this.buffers[i];
                for (int j = 0; j < BUFFER_SIZE; j++) {
                    buffer.lazySet(j, null);
                }
            }
        }

        private void drainReadBuffer(int bufferIndex) {
            final long writeCount = this.recordedCount.get(bufferIndex);
            for (int i = 0; i < MAX_DRAIN_COUNT; i++) {
                final int index = (int) (this.readCount[bufferIndex] & BUFFER_INDEX_MASK);
                final AtomicReferenceArray<Node<K, V>> buffer = this.buffers[bufferIndex];
                final Node<K, V> node = buffer.get(index);
                if (node == null) {
                    break;
                }
                buffer.lazySet(index, null);
                this.evictionQueue.moveToBack(node);
                this.readCount[bufferIndex]++;
            }
            this.processedCount.lazySet(bufferIndex, writeCount);
        }
    }

    private static final class WriteOperations {
        private static final int DRAIN_THRESHOLD = 16;

        private final Queue<Runnable> operations = new ConcurrentLinkedQueue<>();

        public void add(Runnable task) {
            this.operations.add(task);
        }

        public void drain() {
            for (int i = 0; i < DRAIN_THRESHOLD; i++) {
                final Runnable task = this.operations.poll();
                if (task == null) {
                    break;
                }
                task.run();
            }
        }

        public void drainAll() {
            Runnable task;
            while ((task = this.operations.poll()) != null) {
                task.run();
            }
        }
    }

    private static final class Node<K, V> extends AtomicReference<CacheEntry<V>> {
        private static final long serialVersionUID = 6034596142966329577L;

        final K key;
        Node<K, V> prev;
        Node<K, V> next;

        Node(K key, CacheEntry<V> cacheEntry) {
            super(cacheEntry);
            this.key = key;
        }

        public Node<K, V> getPrevious() {
            return this.prev;
        }

        public void setPrevious(Node<K, V> prev) {
            this.prev = prev;
        }

        public Node<K, V> getNext() {
            return this.next;
        }

        public void setNext(Node<K, V> next) {
            this.next = next;
        }

        V getValue() {
            return get().value;
        }
    }

    private static final class EvictionQueue<K, V> {
        Node<K, V> first;
        Node<K, V> last;

        Node<K, V> poll() {
            if (this.first == null) {
                return null;
            }
            final Node<K, V> f = this.first;
            final Node<K, V> next = f.getNext();
            f.setNext(null);

            this.first = next;
            if (next == null) {
                this.last = null;
            } else {
                next.setPrevious(null);
            }
            return f;
        }

        void add(Node<K, V> e) {
            if (contains(e)) {
                return;
            }
            linkLast(e);
        }

        private boolean contains(Node<K, V> e) {
            return (e.getPrevious() != null)
                    || (e.getNext() != null)
                    || (e == this.first);
        }

        private void linkLast(final Node<K, V> e) {
            final Node<K, V> l = this.last;
            this.last = e;

            if (l == null) {
                this.first = e;
            } else {
                l.setNext(e);
                e.setPrevious(l);
            }
        }

        private void unlink(Node<K, V> e) {
            final Node<K, V> prev = e.getPrevious();
            final Node<K, V> next = e.getNext();
            if (prev == null) {
                this.first = next;
            } else {
                prev.setNext(next);
                e.setPrevious(null);
            }
            if (next == null) {
                this.last = prev;
            } else {
                next.setPrevious(prev);
                e.setNext(null);
            }
        }

        void moveToBack(Node<K, V> e) {
            if (contains(e) && e != this.last) {
                unlink(e);
                linkLast(e);
            }
        }

        void remove(Node<K, V> e) {
            if (contains(e)) {
                unlink(e);
            }
        }
    }
}
