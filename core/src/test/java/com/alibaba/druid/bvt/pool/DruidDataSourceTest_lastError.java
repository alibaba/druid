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

import static org.junit.jupiter.api.Assertions.*;

/**
 * 这个场景测试minIdle > maxActive
 *
 * @author wenshao [szujobs@hotmail.com]
 */
public class DruidDataSourceTest_lastError {
    private DruidDataSource dataSource;

    @BeforeEach
    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setTestOnBorrow(false);

        dataSource.getProxyFilters().add(new FilterAdapter() {
            public void connection_setAutoCommit(FilterChain chain, ConnectionProxy connection, boolean autoCommit)
                    throws SQLException {
                throw new SQLException();
            }
        });
    }

    @AfterEach
    protected void tearDown() throws Exception {
        dataSource.close();
    }

    @Test
    public void test_error() throws Exception {
        assertNull(dataSource.getLastError());
        assertNull(dataSource.getLastErrorTime());
        assertEquals(0, dataSource.getLastErrorTimeMillis());

        Connection conn = dataSource.getConnection();

        Exception error = null;
        try {
            conn.setAutoCommit(false);
        } catch (Exception e) {
            error = e;
        }

        assertNotNull(error);

        assertNotNull(dataSource.getLastError());
        assertNotNull(dataSource.getLastErrorTime());
        assertEquals(true, dataSource.getLastErrorTimeMillis() > 0);
    }
}
