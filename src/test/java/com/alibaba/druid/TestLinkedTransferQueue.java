package com.alibaba.druid;

import junit.framework.TestCase;

import com.alibaba.druid.util.LinkedTransferQueue;

public class TestLinkedTransferQueue extends TestCase {

    public void test_0() throws Exception {
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
        q.put(1);
        q.put(2);
        System.out.println(q.size());
    }
}
