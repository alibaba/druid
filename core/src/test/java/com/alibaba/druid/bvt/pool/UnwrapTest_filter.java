package com.alibaba.druid.bvt.pool;

import static org.junit.Assert.*;


import java.sql.Connection;

import com.alibaba.druid.PoolTestCase;
import junit.framework.TestCase;


import com.alibaba.druid.filter.logging.Log4jFilter;
import com.alibaba.druid.filter.logging.LogFilter;
import com.alibaba.druid.filter.stat.MergeStatFilter;
import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.pool.DruidDataSource;

public class UnwrapTest_filter extends PoolTestCase {
    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        super.setUp();

        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setTestOnBorrow(true);
        dataSource.setInitialSize(1);
        dataSource.setValidationQuery("select 1");
        dataSource.setValidationQueryTimeout(10);
        dataSource.setQueryTimeout(100);

        dataSource.setFilters("mergeStat,log4j");
    }

    protected void tearDown() throws Exception {
        dataSource.close();

        super.tearDown();
    }

    public void test_unwrap() throws Exception {
        Connection conn = dataSource.getConnection();
        conn.close();

        assertTrue(dataSource.isWrapperFor(StatFilter.class));
        assertNotNull(dataSource.unwrap(StatFilter.class));

        assertTrue(dataSource.isWrapperFor(MergeStatFilter.class));
        assertNotNull(dataSource.unwrap(MergeStatFilter.class));

        assertTrue(dataSource.isWrapperFor(LogFilter.class));
        assertNotNull(dataSource.unwrap(LogFilter.class));

        assertTrue(dataSource.isWrapperFor(Log4jFilter.class));
        assertNotNull(dataSource.unwrap(Log4jFilter.class));
    }
}
