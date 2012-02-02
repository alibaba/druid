package com.alibaba.druid;

import junit.framework.TestCase;

import com.alibaba.druid.util.LinkedTransferQueue;

public class TestLinkedTransferQueue extends TestCase {

    public void test_LinkedTransferQueue() throws Exception {
        final LinkedTransferQueue<String> q = new LinkedTransferQueue<String>();

        Thread thread = new Thread() {

            public void run() {
                for (;;) {
                    try {
                        q.take();
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        };
        thread.start();
        q.put("1");
    }
}
