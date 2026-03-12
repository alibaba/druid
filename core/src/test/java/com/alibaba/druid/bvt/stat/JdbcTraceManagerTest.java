package com.alibaba.druid.bvt.stat;

import com.alibaba.druid.stat.JdbcTraceManager;
import org.junit.jupiter.api.Test;

@SuppressWarnings("deprecation")
public class JdbcTraceManagerTest {
    @Test
    public void test_instance() throws Exception {
        JdbcTraceManager.getInstance();
    }
}
