package com.alibaba.druid.bvt.pool;

import java.sql.Connection;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.stat.JdbcConnectionStat;
import com.alibaba.druid.stat.JdbcStatManager;

public class DruidDataSourceFilterTest extends TestCase {

    public void test_filter() throws Exception {
        DruidDataSource dataSource = new DruidDataSource();

        Assert.assertEquals(0, dataSource.getFilters().size());

        dataSource.setFilters("stat");

        Assert.assertEquals(1, dataSource.getFilters().size());
    }

    public void test_filter_2() throws Exception {
        DruidDataSource dataSource = new DruidDataSource();

        Assert.assertEquals(0, dataSource.getFilters().size());

        dataSource.setFilters("stat,trace");

        Assert.assertEquals(2, dataSource.getFilters().size());
    }

    public void test_filter_3() throws Exception {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:");

        Assert.assertEquals(0, dataSource.getFilters().size());

        dataSource.setFilters("stat");

        JdbcStatManager.getInstance().reset();

        Assert.assertEquals(0, JdbcStatManager.getInstance().getConnectionstat().getConnectCount());
        Assert.assertEquals(1, dataSource.getFilters().size());

        for (int i = 0; i < 2; ++i) {
            Connection conn = dataSource.getConnection();

            Assert.assertEquals(1, JdbcStatManager.getInstance().getConnectionstat().getConnectCount());
            Assert.assertEquals(0, JdbcStatManager.getInstance().getConnectionstat().getCloseCount());

            conn.close();

            Assert.assertEquals(1, JdbcStatManager.getInstance().getConnectionstat().getConnectCount());
            Assert.assertEquals(0, JdbcStatManager.getInstance().getConnectionstat().getCloseCount()); // logic
                                                                                                       // close不会导致计数器＋1
        }
    }
}
