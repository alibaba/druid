package com.alibaba.druid.bvt.pool;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

public class DruidDataSourceTest9_phyMaxUseCount {
    private DruidDataSource dataSource;

    @BeforeEach
    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setInitialSize(1);
        dataSource.setPhyMaxUseCount(10);

        assertEquals(10, dataSource.getPhyMaxUseCount());

        Properties properties = new Properties();
        properties.put("druid.phyMaxUseCount", "20");
        dataSource.configFromPropeties(properties);
        assertEquals(20, dataSource.getPhyMaxUseCount());

        properties.put("druid.phyMaxUseCount", "10");
        dataSource.configFromPropeties(properties);
        assertEquals(10, dataSource.getPhyMaxUseCount());

        dataSource.setFilters("log4j");
    }

    @AfterEach
    protected void tearDown() throws Exception {
        dataSource.close();
    }

    @Test
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
