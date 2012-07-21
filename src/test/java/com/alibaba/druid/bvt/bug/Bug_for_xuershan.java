package com.alibaba.druid.bvt.bug;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.mock.MockDriver;
import com.alibaba.druid.mock.MockPreparedStatement;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.util.JdbcUtils;

public class Bug_for_xuershan extends TestCase {

    private DruidDataSource dataSource;
    private MockDriver      driver;

    protected void setUp() throws Exception {
        driver = new MockDriver() {
            protected ResultSet createResultSet(MockPreparedStatement stmt) {
                return null;
            }
        };
        
        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xx");
        dataSource.setDriver(driver);
    }
    
    protected void tearDown() throws Exception {
        JdbcUtils.close(dataSource);
    }

    public void test_bug_for_xuershan() throws Exception {
        String sql = "select 1";
        Connection conn = dataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.execute();
        Assert.assertNull(stmt.getResultSet());
        stmt.close();
        conn.close();
    }
}
