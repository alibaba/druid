package com.alibaba.druid;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import junit.framework.TestCase;

import com.alibaba.druid.util.LinkedTransferQueue;

public class TestLinkedTransferQueue extends TestCase {

    public void test_0() throws Exception {
        final Lock lock = new ReentrantLock();
        
        lock.lock();
        
        final LinkedTransferQueue<Object> q = new LinkedTransferQueue<Object>();

        Thread takeThread = new Thread("Take Thread") {

            public void run() {
                try {
                    for (;;) {
                        q.take();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        takeThread.start();

        // q.take();
        for (int i = 0; i < 10; ++i) {
            Object value = i;
            q.transfer(value);
        }
        
        q.put(1);
        q.put(2);
        System.out.println(q.size());
    }
}
