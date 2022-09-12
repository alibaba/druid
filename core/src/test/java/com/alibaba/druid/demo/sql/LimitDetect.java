package com.alibaba.druid.demo.sql;

import junit.framework.TestCase;

public class LimitDetect extends TestCase {
    public void test_limit() throws Exception {
        String sql = "select * from t limit 1";
    }
}
