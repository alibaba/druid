package com.alibaba.druid.util;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Function;


/**
 * Simple LRU (Least Recently Used) cache, bounded by a specified cache limit.
 *
 * @author shenjianeng [ishenjianeng@qq.com]
 */
public class ConcurrentLruCache<K, V> {

    private final ConcurrentHashMap<K, V> cache = new ConcurrentHashMap<>();

    private final ConcurrentLinkedDeque<K> queue = new ConcurrentLinkedDeque<>();

    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    private final AtomicInteger size = new AtomicInteger(0);

    private final int sizeLimit;

    public ConcurrentLruCache(int sizeLimit) {
        if (sizeLimit <= 0) {
            throw new IllegalArgumentException("Cache size limit must be > 0");
        }
        this.sizeLimit = sizeLimit;
    }

    public V get(K key) {
        V cached = this.cache.get(key);
        if (cached != null) {
            if (this.size.get() < this.sizeLimit) {
                return cached;
            }
            this.lock.readLock().lock();
            try {
                if (this.queue.removeLastOccurrence(key)) {
                    this.queue.offer(key);
                }
                return cached;
            } finally {
                this.lock.readLock().unlock();
            }
        }
        return cached;
    }


    public V computeIfAbsent(K key, Function<K, V> generator) {
        V cached = get(key);
        if (cached != null) {
            return cached;
        }

        this.lock.writeLock().lock();
        try {
            cached = this.cache.get(key);
            if (cached != null) {
                if (this.size.get() < this.sizeLimit) {
                    return cached;
                }
                if (this.queue.removeLastOccurrence(key)) {
                    this.queue.offer(key);
                }
                return cached;
            }
            V value = generator.apply(key);
            boolean hasRemoved = false;
            if (this.size.get() == this.sizeLimit) {
                K leastUsed = this.queue.poll();
                if (leastUsed != null) {
                    this.cache.remove(leastUsed);
                    hasRemoved = true;
                }
            }
            this.queue.offer(key);
            this.cache.put(key, value);
            if (!hasRemoved) {
                this.size.incrementAndGet();
            }
            return value;
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    public boolean contains(K key) {
        return this.cache.containsKey(key);
    }


    public Set<K> keys() {
        return cache.keySet();
    }


    public Set<Map.Entry<K, V>> entrySet() {
        return cache.entrySet();
    }


    public void clear() {
        this.lock.writeLock().lock();
        try {
            this.cache.clear();
            this.queue.clear();
            this.size.set(0);
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    public int size() {
        return this.size.get();
    }

}