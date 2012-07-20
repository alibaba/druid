package com.alibaba.druid.bvt.bug;

import junit.framework.TestCase;

import com.alibaba.druid.util.JdbcUtils;

public class Bug_for_JeffYin extends TestCase {

    public void test_0() throws Exception {
        String driverClass = JdbcUtils.getDriverClassName("jdbc:sqlserver://localhost:1433;");
    }
}
