package com.alibaba.druid.bvt.pool;

import com.alibaba.druid.filter.FilterAdapter;
import com.alibaba.druid.filter.FilterChain;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.proxy.jdbc.ConnectionProxy;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 这个场景测试defaultAutoCommit
 *
 * @author wenshao [szujobs@hotmail.com]
 */
public class DruidDataSourceTest_autocommit4 {
    private DruidDataSource dataSource;

    @BeforeEach
    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setTestOnBorrow(false);
        dataSource.setDefaultAutoCommit(true);
        dataSource.setInitialSize(1);
        dataSource.getProxyFilters().add(new FilterAdapter() {
            public ConnectionProxy connection_connect(FilterChain chain, Properties info) throws SQLException {
                ConnectionProxy conn = chain.connection_connect(info);
                conn.setAutoCommit(true);
                return conn;
            }
        });
    }

    @AfterEach
    protected void tearDown() throws Exception {
        dataSource.close();
    }

    @Test
    public void test_autoCommit() throws Exception {
        Connection conn = dataSource.getConnection();

        assertTrue(conn.getAutoCommit());

        conn.close();
    }
}
