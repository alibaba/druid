package com.alibaba.druid.bvt.pool;

import java.sql.Connection;

import org.junit.Assert;

import junit.framework.TestCase;

import com.alibaba.druid.pool.DruidDataSource;

public class FullTest extends TestCase {

    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setTestOnBorrow(false);

        dataSource.init();
    }

    protected void tearDown() throws Exception {
        dataSource.close();
    }

    public void test_restart() throws Exception {
        Assert.assertEquals(false, dataSource.isFull());
        dataSource.fill();

        Assert.assertEquals(true, dataSource.isFull());
        Connection conn = dataSource.getConnection();
        Assert.assertEquals(true, dataSource.isFull());
        conn.close();
        Assert.assertEquals(true, dataSource.isFull());
    }
}
