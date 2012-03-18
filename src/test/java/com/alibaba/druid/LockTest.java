package com.alibaba.druid;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import junit.framework.TestCase;

public class LockTest extends TestCase {

    public void test_0() throws Exception {
        Lock lock = new ReentrantLock();

        lock.lock();
        lock.unlock();
    }
}
