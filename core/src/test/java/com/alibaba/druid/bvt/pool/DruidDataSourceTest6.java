package com.alibaba.druid.bvt.pool;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.atomic.AtomicInteger;

import javax.security.auth.callback.NameCallback;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.filter.FilterAdapter;
import com.alibaba.druid.filter.FilterChain;
import com.alibaba.druid.mock.MockResultSet;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.proxy.jdbc.ResultSetProxy;
import com.alibaba.druid.proxy.jdbc.ResultSetProxyImpl;
import com.alibaba.druid.proxy.jdbc.StatementProxy;
import com.alibaba.druid.util.DruidPasswordCallback;
import com.alibaba.druid.util.JdbcUtils;

public class DruidDataSourceTest6 extends TestCase {
    private DruidDataSource dataSource;

    private final AtomicInteger errorCount = new AtomicInteger();
    private final AtomicInteger returnEmptyCount = new AtomicInteger();

    protected void setUp() throws Exception {
        returnEmptyCount.set(0);

        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setTestOnBorrow(true);
        dataSource.setInitialSize(1);
        dataSource.setValidationQuery("select 1");
        dataSource.setValidationQueryTimeout(10);
        dataSource.setQueryTimeout(100);

        dataSource.setUserCallback(new NameCallback("xx") {
        });

        dataSource.setPasswordCallback(new DruidPasswordCallback() {
            @Override
            public char[] getPassword() {
                return "xx".toCharArray();
            }
        });

        dataSource.getProxyFilters().add(new FilterAdapter() {
            public ResultSetProxy statement_executeQuery(FilterChain chain, StatementProxy statement, String sql)
                    throws SQLException {
                if (errorCount.get() > 0) {
                    errorCount.decrementAndGet();
                    throw new RuntimeException();
                }

                if (returnEmptyCount.get() > 0) {
                    returnEmptyCount.decrementAndGet();
                    return new ResultSetProxyImpl(statement, new MockResultSet(statement), 0, sql);
                }

                return chain.statement_executeQuery(statement, sql);
            }
        });
    }

    protected void tearDown() throws Exception {
        dataSource.close();
    }

    public void testValidate() throws Exception {
        returnEmptyCount.set(1);

        Exception error = null;
        try {
            dataSource.init();
        } catch (SQLException e) {
            error = e;
        }
        // 'SELECT 1' for connection validation will skip all filters, so error is null.
        Assert.assertNull(error);

        {
            Connection conn = dataSource.getConnection();
            Statement stmt = null;
            ResultSet rs = null;
            try {
                stmt = conn.createStatement();
                rs = stmt.executeQuery("select 1");
                // rs.next() should return false as result set is empty.
                if (!rs.next()) {
                    throw new SQLException("result is empty");
                }
            } catch (SQLException e) {
                error = e;
            } finally {
                JdbcUtils.close(rs);
                JdbcUtils.close(stmt);
            }
            conn.close();
        }
        Assert.assertNotNull(error);
        
        {
            returnEmptyCount.set(1);
            Connection conn = dataSource.getConnection();
            conn.close();
        }

        {
            returnEmptyCount.set(1);
            Connection conn = dataSource.getConnection();
            conn.close();
        }

        {
            errorCount.set(1);
            Connection conn = dataSource.getConnection();
            conn.close();
        }

        Connection conn = dataSource.getConnection();

        Statement stmt = conn.createStatement();
        Assert.assertEquals(100, stmt.getQueryTimeout());
        stmt.close();

        conn.close();
    }

}
