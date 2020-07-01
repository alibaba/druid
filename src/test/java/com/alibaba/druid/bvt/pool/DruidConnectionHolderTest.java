package com.alibaba.druid.bvt.pool;

import com.alibaba.druid.PoolTestCase;
import com.alibaba.druid.filter.FilterAdapter;
import com.alibaba.druid.filter.FilterChain;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.proxy.jdbc.ConnectionProxy;
import com.mysql.jdbc.Driver;
import junit.framework.TestCase;

import java.sql.SQLException;

public class DruidConnectionHolderTest extends PoolTestCase {

    Driver driver;
    private DruidDataSource dataSource;

    private Class exceptionClass;

    protected void setUp() throws Exception {
        super.setUp();

        driver = new Driver();

        if (driver.getMajorVersion() == 5) {
            exceptionClass = Class.forName("com.mysql.jdbc.exceptions.jdbc4.MySQLSyntaxErrorException");

            dataSource = new DruidDataSource();
            dataSource.setUrl("jdbc:mock:xxx");
            dataSource.setTestOnBorrow(false);
            dataSource.setInitialSize(1);
            dataSource.getProxyFilters().add(new FilterAdapter() {

                public int connection_getTransactionIsolation(FilterChain chain, ConnectionProxy connection)
                        throws SQLException {
                    throw createSyntaxException();
                }
            });
        }
    }

    protected SQLException createSyntaxException() {
        try {
            return (SQLException) exceptionClass.newInstance();
        } catch (Exception e) {
            throw new IllegalStateException();
        }
    }

    protected void tearDown() throws Exception {
        if (dataSource != null) {
            dataSource.close();
        }

        super.tearDown();
    }

    public void test_mysqlSyntaxError() throws Exception {
        if (driver.getMajorVersion() == 5) {
            dataSource.init();

            dataSource.getConnection();
        }
    }
}
