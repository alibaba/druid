package com.alibaba.druid.bvt.utils;

import static org.junit.Assert.assertEquals;

import junit.framework.TestCase;


import com.alibaba.druid.util.DaemonThreadFactory;


public class DaemonThreadFactoryTest extends TestCase {
    public void test_0() throws Exception {
        Runnable task = new Runnable() {
            public void run() {
            }
        };
        DaemonThreadFactory factory = new DaemonThreadFactory("test");
        assertEquals("[test-1]", factory.newThread(task).getName());
        assertEquals("[test-2]", factory.newThread(task).getName());
    }
}
