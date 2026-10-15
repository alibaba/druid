package com.alibaba.druid.bvt.bug;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.util.JdbcUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

public class Bug_for_wdw1206 {
    private ClassLoader ctxClassLoader;
    private DruidDataSource dataSource;

    @BeforeEach
    protected void setUp() throws Exception {
        ctxClassLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(null);

        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:");
        dataSource.setPoolPreparedStatements(false);
        dataSource.setTestOnBorrow(true);
        dataSource.setFilters("stat");
    }

    @AfterEach
    protected void tearDown() throws Exception {
        Thread.currentThread().setContextClassLoader(ctxClassLoader);

        JdbcUtils.close(dataSource);
    }

    @Test
    public void test_nullCtxClassLoader() throws Exception {
        Connection conn = dataSource.getConnection();
        conn.close();
    }
}
