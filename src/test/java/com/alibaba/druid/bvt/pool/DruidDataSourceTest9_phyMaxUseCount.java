package com.alibaba.druid.bvt.pool;

import com.alibaba.druid.filter.FilterAdapter;
import com.alibaba.druid.filter.FilterChain;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.druid.proxy.jdbc.ConnectionProxy;
import junit.framework.TestCase;
import org.junit.Assert;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class DruidDataSourceTest9_phyMaxUseCount extends TestCase {

    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setInitialSize(1);
        dataSource.setPhyMaxUseCount(10);

        assertEquals(10, dataSource.getPhyMaxUseCount());

        Properties properties = new Properties();
        properties.put("druid.phyMaxUseCount", "20");
        dataSource.configFromPropety(properties);
        assertEquals(20, dataSource.getPhyMaxUseCount());

        properties.put("druid.phyMaxUseCount", "10");
        dataSource.configFromPropety(properties);
        assertEquals(10, dataSource.getPhyMaxUseCount());

        dataSource.setFilters("log4j");
    }

    protected void tearDown() throws Exception {
        dataSource.close();
    }

    public void test_for_phyMaxUseCount() throws Exception {
        Connection phyConn = null;
        for (int i = 0; i < 100; ++i) {
            DruidPooledConnection conn = dataSource.getConnection();
            if (i % 10 == 0) {
                if (conn.getConnection() == phyConn) {
                     throw new IllegalStateException();
                }
            }

            phyConn = conn.getConnection();
            conn.close();
//            System.out.println(i);
        }
    }

}
