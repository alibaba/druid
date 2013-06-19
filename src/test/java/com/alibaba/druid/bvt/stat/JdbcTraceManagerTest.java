package com.alibaba.druid.bvt.stat;

import junit.framework.TestCase;

import com.alibaba.druid.stat.JdbcTraceManager;

@SuppressWarnings("deprecation")
public class JdbcTraceManagerTest extends TestCase {

    public void test_instance() throws Exception {
        JdbcTraceManager.getInstance();
    }
}
