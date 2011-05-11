package com.alibaba.druid.pool.benckmark;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.NumberFormat;

import javax.sql.DataSource;

import junit.framework.TestCase;

import org.apache.commons.dbcp.BasicDataSource;

import com.alibaba.druid.pool.DruidDataSource;

public class Case0 extends TestCase {

    private String jdbcUrl;
    private String user;
    private String password;
    private String driverClass;
    private int    initialSize = 10;
    private int    minPoolSize = 1;
    private int    maxPoolSize = 2;
    private int    maxActive   = 2;

    protected void setUp() throws Exception {
        jdbcUrl = "jdbc:fake:dragoon_v25masterdb";
        user = "dragoon25";
        password = "dragoon25";
        driverClass = "com.alibaba.druid.mock.MockDriver";
    }

    public void test_0() throws Exception {
        DruidDataSource dataSource = new DruidDataSource();

        dataSource.setInitialSize(initialSize);
        dataSource.setMaxActive(maxActive);
        dataSource.setMinIdle(minPoolSize);
        dataSource.setMaxIdle(maxPoolSize);
        dataSource.setPoolPreparedStatements(true);
        dataSource.setDriverClass(driverClass);
        dataSource.setUrl(jdbcUrl);
        dataSource.setPoolPreparedStatements(true);
        dataSource.setUser(user);
        dataSource.setPassword(password);

        for (int i = 0; i < 10; ++i) {
            p0(dataSource);
        }
    }
    
    public void test_1() throws Exception {
        final BasicDataSource dataSource = new BasicDataSource();

        dataSource.setInitialSize(initialSize);
        dataSource.setMaxActive(maxActive);
        dataSource.setMinIdle(minPoolSize);
        dataSource.setMaxIdle(maxPoolSize);
        dataSource.setPoolPreparedStatements(true);
        dataSource.setDriverClassName(driverClass);
        dataSource.setUrl(jdbcUrl);
        dataSource.setPoolPreparedStatements(true);
        dataSource.setUsername(user);
        dataSource.setPassword(password);

        for (int i = 0; i < 10; ++i) {
            p0(dataSource);
        }
    }

    private void p0(DataSource dataSource) throws SQLException {
        long startMillis = System.currentTimeMillis();
        final int COUNT = 1000 * 1000;
        for (int i = 0; i < COUNT; ++i) {
            Connection conn = dataSource.getConnection();
            conn.close();
        }
        long millis = System.currentTimeMillis() - startMillis;
        System.out.println(dataSource.getClass().getName() + " millis : " + NumberFormat.getInstance().format(millis));
    }
}
