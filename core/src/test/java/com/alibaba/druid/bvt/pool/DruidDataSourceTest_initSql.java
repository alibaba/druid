package com.alibaba.druid.bvt.pool;

import com.alibaba.druid.mock.MockConnection;
import com.alibaba.druid.pool.DruidDataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DruidDataSourceTest_initSql {
    private DruidDataSource dataSource;

    @BeforeEach
    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xxx");
        List<Object> sqlList = new ArrayList<Object>();
        sqlList.add("select 123");
        sqlList.add(null);
        sqlList.add("");

        dataSource.setConnectionInitSqls(sqlList);
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
