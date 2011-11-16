package com.alibaba.druid.bvt.pool;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import com.alibaba.druid.mock.MockDriver;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.stat.DruidDataSourceStatManager;

import junit.framework.Assert;
import junit.framework.TestCase;

public class Case1 extends TestCase {

    protected void setUp() throws Exception {
        Assert.assertEquals(0, DruidDataSourceStatManager.getInstance().getDataSourceList().size());
    }

    protected void tearDown() throws Exception {
        Assert.assertEquals(0, DruidDataSourceStatManager.getInstance().getDataSourceList().size());
    }

    public void test_f() throws Exception {
        final DruidDataSource dataSource = new DruidDataSource();
        dataSource.setTimeBetweenConnectErrorMillis(100);

        final long startTime = System.currentTimeMillis();
        final long okTime = startTime + 1000 * 1;

        dataSource.setDriver(new MockDriver() {

            @Override
            public Connection connect(String url, Properties info) throws SQLException {
                if (System.currentTimeMillis() < okTime) {
                    throw new SQLException();
                }

                return super.connect(url, info);
            }
        });
        dataSource.setUrl("jdbc:mock:");

        dataSource.setMinIdle(0);
        dataSource.setMaxActive(2);
        dataSource.setMaxIdle(2);

        Connection conn = dataSource.getConnection();
        conn.close();

        dataSource.close();
    }
}
