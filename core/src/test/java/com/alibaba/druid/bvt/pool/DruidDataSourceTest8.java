package com.alibaba.druid.bvt.pool;

import static org.junit.Assert.*;


import java.sql.SQLException;
import java.util.Properties;

import junit.framework.TestCase;


import com.alibaba.druid.filter.FilterAdapter;
import com.alibaba.druid.filter.FilterChain;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.proxy.jdbc.ConnectionProxy;

public class DruidDataSourceTest8 extends TestCase {
    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setInitialSize(1);
        dataSource.getProxyFilters().add(new FilterAdapter() {
            @Override
            public ConnectionProxy connection_connect(FilterChain chain, Properties info) throws SQLException {
                throw new Error();
            }
        });
    }

    protected void tearDown() throws Exception {
        dataSource.close();
    }

    public void testInitError() throws Exception {
        assertEquals(0, dataSource.getCreateErrorCount());
        Throwable error = null;
        try {
            dataSource.init();
        } catch (Throwable e) {
            error = e;
        }
        assertNotNull(error);
        assertTrue(dataSource.getCreateErrorCount() > 0);

        dataSource.getCompositeData();
    }

}
