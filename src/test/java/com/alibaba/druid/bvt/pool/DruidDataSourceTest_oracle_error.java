package com.alibaba.druid.bvt.pool;

import org.junit.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.mock.MockDriver;
import com.alibaba.druid.pool.DruidDataSource;

/**
 * 这个场景测试minIdle > maxActive
 * 
 * @author wenshao [szujobs@hotmail.com]
 */
public class DruidDataSourceTest_oracle_error extends TestCase {

    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setTestOnBorrow(false);
        dataSource.setFilters("stat");
        dataSource.setDbType("oracle");
        dataSource.setDriver(new MockDriver() {

            @Override
            public int getMajorVersion() {
                return 9;
            }
        });
    }

    protected void tearDown() throws Exception {
        dataSource.close();
    }

    public void test_error() throws Exception {
        Exception error = null;
        try {
            dataSource.init();
        } catch (Exception e) {
            error = e;
        }
        Assert.assertNotNull(error);
    }
}
