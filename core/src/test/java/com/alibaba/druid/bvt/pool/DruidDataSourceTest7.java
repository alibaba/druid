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

public class DruidDataSourceTest7 {
    private DruidDataSource dataSource;

    @BeforeEach
    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setInitialSize(1);
        dataSource.getProxyFilters().add(new FilterAdapter() {
            @Override
            public ConnectionProxy connection_connect(FilterChain chain, Properties info) throws SQLException {
                throw new RuntimeException();
            }
        });
    }

    @AfterEach
    protected void tearDown() throws Exception {
        dataSource.close();
    }

    @Test
    public void testInitError() throws Exception {
        assertEquals(0, dataSource.getCreateErrorCount());
        Throwable error = null;
        try {
            dataSource.init();
        } catch (RuntimeException e) {
            error = e;
        }
        assertNotNull(error);
        assertTrue(dataSource.getCreateErrorCount() > 0);

        dataSource.getCompositeData();
    }
}
