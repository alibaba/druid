package com.alibaba.druid.oracle;

import com.alibaba.druid.DbTestCase;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;
import com.alibaba.druid.util.JdbcUtils;
import junit.framework.TestCase;
import oracle.jdbc.driver.OracleConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class OracleKillSessionTest extends DbTestCase {
    private final static Log LOG                     = LogFactory.getLog(OracleKillSessionTest.class);

    private ScheduledExecutorService scheduler;

    public OracleKillSessionTest() {
        super("pool_config/oracle_db_sonar.properties");
    }

    protected void setUp() throws Exception {
        super.setUp();

        scheduler = Executors.newScheduledThreadPool(10);
    }

    public void test_connect() throws Exception {
        for (int i = 0; i < 100; ++i) {
            runSql();
        }
//
//        for (int i = 0; i < 5; ++i) {
//            Task task = new Task();
//            scheduler.scheduleWithFixedDelay(task, 0, 30, TimeUnit.SECONDS);
//        }
//
        Thread.sleep(1000 * 1000);
    }

    public void runSql() throws Exception {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            OracleConnection unwrapped = (OracleConnection) conn.unwrap(Connection.class);

            pstmt = conn.prepareStatement("select 1 from dual");
            rs = pstmt.executeQuery();
            JdbcUtils.printResultSet(rs);
        } finally {
             JdbcUtils.close(rs);
             JdbcUtils.close(pstmt);
             JdbcUtils.close(conn);
        }
    }


    class Task implements Runnable {
        @Override
        public void run() {
            try {
                Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement("select 1 from dual");
                ResultSet rs = pstmt.executeQuery();
                JdbcUtils.printResultSet(rs);
                rs.close();
                pstmt.close();
                conn.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
