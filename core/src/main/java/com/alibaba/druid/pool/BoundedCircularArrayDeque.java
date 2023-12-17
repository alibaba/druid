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
    private Object[] elementData;
    private int front;
    private int rear;
    private int capacity;
    private int size;

    public BoundedCircularArrayDeque(int capacity) {
        this.front = 0;
        this.rear = 0;
        this.size = 0;
        this.capacity = capacity;
        elementData = new Object[capacity];
    }

    public boolean addFirst(E element) {
        if (isFull()) {
            return false;
        }
        front = (front - 1 + capacity) % capacity;
        elementData[front] = element;
        size++;
        return true;
    }

    public boolean addLast(E element) {
        if (isFull()) {
            return false;
        }
        elementData[rear] = element;
        rear = (rear + 1) % capacity;
        size++;
        return true;
    }

    public E removeFirst() {
        if (isEmpty()) {
            // Impossible path to execute
            throw new NoSuchElementException("a serious bug occurred, returning a null connection");
        }
        E element = (E) elementData[front];
        elementData[front] = null;
        front = (front + 1) % capacity;
        size--;
        return element;
    }

    public E removeLast() {
        if (isEmpty()) {
            // Impossible path to execute
            throw new NoSuchElementException("a serious bug occurred, returning a null connection");
        }
        rear = (rear - 1 + capacity) % capacity;
        Object element = elementData[rear];
        elementData[rear] = null;
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

    class CycleQueueIterator implements Iterator<E> {
        private int cur = front;
        private int remainSize = size();

        @Override
        public boolean hasNext() {
            return remainSize > 0;
        }

        @Override
        public E next() {
            remainSize--;
            Object ret = elementData[cur];
            cur = (cur + 1) % capacity;
            return (E) ret;
        }
    }
}
