package com.alibaba.druid.pool.benckmark;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.NumberFormat;
import java.util.concurrent.CountDownLatch;

import javax.sql.DataSource;

import junit.framework.TestCase;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.util.Assert;

import com.alibaba.druid.TestUtil;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.util.JdbcUtils;

public class Oracle_Case4 extends TestCase {

    private String       jdbcUrl;
    private String       user;
    private String       password;
    private String       driverClass;
    private int          maxIdle                = 40;
    private int          maxActive              = 50;
    private int          maxWait                = 5000;
    private String       validationQuery        = "SELECT 1 FROM DUAL";
    private int          threadCount            = 1;
    private int          loopCount              = 3;
    final int            LOOP_COUNT             = 1000 * 1;
    private boolean      testOnBorrow           = false;
    private boolean      preparedStatementCache = true;

    private final String SQL                    = "SELECT MEMBER_ID FROM WP_ORDERS WHERE ID = ?";

    protected void setUp() throws Exception {
        jdbcUrl = "jdbc:oracle:thin:@10.20.149.85:1521:ocnauto";
        user = "alibaba";
        password = "ccbuauto";
        driverClass = "oracle.jdbc.driver.OracleDriver";
    }

    public void test_0() throws Exception {
        DruidDataSource dataSource = new DruidDataSource();

        dataSource.setMaxActive(maxActive);
        dataSource.setMaxIdle(maxIdle);
        dataSource.setMaxWait(maxWait);
        dataSource.setPoolPreparedStatements(preparedStatementCache);
        dataSource.setMaxOpenPreparedStatements(10);
        dataSource.setDriverClassName(driverClass);
        dataSource.setUrl(jdbcUrl);
        dataSource.setUsername(user);
        dataSource.setPassword(password);
        dataSource.setValidationQuery(validationQuery);
        dataSource.setTestOnBorrow(testOnBorrow);

        // printTables(dataSource);
        // printWP_ORDERS(dataSource);

        for (int i = 0; i < loopCount; ++i) {
            p0(dataSource, "druid", threadCount);
        }
        System.out.println();
    }

    public void test_1() throws Exception {
        final BasicDataSource dataSource = new BasicDataSource();

        dataSource.setMaxActive(maxActive);
        dataSource.setMaxIdle(maxIdle);
        dataSource.setMaxWait(maxWait);
        dataSource.setPoolPreparedStatements(preparedStatementCache);
        dataSource.setMaxOpenPreparedStatements(10);
        dataSource.setDriverClassName(driverClass);
        dataSource.setUrl(jdbcUrl);
        dataSource.setUsername(user);
        dataSource.setPassword(password);
        dataSource.setValidationQuery(validationQuery);
        dataSource.setTestOnBorrow(testOnBorrow);

        for (int i = 0; i < loopCount; ++i) {
            p0(dataSource, "dbcp", threadCount);
        }
        System.out.println();
    }

    private void printWP_ORDERS(DruidDataSource dataSource) throws SQLException {
        Connection conn = dataSource.getConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM WP_ORDERS");

        JdbcUtils.printResultSet(rs);

        rs.close();
        stmt.close();
        conn.close();
    }

    protected void printTables(DruidDataSource dataSource) throws SQLException {
        Connection conn = dataSource.getConnection();

        ResultSet rs = conn.getMetaData().getTables(null, "ALIBABA", null, new String[] { "TABLE" });
        JdbcUtils.printResultSet(rs);
        rs.close();

        conn.close();
    }

    private void p0(final DataSource dataSource, String name, int threadCount) throws Exception {

        final CountDownLatch startLatch = new CountDownLatch(1);
        final CountDownLatch endLatch = new CountDownLatch(threadCount);
        for (int i = 0; i < threadCount; ++i) {
            Thread thread = new Thread() {

                public void run() {
                    try {
                        startLatch.await();

                        for (int i = 0; i < LOOP_COUNT; ++i) {
                            Connection conn = dataSource.getConnection();
                            String sql = SQL; // + " AND ROWNUM <= " + (i % 20 + 1);
                            PreparedStatement stmt = conn.prepareStatement(sql);
                            stmt.setInt(1, 337);
                            ResultSet rs = stmt.executeQuery();
                            while (rs.next()) {

                            }
                            // Assert.isTrue(!rs.isClosed());
                            rs.close();
                            // Assert.isTrue(!stmt.isClosed());
                            stmt.close();
                            Assert.isTrue(stmt.isClosed());
                            conn.close();
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    endLatch.countDown();
                }
            };
            thread.start();
        }
        long startMillis = System.currentTimeMillis();
        long startYGC = TestUtil.getYoungGC();
        long startFullGC = TestUtil.getFullGC();
        startLatch.countDown();
        endLatch.await();

        long millis = System.currentTimeMillis() - startMillis;
        long ygc = TestUtil.getYoungGC() - startYGC;
        long fullGC = TestUtil.getFullGC() - startFullGC;

        System.out.println("thread " + threadCount + " " + name + " millis : "
                           + NumberFormat.getInstance().format(millis) + ", YGC " + ygc + " FGC " + fullGC);
    }
}
