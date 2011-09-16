package com.alibaba.druid.bvt.pool.basic;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Properties;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.mock.MockDriver;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.PoolableConnection;
import com.alibaba.druid.stat.DruidDataSourceStatManager;

public class ConnectionTest3 extends TestCase {

    private MockDriver      driver;
    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        Assert.assertEquals(0, DruidDataSourceStatManager.getInstance().getDataSourceList().size());

        driver = new MockDriver();

        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setDriver(driver);
        dataSource.setInitialSize(1);
        dataSource.setMaxActive(2);
        dataSource.setMaxIdle(2);
        dataSource.setMinIdle(1);
        dataSource.setMinEvictableIdleTimeMillis(300 * 1000); // 300 / 10
        dataSource.setTimeBetweenEvictionRunsMillis(180 * 1000); // 180 / 10
        dataSource.setTestWhileIdle(true);
        dataSource.setTestOnBorrow(false);
        dataSource.setValidationQuery("SELECT 1");
        dataSource.setFilters("stat,trace");
    }

    protected void tearDown() throws Exception {
        dataSource.close();
        Assert.assertEquals(0, DruidDataSourceStatManager.getInstance().getDataSourceList().size());
    }

    public void test_basic() throws Exception {
        PoolableConnection conn = (PoolableConnection) dataSource.getConnection();
        
        conn.getStartTransactionTimeMillis();
        conn.getMetaData();
        conn.setReadOnly(true);
        Assert.assertEquals(true, conn.isReadOnly());
        
        conn.setCatalog("xxx");
        Assert.assertEquals("xxx", conn.getCatalog());
        
        conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
        Assert.assertEquals(Connection.TRANSACTION_READ_COMMITTED, conn.getTransactionIsolation());
        
        conn.getWarnings();
        conn.clearWarnings();
        conn.getTypeMap();
        conn.setTypeMap(null);
        
        conn.setHoldability(ResultSet.CLOSE_CURSORS_AT_COMMIT);
        Assert.assertEquals(ResultSet.CLOSE_CURSORS_AT_COMMIT, conn.getHoldability());
        
        conn.setSavepoint();
        conn.setSavepoint("savepoint");
        conn.rollback();
        conn.rollback(null);
        conn.releaseSavepoint(null);
        conn.createBlob();
        conn.createClob();
        conn.createNClob();
        conn.createSQLXML();
        conn.isValid(200);
        conn.setClientInfo(new Properties());
        conn.setClientInfo("xx", "11");
        conn.getClientInfo("xx");
        conn.getClientInfo();
        
        conn.createArrayOf("int", new Object[0]);
        conn.createStruct("int", new Object[0]);
        
        conn.addConnectionEventListener(null);
        conn.removeConnectionEventListener(null);
        conn.addStatementEventListener(null);
        conn.removeStatementEventListener(null);
        
        conn.close();
    }
    
 
}
