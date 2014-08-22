package com.alibaba.druid.bvt.pool;

import java.sql.Connection;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import junit.framework.TestCase;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.util.JdbcUtils;

public class Large10KTest extends TestCase {

    private DruidDataSource[]        dataSources = new DruidDataSource[10000];
    private ScheduledExecutorService scheduler;

    protected void setUp() throws Exception {
        scheduler = Executors.newScheduledThreadPool(10);
        for (int i = 0; i < dataSources.length; ++i) {
            DruidDataSource dataSource = new DruidDataSource();
            dataSource.setUrl("jdbc:mock:xxx");
            dataSource.setCreateScheduler(scheduler);
            dataSource.setDestroyScheduler(scheduler);
            dataSource.setTestOnBorrow(false);
            dataSource.setTestWhileIdle(false);
            
            dataSources[i] = dataSource;
        }
    }

    protected void tearDown() throws Exception {
        for (int i = 0; i < dataSources.length; ++i) {
            JdbcUtils.close(dataSources[i]);
        }
        scheduler.shutdown();
    }
    
    public void test_large() throws Exception {
        Connection[] connections = new Connection[dataSources.length * 8];
        for (int i = 0; i < dataSources.length; ++i) {
            for (int j = 0; j < 8; ++j) {
                connections[i * 8 + j] = dataSources[i].getConnection();
            }
        }
        
        for (int i = 0; i < dataSources.length; ++i) {
            for (int j = 0; j < 8; ++j) {
                connections[i * 8 + j].close();
            }
        }
    }
}
