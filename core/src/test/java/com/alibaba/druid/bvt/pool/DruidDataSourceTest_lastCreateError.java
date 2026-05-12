package com.alibaba.druid.bvt.pool;

import com.alibaba.druid.filter.FilterAdapter;
import com.alibaba.druid.filter.FilterChain;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.proxy.jdbc.ConnectionProxy;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 这个场景测试minIdle > maxActive
 *
 * @author wenshao [szujobs@hotmail.com]
 */
public class DruidDataSourceTest_lastCreateError {
    private DruidDataSource dataSource;

    @BeforeEach
    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setTestOnBorrow(false);

        dataSource.getProxyFilters().add(new FilterAdapter() {
            public ConnectionProxy connection_connect(FilterChain chain, Properties info) throws SQLException {
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
        assertNull(dataSource.getLastCreateError());
        assertNull(dataSource.getLastCreateErrorTime());
        assertEquals(0, dataSource.getLastCreateErrorTimeMillis());

        Exception error = null;
        try {
            dataSource.getConnection(100);
        } catch (Exception e) {
            error = e;
        }

        assertNotNull(error);

        assertNotNull(dataSource.getLastCreateError());
        assertNotNull(dataSource.getLastCreateErrorTime());
        assertEquals(true, dataSource.getLastCreateErrorTimeMillis() > 0);
    }
}
