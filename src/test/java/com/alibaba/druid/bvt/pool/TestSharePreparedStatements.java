package com.alibaba.druid.bvt.pool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.mock.MockPreparedStatement;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.stat.DruidDataSourceStatManager;

public class TestSharePreparedStatements extends TestCase {

    protected void setUp() throws Exception {
        Assert.assertEquals(0, DruidDataSourceStatManager.getInstance().getDataSourceList().size());
    }

    protected void tearDown() throws Exception {
        Assert.assertEquals(0, DruidDataSourceStatManager.getInstance().getDataSourceList().size());
    }

    public void test_sharePreparedStatements() throws Exception {

        // sharePreparedStatements

        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setMaxPoolPreparedStatementPerConnectionSize(30);

        String sql = "SELECT 1";

        MockPreparedStatement mockStmt = null;
        {
            Connection conn = dataSource.getConnection();

            PreparedStatement stmt = conn.prepareStatement(sql);
            mockStmt = stmt.unwrap(MockPreparedStatement.class);
            ResultSet rs = stmt.executeQuery();
            rs.next();
            rs.close();
            stmt.close();

            conn.close();
        }
        
        {
            Connection conn = dataSource.getConnection();

            PreparedStatement stmt = conn.prepareStatement(sql);
            Assert.assertEquals(mockStmt, stmt.unwrap(MockPreparedStatement.class));
            ResultSet rs = stmt.executeQuery();
            rs.next();
            rs.close();
            stmt.close();

            conn.close();
        }

        {
            Connection conn = dataSource.getConnection();
            conn.setAutoCommit(false);

            PreparedStatement stmt = conn.prepareStatement(sql);
            Assert.assertEquals(true, mockStmt != stmt.unwrap(MockPreparedStatement.class));
            ResultSet rs = stmt.executeQuery();
            rs.next();
            rs.close();
            stmt.close();

            conn.close();
        }
        
        {
            Connection conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            
            PreparedStatement stmt = conn.prepareStatement(sql);
            Assert.assertEquals(true, mockStmt != stmt.unwrap(MockPreparedStatement.class));
            ResultSet rs = stmt.executeQuery();
            rs.next();
            rs.close();
            stmt.close();
            
            conn.close();
        }

        dataSource.close();
    }
}
