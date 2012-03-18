package com.alibaba.druid.pool.bonecp;

import java.lang.reflect.Field;
import java.sql.Connection;

import javax.sql.DataSource;

import junit.framework.TestCase;

import org.apache.commons.dbcp.BasicDataSource;
import org.logicalcobwebs.proxool.ProxoolDataSource;

import com.alibaba.druid.mock.MockConnection;
import com.alibaba.druid.pool.DruidDataSource;
import com.jolbox.bonecp.BoneCPDataSource;
import com.jolbox.bonecp.ConnectionHandle;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.mchange.v2.c3p0.impl.NewProxyConnection;

public class TestLRU extends TestCase {

    public void f_test_boneCP() throws Exception {
        BoneCPDataSource ds = new BoneCPDataSource();
        ds.setJdbcUrl("jdbc:mock:test");
        ds.setPartitionCount(1);
        ds.setMaxConnectionsPerPartition(10);
        ds.setMinConnectionsPerPartition(0);

        for (int i = 0; i < 10; ++i) {
            f(ds, 5);
            System.out.println("--------------------------------------------");
        }
    }

    public void f_test_druid() throws Exception {
        DruidDataSource ds = new DruidDataSource();
        ds.setUrl("jdbc:mock:test");
        ds.setMaxIdle(10);

        for (int i = 0; i < 10; ++i) {
            f(ds, 5);
            System.out.println("--------------------------------------------");
        }
    }
    
    public void f_test_dbcp() throws Exception {
        BasicDataSource ds = new BasicDataSource();
        ds.setUrl("jdbc:mock:test");
        ds.setMaxIdle(10);
        
        for (int i = 0; i < 10; ++i) {
            f(ds, 5);
            System.out.println("--------------------------------------------");
        }
    }
    
    public void f_test_c3p0() throws Exception {
        ComboPooledDataSource ds = new ComboPooledDataSource();
        ds.setJdbcUrl("jdbc:mock:test");
        ds.setMaxPoolSize(10);
        ds.setMinPoolSize(0);
        
        for (int i = 0; i < 10; ++i) {
            f(ds, 5);
            System.out.println("--------------------------------------------");
        }
    }
    
    public void f_test_proxool() throws Exception {
        ProxoolDataSource ds = new ProxoolDataSource();
        ds.setDriver("com.alibaba.druid.mock.MockDriver");
        ds.setDriverUrl("jdbc:mock:test");
        ds.setMaximumConnectionCount(10);
        ds.setMinimumConnectionCount(0);
        ds.setUser("user");
        ds.setPassword("password");
        
        for (int i = 0; i < 10; ++i) {
            f(ds, 5);
            System.out.println("--------------------------------------------");
        }
    }
    
//    public void test_jboss() throws Exception {
//        LocalTxDataSource ds = new LocalTxDataSource();
//        ds.setDriverClass("com.alibaba.druid.mock.MockDriver");
//        ds.setConnectionURL("jdbc:mock:test");
//        ds.setMaxSize(10);
//        ds.setMinSize(0);
//        ds.setUserName("user");
//        ds.setPassword("password");
//        
//        for (int i = 0; i < 10; ++i) {
//            f(ds, 5);
//            System.out.println("--------------------------------------------");
//        }
//    }

    public static void f(DataSource ds, int count) throws Exception {
        Connection[] connections = new Connection[count];
        for (int i = 0; i < count; ++i) {
            connections[i] = ds.getConnection();
        }

        for (int i = 0; i < count; ++i) {
            System.out.println(unwrap(connections[i]).getId());
        }

        for (int i = count - 1; i >= 0; --i) {
            connections[i].close();
        }
    }

    public static MockConnection unwrap(Connection conn) throws Exception {
        if (conn instanceof ConnectionHandle) {
            ConnectionHandle handle = (ConnectionHandle) conn;
            return (MockConnection) handle.getInternalConnection();
        }
        if (conn instanceof NewProxyConnection) {
            NewProxyConnection handle = (NewProxyConnection) conn;
            
            Field field = NewProxyConnection.class.getDeclaredField("inner");
            field.setAccessible(true);
            return (MockConnection) field.get(handle);
        }
        
        return conn.unwrap(MockConnection.class);
    }

}
