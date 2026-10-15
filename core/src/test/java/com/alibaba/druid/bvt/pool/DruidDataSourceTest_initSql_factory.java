package com.alibaba.druid.bvt.pool;

import com.alibaba.druid.mock.MockConnection;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

public class DruidDataSourceTest_initSql_factory {
    private DruidDataSource dataSource;

    @BeforeEach
    protected void setUp() throws Exception {
        Properties properties = new Properties();
        properties.put(DruidDataSourceFactory.PROP_URL, "jdbc:mock:xxx");
        properties.put(DruidDataSourceFactory.PROP_INITCONNECTIONSQLS, ";;select 123");
        dataSource = (DruidDataSource) DruidDataSourceFactory.createDataSource(properties);
    }

    @AfterEach
    protected void tearDown() throws Exception {
        dataSource.close();
    }

    @Test
    public void testDefault() throws Exception {
        Connection conn = dataSource.getConnection();

        MockConnection mockConn = conn.unwrap(MockConnection.class);

        assertEquals("select 123", mockConn.getLastSql());

        conn.close();
    }
}
