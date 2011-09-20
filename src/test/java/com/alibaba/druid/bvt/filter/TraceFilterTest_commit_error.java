package com.alibaba.druid.bvt.filter;

import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.Properties;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.filter.FilterChainImpl;
import com.alibaba.druid.filter.trace.TraceFilter;
import com.alibaba.druid.mock.MockConnection;
import com.alibaba.druid.mock.MockDriver;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.proxy.jdbc.ConnectionProxyImpl;
import com.alibaba.druid.stat.DruidDataSourceStatManager;
import com.alibaba.druid.stat.JdbcStatContext;
import com.alibaba.druid.stat.JdbcStatManager;

public class TraceFilterTest_commit_error extends TestCase {

    private MockDriver      driver;
    private DruidDataSource dataSource;
    private TraceFilter     filter;

    protected void setUp() throws Exception {
        Assert.assertEquals(0, DruidDataSourceStatManager.getInstance().getDataSourceList().size());

        driver = new MockDriver() {

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

    public void test_commit_error() throws Exception {
        FilterChainImpl chain = new FilterChainImpl(dataSource);

        MockConnection conn = new MockConnection() {

            private int i = 0;

            public void commit() throws SQLException {
                if (i++ % 2 == 1) {
                    throw new RuntimeException();
                } else {
                    throw new SQLException();
                }
            }
        };

        {
            SQLException error = null;
            try {
                filter.connection_commit(chain, new ConnectionProxyImpl(dataSource, conn, new Properties(), 0));
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
        {
            RuntimeException error = null;
            try {
                filter.connection_commit(chain, new ConnectionProxyImpl(dataSource, conn, new Properties(), 0));
            } catch (RuntimeException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

    }

    public void test_rollback_error() throws Exception {
        FilterChainImpl chain = new FilterChainImpl(dataSource);

        MockConnection conn = new MockConnection() {

            private int i = 0;

            public void rollback() throws SQLException {
                int count = i++ % 3;
                if (count % 3 == 1) {
                    throw new SQLException();
                } else if (count % 3 == 2) {
                    throw new RuntimeException();
                }
            }
        };

        filter.connection_rollback(chain, new ConnectionProxyImpl(dataSource, conn, new Properties(), 0));

        {
            SQLException error = null;
            try {
                filter.connection_rollback(chain, new ConnectionProxyImpl(dataSource, conn, new Properties(), 0));
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
        {
            RuntimeException error = null;
            try {
                filter.connection_rollback(chain, new ConnectionProxyImpl(dataSource, conn, new Properties(), 0));
            } catch (RuntimeException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

    }

    public void test_rollback_2() throws Exception {
        FilterChainImpl chain = new FilterChainImpl(dataSource);

        MockConnection conn = new MockConnection() {

            private int i = 0;

            public void rollback(Savepoint savepoint) throws SQLException {
                int count = i++ % 3;
                if (count % 3 == 1) {
                    throw new SQLException();
                } else if (count % 3 == 2) {
                    throw new RuntimeException();
                }
            }
        };

        Savepoint savepoint = new Savepoint() {

            @Override
            public int getSavepointId() throws SQLException {
                // TODO Auto-generated method stub
                return 0;
            }

            @Override
            public String getSavepointName() throws SQLException {
                // TODO Auto-generated method stub
                return null;
            }
            
        };
        filter.connection_rollback(chain, new ConnectionProxyImpl(dataSource, conn, new Properties(), 0), savepoint);

        {
            SQLException error = null;
            try {
                filter.connection_rollback(chain, new ConnectionProxyImpl(dataSource, conn, new Properties(), 0), savepoint);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
        {
            RuntimeException error = null;
            try {
                filter.connection_rollback(chain, new ConnectionProxyImpl(dataSource, conn, new Properties(), 0), savepoint);
            } catch (RuntimeException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

    }
}
