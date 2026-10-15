package com.alibaba.druid.bvt.pool;

import com.alibaba.druid.filter.Filter;
import com.alibaba.druid.filter.FilterAdapter;
import com.alibaba.druid.filter.FilterChain;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.proxy.jdbc.ConnectionProxy;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;

public class HoldableUnsupportTest {
    private DruidDataSource dataSource;

    @BeforeEach
    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xxx");
        Filter filter = new FilterAdapter() {
            @Override
            public int connection_getHoldability(FilterChain chain, ConnectionProxy connection) throws SQLException {
                throw new UnsupportedOperationException();
            }
        };
        dataSource.getProxyFilters().add(filter);
    }

    @AfterEach
    protected void tearDown() throws Exception {
        dataSource.close();
    }

    @Test
    public void test_0() throws Exception {
        Connection[] connections = new Connection[8];
        for (int i = 0; i < connections.length; ++i) {
            connections[i] = dataSource.getConnection();
        }

        for (int i = 0; i < connections.length; ++i) {
            connections[i].close();
        }
    }
}
