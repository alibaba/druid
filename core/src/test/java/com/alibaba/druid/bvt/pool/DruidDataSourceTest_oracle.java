package com.alibaba.druid.bvt.pool;

import com.alibaba.druid.mock.MockDriver;
import com.alibaba.druid.pool.DruidDataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 这个场景测试minIdle > maxActive
 *
 * @author wenshao [szujobs@hotmail.com]
 */
public class DruidDataSourceTest_oracle {
    private DruidDataSource dataSource;

    @BeforeEach
    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setTestOnBorrow(false);
        dataSource.setFilters("stat");
        dataSource.setDbType("oracle");
        dataSource.setDriver(new MockDriver() {
            @Override
            public int getMajorVersion() {
                return 11;
            }
        });
    }

    @AfterEach
    protected void tearDown() throws Exception {
        dataSource.close();
    }

    @Test
    public void test_error() throws Exception {
        dataSource.init();
        assertTrue(dataSource.isOracle());
    }
}
