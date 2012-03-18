package com.alibaba.druid.bvt.filter;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.filter.trace.TraceFilter;
import com.alibaba.druid.mock.MockDriver;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.stat.DruidDataSourceStatManager;
import com.alibaba.druid.stat.JdbcStatContext;
import com.alibaba.druid.stat.JdbcStatManager;

public class TraceFilterTest_connect_error_2 extends TestCase {

    private MockDriver      driver;
    private DruidDataSource dataSource;
    private TraceFilter     filter;

    protected void setUp() throws Exception {
        Assert.assertEquals(0, DruidDataSourceStatManager.getInstance().getDataSourceList().size());

        driver = new MockDriver() {

            public Connection connect(String url, Properties info) throws SQLException {
                throw new RuntimeException();
            }
        };

        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setDriver(driver);
        dataSource.setInitialSize(1);
        dataSource.setMaxActive(2);
        dataSource.setMaxIdle(2);
        dataSource.setMinIdle(1);
        dataSource.setMinEvictableIdleTimeMillis(300 * 1000); // 300 / 10
        dataSource.setTimeBetweenEvictionRunsMillis(180 * 1000); // 180 / 10
        dataSource.setTestWhileIdle(true);
        dataSource.setTestOnBorrow(false);
        dataSource.setValidationQuery("SELECT 1");
        dataSource.setFilters("trace");

        filter = (TraceFilter) dataSource.getProxyFilters().get(0);
        JdbcStatContext statContext = new JdbcStatContext();
        statContext.setTraceEnable(true);
        JdbcStatManager.getInstance().setStatContext(statContext);
    }

    protected void tearDown() throws Exception {
        dataSource.close();
        Assert.assertEquals(0, DruidDataSourceStatManager.getInstance().getDataSourceList().size());
        JdbcStatManager.getInstance().setStatContext(null);
    }

    public void test_exuecute() throws Exception {
        {
            Exception error = null;
            try {
                dataSource.getConnection();
            } catch (Exception ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

    }

}
