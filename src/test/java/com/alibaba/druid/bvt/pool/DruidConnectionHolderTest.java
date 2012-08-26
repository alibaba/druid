package com.alibaba.druid.bvt.pool;

import java.sql.Connection;
import java.sql.SQLException;

import junit.framework.TestCase;

import com.alibaba.druid.filter.FilterAdapter;
import com.alibaba.druid.filter.FilterChain;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.proxy.jdbc.ConnectionProxy;

public class DruidConnectionHolderTest extends TestCase {

    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setTestOnBorrow(false);
        dataSource.setPoolPreparedStatements(true);
        dataSource.getProxyFilters().add(new FilterAdapter() {

            public int connection_getTransactionIsolation(FilterChain chain, ConnectionProxy connection)
                                                                                                        throws SQLException {
                throw new com.mysql.jdbc.exceptions.jdbc4.MySQLSyntaxErrorException();
            }
        });

    }

    protected void tearDown() throws Exception {
        dataSource.close();
    }

    public void test_mysqlSyntaxError() throws Exception {
        Connection conn = dataSource.getConnection();
        conn.close();
    }
}
