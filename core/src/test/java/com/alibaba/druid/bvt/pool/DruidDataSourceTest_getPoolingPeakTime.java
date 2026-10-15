package com.alibaba.druid.bvt.pool;

import com.alibaba.druid.pool.DruidDataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 这个场景测试initialSize > maxActive
 *
 * @author wenshao [szujobs@hotmail.com]
 */
public class DruidDataSourceTest_getPoolingPeakTime {
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
    public void test_error() throws Exception {
        assertNull(dataSource.getPoolingPeakTime());
        assertNull(dataSource.getActivePeakTime());

        Connection conn = dataSource.getConnection();
        conn.close();

        assertNotNull(dataSource.getPoolingPeakTime());
        assertNotNull(dataSource.getActivePeakTime());
    }
}
