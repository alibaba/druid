package com.alibaba.druid.mysql;

import com.alibaba.druid.pool.DruidDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.util.concurrent.Executors;

/**
 * Created by wenshao on 10/08/2017.
 */
public class MySqlConnectFailTest {
    DruidDataSource dataSource;

    @BeforeEach
    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mysql://rm-bp1n325y4m6h78xt3.mysql.rds.aliyuncs.com:3306/oracle_info?allowMultiQueries=true&characterEncoding=UTF8");
        dataSource.setUsername("xx");
        dataSource.setPassword("xxx");
        dataSource.setCreateScheduler(Executors.newScheduledThreadPool(10));
    }

    @Test
    public void test_0() throws Exception {
        Connection conn = dataSource.getConnection();
        conn.close();
    }
}
