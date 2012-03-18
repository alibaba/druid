package com.alibaba.druid.bvt.pool;

import java.sql.Connection;
import java.sql.PreparedStatement;

import org.junit.Assert;

import junit.framework.TestCase;

import com.alibaba.druid.mock.MockConnection;
import com.alibaba.druid.mock.MockConnectionClosedException;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.stat.DruidDataSourceStatManager;

public class Bug_for_dupCloseStmtError extends TestCase {

    protected DruidDataSource dataSource;

    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:");
        dataSource.setPoolPreparedStatements(false);
        dataSource.setTestOnBorrow(true);
    }

    protected void tearDown() throws Exception {
        dataSource.close();
        for (DruidDataSource dataSource : DruidDataSourceStatManager.getDruidDataSourceInstances()) {
            dataSource.close();
        }
    }

    public void test_2() throws Exception {
        {
            Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement("select 1");
            stmt.setString(1, "xx");

            MockConnection mockConn = conn.unwrap(MockConnection.class);
            mockConn.close();

            MockConnectionClosedException error = null;
            try {
                stmt.execute();
            } catch (MockConnectionClosedException ex) {
                error = ex;
            }
            
            Assert.assertNotNull(error);
            
            conn.close();
            stmt.close();
        }
        {
            Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement("select 1");
            stmt.close();
            conn.close();
        }
        Assert.assertEquals(0, dataSource.getDupCloseCount());
    }
}
