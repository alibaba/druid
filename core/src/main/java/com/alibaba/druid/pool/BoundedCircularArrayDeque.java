/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.druid.pool;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A BoundedCircularArrayDeque is a deque (double-ended queue) implementation
 * backed by an array with a fixed capacity. It supports adding and removing
 * elements from both ends, and has a maximum capacity set at creation time.
 */
public class BoundedCircularArrayDeque<E> implements Iterable<E> {
    private Object[] elements;
    private int head;
    private int tail;
    private int capacity;
    private int size;

    public BoundedCircularArrayDeque(int capacity) {
        this.head = 0;
        this.tail = 0;
        this.size = 0;
        this.capacity = capacity;
        elements = new Object[capacity];
    }

    public boolean offerFirst(E element) {
        if (isFull()) {
            return false;
        }
        head = (head - 1 + capacity) % capacity;
        elements[head] = element;
        size++;
        return true;
    }

    public boolean offerLast(E element) {
        if (isFull()) {
            return false;
        }
        elements[tail] = element;
        tail = (tail + 1) % capacity;
        size++;
        return true;
    }

    public E pollFirst() {
        if (isEmpty()) {
            // Impossible path to execute
            throw new NoSuchElementException();
        }
        E element = (E) elements[head];
        elements[head] = null;
        head = (head + 1) % capacity;
        size--;
        return element;
    }

    public E pollLast() {
        if (isEmpty()) {
            // Impossible path to execute
            throw new NoSuchElementException();
        }
        tail = (tail - 1 + capacity) % capacity;
        Object element = elements[tail];
        elements[tail] = null;
        size--;
        return (E) element;
    }

    public boolean isFull() {
        return size == capacity;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    @Override
    public Iterator<E> iterator() {
        return new CycleQueueIterator();
    }

    /**
     * Returns an iterator over the elements in this deque.  The elements
     * will be ordered from first (head) to last (tail).
     *
     * @return an iterator over the elements in this deque
     */
    class CycleQueueIterator implements Iterator<E> {
        private int cur = head;
        private int remainSize = size();

        @Override
        public boolean hasNext() {
            return remainSize > 0;
        }

        @Override
        public E next() {
            remainSize--;
            Object ret = elements[cur];
            cur = (cur + 1) % capacity;
            return (E) ret;
        }
    }
}
