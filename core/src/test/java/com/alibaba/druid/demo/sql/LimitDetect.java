package com.alibaba.druid.demo.sql;

import org.junit.jupiter.api.Test;
public class LimitDetect {
    @Test
    public void test_limit() throws Exception {
        String sql = "select * from t limit 1";
    }
}
