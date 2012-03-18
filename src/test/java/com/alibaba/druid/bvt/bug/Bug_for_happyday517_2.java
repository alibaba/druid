package com.alibaba.druid.bvt.bug;

import java.sql.Connection;
import java.sql.DataTruncation;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.mock.MockConnection;
import com.alibaba.druid.mock.MockDriver;
import com.alibaba.druid.mock.MockPreparedStatement;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.stat.DruidDataSourceStatManager;

public class Bug_for_happyday517_2 extends TestCase {

    private DruidDataSource dataSource;
    private MockDriver      driver;

    final DataTruncation    exception = new java.sql.DataTruncation(0, true, true, 0, 0);

    protected void setUp() throws Exception {

        final MockPreparedStatement mockStatement = new MockPreparedStatement(null, null) {

            public boolean execute() throws SQLException {
                throw exception;
            }
        };

        driver = new MockDriver() {

            public Connection connect(String url, Properties info) throws SQLException {
                super.connect(url, info);
                return new MockConnection(driver, url, info) {

                    public PreparedStatement prepareStatement(String sql) throws SQLException {
                        return mockStatement;
                    }
                };
            }
        };
        dataSource = new DruidDataSource();
        dataSource.setDriver(driver);
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setFilters("stat,trace,log4j,encoding");

    }

    protected void tearDown() throws Exception {
        dataSource.close();
        Assert.assertEquals(0, DruidDataSourceStatManager.getInstance().getDataSourceList().size());
    }

    public void test_bug() throws Exception {
        Connection conn = dataSource.getConnection();

        PreparedStatement stmt = conn.prepareStatement("insert into message.dbo.TempSMS(sms) values ('333')");

        Exception error = null;
        try {
            stmt.execute();
        } catch (SQLException ex) {
            error = ex;
        }

        Assert.assertTrue(exception == error);

        stmt.close();

        conn.close();
    }
}
