package com.alibaba.druid.bvt.utils;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.util.DaemonThreadFactory;


public class DaemonThreadFactoryTest extends TestCase  {
    public void test_0 () throws Exception {
        Runnable task = new Runnable() {
            public void run() {
                
            }
        };
        DaemonThreadFactory factory = new DaemonThreadFactory("test");
        Assert.assertEquals("[test-1]", factory.newThread(task).getName());
        Assert.assertEquals("[test-2]", factory.newThread(task).getName());
    }
}
