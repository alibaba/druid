package com.alibaba.druid.pool.benckmark;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.NumberFormat;
import java.util.concurrent.CountDownLatch;

import javax.sql.DataSource;

import junit.framework.TestCase;

import org.apache.commons.dbcp.BasicDataSource;

import com.alibaba.druid.TestUtil;
import com.alibaba.druid.pool.DruidDataSource;

public class Case3 extends TestCase {

    private String  jdbcUrl;
    private String  user;
    private String  password;
    private String  driverClass;
    private int     maxIdle              = 40;
    private int     maxActive            = 50;
    private int     maxWait              = -1;
    private String  validationQuery      = null;                                                                                      // "SELECT 1";
    private int     threadCount          = 40;
    private int     TEST_COUNT           = 3;
    final int       LOOP_COUNT           = 1000 * 10;
    private boolean testOnBorrow         = true;
    private String  connectionProperties = ""; //"bigStringTryClob=true;clientEncoding=GBK;defaultRowPrefetch=50;serverEncoding=ISO-8859-1";
    private String  sql                  = "SELECT 1";
    

    protected void setUp() throws Exception {
//        jdbcUrl = "jdbc:fake:dragoon_v25masterdb";
//        user = "dragoon25";
//        password = "dragoon25";
//        driverClass = "com.alibaba.druid.mock.MockDriver";

        jdbcUrl = "jdbc:mysql://10.20.153.104:3306/druid2";
        user = "root";
        password = "root";
    }

    public void test_perf() throws Exception {
        for (int i = 0; i < 10; ++i) {
            druid();
            dbcp();
        }
    }

    public void druid() throws Exception {
        DruidDataSource dataSource = new DruidDataSource();

        dataSource.setMaxActive(maxActive);
        dataSource.setMaxIdle(maxIdle);
        dataSource.setMaxWait(maxWait);
        dataSource.setPoolPreparedStatements(true);
        dataSource.setDriverClassName(driverClass);
        dataSource.setUrl(jdbcUrl);
        dataSource.setPoolPreparedStatements(true);
        dataSource.setUsername(user);
        dataSource.setPassword(password);
        dataSource.setValidationQuery(validationQuery);
        dataSource.setTestOnBorrow(testOnBorrow);
        dataSource.setConnectionProperties(connectionProperties);

        for (int i = 0; i < TEST_COUNT; ++i) {
            p0(dataSource, "druid", threadCount);
        }
        dataSource.close();
        System.out.println();
    }

    public void dbcp() throws Exception {
        final BasicDataSource dataSource = new BasicDataSource();

        dataSource.setMaxActive(maxActive);
        dataSource.setMaxIdle(maxIdle);
        dataSource.setMaxWait(maxWait);
        dataSource.setPoolPreparedStatements(true);
        dataSource.setDriverClassName(driverClass);
        dataSource.setUrl(jdbcUrl);
        dataSource.setPoolPreparedStatements(true);
        dataSource.setUsername(user);
        dataSource.setPassword(password);
        dataSource.setValidationQuery(validationQuery);
        dataSource.setTestOnBorrow(testOnBorrow);
        dataSource.setConnectionProperties(connectionProperties);

        for (int i = 0; i < TEST_COUNT; ++i) {
            p0(dataSource, "dbcp", threadCount);
        }
        // dataSource.close();
        System.out.println();
    }

    private void p0(final DataSource dataSource, String name, int threadCount) throws Exception {

        final CountDownLatch startLatch = new CountDownLatch(1);
        final CountDownLatch endLatch = new CountDownLatch(1);
        for (int i = 0; i < threadCount; ++i) {
            Thread thread = new Thread() {

                public void run() {
                    try {
                        startLatch.await();

                        for (int i = 0; i < LOOP_COUNT; ++i) {
                            Connection conn = dataSource.getConnection();
                            Statement stmt = conn.createStatement();
                            ResultSet rs = stmt.executeQuery(sql);
                            while (rs.next()) {
                                rs.getInt(1);
                            }
                            rs.close();
                            stmt.close();

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

        System.out.println("thread " + threadCount + " " + name + " millis : " + NumberFormat.getInstance().format(millis) + ", YGC " + ygc + " FGC " + fullGC);
    }
}
