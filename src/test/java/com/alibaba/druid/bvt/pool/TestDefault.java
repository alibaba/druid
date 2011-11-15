package com.alibaba.druid.bvt.pool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.stat.DruidDataSourceStatManager;

public class TestDefault extends TestCase {

    protected void setUp() throws Exception {
        Assert.assertEquals(0, DruidDataSourceStatManager.getInstance().getDataSourceList().size());
    }

    protected void tearDown() throws Exception {
        Assert.assertEquals(0, DruidDataSourceStatManager.getInstance().getDataSourceList().size());
    }

    public void test_close() throws Exception {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xxx");

        String sql = "SELECT 1";
        {
            Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            rs.next();

            Assert.assertEquals(0, conn.getHoldability());
            Assert.assertEquals(false, conn.isReadOnly());
            Assert.assertEquals(0, conn.getTransactionIsolation());

            conn.setHoldability(ResultSet.HOLD_CURSORS_OVER_COMMIT);
            conn.setReadOnly(false);
            conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);

            conn.close();

            Assert.assertEquals(true, stmt.isClosed());
            Assert.assertEquals(true, rs.isClosed());

            rs.close();
            stmt.close();
        }

        Connection conn = dataSource.getConnection();
        
        Assert.assertEquals(0, conn.getHoldability());
        Assert.assertEquals(false, conn.isReadOnly());
        Assert.assertEquals(0, conn.getTransactionIsolation());

        conn.close();

        dataSource.close();
    }
}
