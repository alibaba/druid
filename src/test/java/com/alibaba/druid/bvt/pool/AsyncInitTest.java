package com.alibaba.druid.bvt.pool;

import com.alibaba.druid.pool.DruidDataSource;
import junit.framework.TestCase;

public class AsyncInitTest extends TestCase {
    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();
        dataSource.setAsyncInit(true);
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setInitialSize(10);
        dataSource.setMaxActive(10);
    }

    protected void tearDown() throws Exception {
        dataSource.close();
    }

    public void test_async_init() throws Exception {
        dataSource.init();

        for (int i = 0; i < 1000; ++i) {
            if (dataSource.getPoolingCount() == dataSource.getInitialSize()) {
                break;
            }
            Thread.sleep(10);
        }
        assertEquals(10, dataSource.getPoolingCount());
        assertEquals(10, dataSource.getCreateCount());
    }
}
