package com.alibaba.druid.bvt.bug;

import java.sql.Connection;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.mock.MockDriver;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.stat.DruidDataSourceStatManager;

public class Bug_for_happyday517_3 extends TestCase {

    private DruidDataSource dataSource;
    private MockDriver      driver;

    protected void setUp() throws Exception {
        driver = new MockDriver();
        dataSource = new DruidDataSource();
        dataSource.setDriver(driver);
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setFilters("stat,trace,log4j,encoding");
        dataSource.setDefaultAutoCommit(false);
    }

    protected void tearDown() throws Exception {
        dataSource.close();
        Assert.assertEquals(0, DruidDataSourceStatManager.getInstance().getDataSourceList().size());
    }

    public void test_bug() throws Exception {
        Connection conn = dataSource.getConnection();
        Assert.assertEquals(false, conn.getAutoCommit());
        conn.close();
    }
}
