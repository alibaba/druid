package com.alibaba.druid.bvt.pool;

import com.alibaba.druid.pool.DataSourceDisableException;
import com.alibaba.druid.pool.DruidDataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 这个场景测试minIdle > maxActive
 *
 * @author wenshao [szujobs@hotmail.com]
 */
public class DruidDataSourceTest_enable {
    private DruidDataSource dataSource;

    @BeforeEach
    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setTestOnBorrow(false);
        dataSource.setMaxWait(1000);
    }

    @AfterEach
    protected void tearDown() throws Exception {
        dataSource.close();
    }

    @Test
    public void test_disable() throws Exception {
        {
            Connection conn = dataSource.getConnection();
            conn.close();
        }
        assertTrue(dataSource.isEnable());

        dataSource.setEnable(false);

        assertFalse(dataSource.isEnable());

        dataSource.shrink();

        Exception error = null;
        try {
            Connection conn = dataSource.getConnection();
            conn.close();
        } catch (DataSourceDisableException e) {
            error = e;
        }
        assertNotNull(error);
    }

    @Test
    public void test_disable_() throws Exception {
        dataSource.setEnable(false);

        assertFalse(dataSource.isEnable());

        Exception error = null;
        try {
            Connection conn = dataSource.getConnection();
            conn.close();
        } catch (DataSourceDisableException e) {
            error = e;
        }
        assertNotNull(error);
    }
}
