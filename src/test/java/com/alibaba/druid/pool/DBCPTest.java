package com.alibaba.druid.pool;

import java.sql.CallableStatement;
import java.sql.Connection;

import junit.framework.TestCase;

import org.apache.commons.dbcp.BasicDataSource;

import com.alibaba.druid.mock.MockDriver;

public class DBCPTest extends TestCase {

    public void test_dbcp() throws Exception {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName(MockDriver.class.getName());
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setMaxOpenPreparedStatements(100);
        dataSource.setPoolPreparedStatements(true);

        final String sql = "selelct 1";
        {
            Connection conn = dataSource.getConnection();
            CallableStatement stmt = conn.prepareCall(sql);
            stmt.close();
            conn.close();
        }
        {
            Connection conn = dataSource.getConnection();
            CallableStatement stmt = conn.prepareCall(sql);
            stmt.close();
            conn.close();
        }
    }
}
