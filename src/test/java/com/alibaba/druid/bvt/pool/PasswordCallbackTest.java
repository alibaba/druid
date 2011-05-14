package com.alibaba.druid.bvt.pool;

import junit.framework.TestCase;

import com.alibaba.druid.pool.DruidDataSource;

public class PasswordCallbackTest extends TestCase {
    public void test_0 () throws Exception {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:");
        dataSource.setPasswordCallback(passwordCallback)
    }
}
