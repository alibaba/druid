package com.alibaba.druid.pool;

import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;

import java.util.NoSuchElementException;

public class TestBoundedCircularArrayDeque extends TestCase {

    private BoundedCircularArrayDeque<Integer> deque;

    public void setUp() {
        deque = new BoundedCircularArrayDeque<>(5);
    }

    @Test
    public void testAddFirstAndRemoveLast() {
        deque.addFirst(1);
        deque.addFirst(2);
        deque.addFirst(3);
        Assert.assertEquals(1, (int) deque.removeLast());
        Assert.assertEquals(2, (int) deque.removeLast());
        Assert.assertEquals(3, (int) deque.removeLast());
    }

    @Test
    public void testAddLastAndRemoveFirst() {
        deque.addLast(1);
        deque.addLast(2);
        deque.addLast(3);
        Assert.assertEquals(1, (int) deque.removeFirst());
        Assert.assertEquals(2, (int) deque.removeFirst());
        Assert.assertEquals(3, (int) deque.removeFirst());
    }

    @Test
    public void testIsFullAndIsEmpty() {
        Assert.assertTrue(deque.isEmpty());
        Assert.assertFalse(deque.isFull());

        deque.addLast(1);
        deque.addLast(2);
        deque.addLast(3);
        deque.addLast(4);
        deque.addLast(5);

        Assert.assertFalse(deque.isEmpty());
        Assert.assertTrue(deque.isFull());
    }

    @Test
    public void testSize() {
        Assert.assertEquals(0, deque.size());

        deque.addLast(1);
        deque.addLast(2);
        deque.addLast(3);

        Assert.assertEquals(3, deque.size());

        deque.removeFirst();

        Assert.assertEquals(2, deque.size());
    }

    @Test
    public void testIterator() {
        deque.addLast(1);
        deque.addLast(2);
        deque.addLast(3);

        StringBuilder sb = new StringBuilder();
        for (Integer i : deque) {
            sb.append(i).append("-");
        }
        Assert.assertEquals("1-2-3-", sb.toString());
    }

    @Test
    public void testRemoveFirstOnEmptyDequeWithNoSuchElementException() {
        Assert.assertThrows(NoSuchElementException.class, () -> deque.removeFirst());
    }

    @Test
    public void testRemoveLastOnEmptyDequeWithNoSuchElementException() {
        Assert.assertThrows(NoSuchElementException.class, () -> deque.removeLast());
    }
}

