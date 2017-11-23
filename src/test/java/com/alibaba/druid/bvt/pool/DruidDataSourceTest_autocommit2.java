package com.alibaba.druid.bvt.pool;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.filter.FilterAdapter;
import com.alibaba.druid.filter.FilterChain;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.proxy.jdbc.ConnectionProxy;

/**
 * 这个场景测试defaultAutoCommit
 * 
 * @author wenshao [szujobs@hotmail.com]
 */
public class DruidDataSourceTest_autocommit2 extends TestCase {

    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setTestOnBorrow(false);
        dataSource.setDefaultAutoCommit(false);
        dataSource.setInitialSize(1);
        dataSource.getProxyFilters().add(new FilterAdapter() {
            public ConnectionProxy connection_connect(FilterChain chain, Properties info) throws SQLException {
                ConnectionProxy conn = chain.connection_connect(info);
                conn.setAutoCommit(true);
                return conn;
            }
        });

    }

    protected void tearDown() throws Exception {
        dataSource.close();
    }

    public void test_autoCommit() throws Exception {
        Connection conn = dataSource.getConnection();
        
        Assert.assertFalse(conn.getAutoCommit());
        
        conn.close();
    }
}
