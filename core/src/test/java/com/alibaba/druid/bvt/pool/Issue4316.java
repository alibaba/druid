package com.alibaba.druid.bvt.pool;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import org.junit.Test;

import static org.junit.Assert.assertFalse;

public class Issue4316 {
    @Test
    public void test_0() throws Exception {
//        DruidDataSource druidDataSource = new DruidDataSource();
//        druidDataSource.setUsername("");
//        druidDataSource.setPassword("");
//        druidDataSource.setUrl("jdbc:mock:xxx");
//        druidDataSource.setValidationQuery("select 1");
//        druidDataSource.setMinEvictableIdleTimeMillis(100000);
//        druidDataSource.setMaxEvictableIdleTimeMillis(DruidDataSource.DEFAULT_MAX_EVICTABLE_IDLE_TIME_MILLIS);
//        druidDataSource.setKeepAliveBetweenTimeMillis(120000);
//        druidDataSource.setMinIdle(2);
//        druidDataSource.setTimeBetweenEvictionRunsMillis(70000);
//        druidDataSource.setKeepAlive(true);
//        DruidPooledConnection connection1 = druidDataSource.getConnection();
//        DruidPooledConnection connection2 = druidDataSource.getConnection();
//        connection2.close();
//        Thread.sleep(95000);
//        connection1.close();
//        Thread.sleep(140000);
//        DruidPooledConnection connection3 = druidDataSource.getConnection();
//        DruidPooledConnection connection4 = druidDataSource.getConnection();
//        assertFalse(connection3.getConnectionHolder() == connection4.getConnectionHolder());
//        connection3.close();
//        connection4.close();
    }
}
