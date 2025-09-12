package com.alibaba.druid.bvt.stat;

import com.alibaba.druid.stat.JdbcTraceManager;
import junit.framework.TestCase;

@SuppressWarnings("deprecation")
public class JdbcTraceManagerTest extends TestCase {
    public void test_instance() throws Exception {
        JdbcTraceManager.getInstance();
    }
}
