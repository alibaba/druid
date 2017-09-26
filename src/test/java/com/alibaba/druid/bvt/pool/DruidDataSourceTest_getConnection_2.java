package com.alibaba.druid.bvt.pool;

import com.alibaba.druid.pool.DruidDataSource;
import junit.framework.TestCase;
import org.junit.Assert;

import java.sql.Connection;

/**
 * 这个场景测试defaultAutoCommit
 * 
 * @author wenshao [szujobs@hotmail.com]
 */
public class DruidDataSourceTest_getConnection_2 extends TestCase {

    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setTestOnBorrow(false);

    }

    protected void tearDown() throws Exception {
        dataSource.close();
    }

    public void test_conn_ok() throws Exception {
        {
            Connection conn = dataSource.getConnection("usr1", "pwd1");
            conn.close();
        }
        {
            Connection conn = dataSource.getConnection("usr1", "pwd1");
            conn.close();
        }
    }
}
