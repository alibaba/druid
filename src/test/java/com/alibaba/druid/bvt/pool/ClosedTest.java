package com.alibaba.druid.bvt.pool;

import java.sql.Connection;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.pool.DataSourceClosedException;
import com.alibaba.druid.pool.DruidDataSource;

public class ClosedTest extends TestCase {

    public void test_close() throws Exception {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xxx");

        Connection conn = dataSource.getConnection();
        conn.close();

        dataSource.close();

        DataSourceClosedException error = null;

        try {
            dataSource.getConnection();
        } catch (DataSourceClosedException ex) {
            error = ex;
        }

        Assert.assertNotNull(error);
    }
}
