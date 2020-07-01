package com.alibaba.druid.bvt.filter;

import java.sql.Connection;

import org.junit.Assert;

import junit.framework.TestCase;

import com.alibaba.druid.filter.logging.Slf4jLogFilter;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.util.JdbcUtils;

public class Slf4jFilterTest extends TestCase {

    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();

        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setFilters("slf4j");
        dataSource.setDbType("mysql");
    }

    protected void tearDown() throws Exception {
        JdbcUtils.close(dataSource);
    }

    public void test_slf4j() throws Exception {
        dataSource.init();
        
        Slf4jLogFilter filter = dataSource.unwrap(Slf4jLogFilter.class);
        Assert.assertNotNull(filter);
        
        Connection conn = dataSource.getConnection();
        conn.close();
    }
}
