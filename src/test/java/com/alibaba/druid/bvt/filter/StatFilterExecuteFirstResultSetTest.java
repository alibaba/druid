package com.alibaba.druid.bvt.filter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.mock.MockConnection;
import com.alibaba.druid.mock.MockDriver;
import com.alibaba.druid.mock.MockPreparedStatement;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.stat.JdbcSqlStat;
import com.alibaba.druid.util.Histogram;
import com.alibaba.druid.util.JdbcUtils;

public class StatFilterExecuteFirstResultSetTest extends TestCase {

    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();

        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setFilters("stat");
        dataSource.setTestOnBorrow(false);

        MockDriver driver = new MockDriver() {

            public MockPreparedStatement createMockPreparedStatement(MockConnection conn, String sql) {
                return new MyMockPreparedStatement(conn, sql);
            }
        };

        dataSource.setDriver(driver);

        dataSource.init();
    }

    protected void tearDown() throws Exception {
        JdbcUtils.close(dataSource);
    }

    public void test_stat() throws Exception {

        Assert.assertTrue(dataSource.isInited());
        final String sql = "select 1";

        Connection conn = dataSource.getConnection();

        PreparedStatement stmt = conn.prepareStatement(sql);
        boolean firstResult = stmt.execute();
        Assert.assertTrue(firstResult);

        ResultSet rs = stmt.getResultSet();
        rs.next();
        rs.close();

        stmt.close();

        conn.close();

        JdbcSqlStat sqlStat = dataSource.getDataSourceStat().getSqlStat(sql);

        Histogram histogram = sqlStat.getHistogram();
        Assert.assertEquals(1,
                            histogram.getValue(0) + histogram.getValue(1) + histogram.getValue(2)
                                    + histogram.getValue(3));

        Histogram rsHoldHistogram = sqlStat.getExecuteAndResultHoldTimeHistogram();

        Assert.assertEquals(1, rsHoldHistogram.getValue(0) + rsHoldHistogram.getValue(1) + rsHoldHistogram.getValue(2)
                               + rsHoldHistogram.getValue(3));
    }

    static class MyMockPreparedStatement extends MockPreparedStatement {

        public MyMockPreparedStatement(MockConnection conn, String sql){
            super(conn, sql);
        }

        public boolean execute() throws SQLException {
            return true;
        }

        public ResultSet getResultSet() throws SQLException {
            return getConnection().getDriver().executeQuery(this, getSql());
        }
    }
}
