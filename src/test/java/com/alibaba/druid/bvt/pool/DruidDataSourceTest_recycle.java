package com.alibaba.druid.bvt.pool;


import java.sql.Statement;

import junit.framework.TestCase;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;

/**
 * 这个场景测试initialSize > maxActive
 * 
 * @author wenshao [szujobs@hotmail.com]
 */
public class DruidDataSourceTest_recycle extends TestCase {

    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setTestOnBorrow(false);
        dataSource.setMaxActive(1);
    }

    protected void tearDown() throws Exception {
        dataSource.close();
    }

    public void test_recycle() throws Exception {
        DruidPooledConnection conn = dataSource.getConnection();
        conn.setAutoCommit(false);
        conn.setReadOnly(false);
        
        Statement stmt = conn.createStatement();
        stmt.execute("select 1");
        
        conn.close();
    }
}
