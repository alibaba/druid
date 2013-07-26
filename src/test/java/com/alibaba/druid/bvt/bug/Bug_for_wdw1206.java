package com.alibaba.druid.bvt.bug;

import java.sql.Connection;

import junit.framework.TestCase;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.util.JdbcUtils;

public class Bug_for_wdw1206 extends TestCase {

    private ClassLoader     ctxClassLoader;
    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        ctxClassLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(null);

        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:");
        dataSource.setPoolPreparedStatements(false);
        dataSource.setTestOnBorrow(true);
        dataSource.setFilters("stat");
    }

    protected void tearDown() throws Exception {
        Thread.currentThread().setContextClassLoader(ctxClassLoader);
        
        JdbcUtils.close(dataSource);
    }

    public void test_nullCtxClassLoader() throws Exception {
        Connection conn = dataSource.getConnection();
        conn.close();
    }
}
