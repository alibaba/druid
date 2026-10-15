package com.alibaba.druid.bvt.pool;

import com.alibaba.druid.pool.DruidDataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 这个场景测试initialSize > maxActive
 *
 * @author wenshao [szujobs@hotmail.com]
 */
public class DruidDataSourceTest_getProperties {
    private DruidDataSource dataSource;

    @BeforeEach
    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setTestOnBorrow(false);
        dataSource.getConnectProperties().put("user", "jobs");
        dataSource.getConnectProperties().put("password", "xxx");
        dataSource.init();
    }

    @AfterEach
    protected void tearDown() throws Exception {
        dataSource.close();
    }

    @Test
    public void test_error() throws Exception {
        assertEquals(-1, dataSource.getProperties().indexOf("xxx"));
        assertEquals(true, dataSource.getProperties().indexOf("******") != -1);
    }
}
