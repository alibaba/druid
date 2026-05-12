package com.alibaba.druid.bvt.pool;

import com.alibaba.druid.pool.DruidDataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 这个场景测试defaultAutoCommit
 *
 * @author wenshao [szujobs@hotmail.com]
 */
public class DruidDataSourceTest_getConnection {
    private DruidDataSource dataSource;

    @BeforeEach
    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setTestOnBorrow(false);
    }

    @AfterEach
    protected void tearDown() throws Exception {
        dataSource.close();
    }

    @Test
    public void test_conn_ok() throws Exception {
        Connection conn = dataSource.getConnection(null, null);
        conn.close();
    }

    @Test
    public void test_conn_user_error() throws Exception {
        Exception error = null;
        try {
            dataSource.getConnection("a", null);
        } catch (UnsupportedOperationException e) {
            error = e;
        }
        assertNotNull(error);
    }

    @Test
    public void test_conn_password_error() throws Exception {
        Exception error = null;
        try {
            dataSource.getConnection(null, "a");
        } catch (UnsupportedOperationException e) {
            error = e;
        }
        assertNotNull(error);
    }
}
