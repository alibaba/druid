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
    public void testofferFirstAndpollLast() {
        deque.offerFirst(1);
        deque.offerFirst(2);
        deque.offerFirst(3);
        Assert.assertEquals(1, (int) deque.pollLast());
        Assert.assertEquals(2, (int) deque.pollLast());
        Assert.assertEquals(3, (int) deque.pollLast());
    }

    @Test
    public void testofferLastAndpollFirst() {
        deque.offerLast(1);
        deque.offerLast(2);
        deque.offerLast(3);
        Assert.assertEquals(1, (int) deque.pollFirst());
        Assert.assertEquals(2, (int) deque.pollFirst());
        Assert.assertEquals(3, (int) deque.pollFirst());
    }

    @Test
    public void testIsFullAndIsEmpty() {
        Assert.assertTrue(deque.isEmpty());
        Assert.assertFalse(deque.isFull());

        deque.offerLast(1);
        deque.offerLast(2);
        deque.offerLast(3);
        deque.offerLast(4);
        deque.offerLast(5);

        Assert.assertFalse(deque.isEmpty());
        Assert.assertTrue(deque.isFull());
    }

    @Test
    public void testSize() {
        Assert.assertEquals(0, deque.size());

        deque.offerLast(1);
        deque.offerLast(2);
        deque.offerLast(3);

        Assert.assertEquals(3, deque.size());

        deque.pollFirst();

        Assert.assertEquals(2, deque.size());
    }

    @Test
    public void testIterator() {
        deque.offerLast(1);
        deque.offerLast(2);
        deque.offerLast(3);

        StringBuilder sb = new StringBuilder();
        for (Integer i : deque) {
            sb.append(i).append("-");
        }
        Assert.assertEquals("1-2-3-", sb.toString());
    }

    @Test
    public void testpollFirstOnEmptyDequeWithNoSuchElementException() {
        Assert.assertThrows(NoSuchElementException.class, () -> deque.pollFirst());
    }
    @Test
    public void testpollLastOnEmptyDequeWithNoSuchElementException() {
        Assert.assertThrows(NoSuchElementException.class, () -> deque.pollLast());
    }
}

