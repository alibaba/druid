package com.alibaba.druid;

import junit.framework.TestCase;

import com.alibaba.druid.pool.DruidDataSource;

public class DruidTest extends TestCase {

    public void test_0() throws Exception {
        DruidDataSource ds = new DruidDataSource();
        ds.setMaxWait(30);
        ds.setInitialSize(0);
        ds.setUrl("jdbc:mysql:xxx");

        try {
            ds.getConnection();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
