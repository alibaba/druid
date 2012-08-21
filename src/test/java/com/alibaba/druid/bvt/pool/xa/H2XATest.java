package com.alibaba.druid.bvt.pool.xa;

import javax.sql.XAConnection;

import junit.framework.TestCase;

import com.alibaba.druid.pool.xa.DruidXADataSource;
import com.alibaba.druid.util.JdbcUtils;

public class H2XATest extends TestCase {

    private DruidXADataSource dataSource;

    protected void setUp() throws Exception {
        dataSource = new DruidXADataSource();
        dataSource.setMinIdle(1);
        dataSource.setUrl("jdbc:h2:mem:test;");
        dataSource.setTestOnBorrow(false);

        JdbcUtils.execute(dataSource, "CREATE TABLE user (id INT, name VARCHAR(40))");

    }

    protected void tearDown() throws Exception {
        JdbcUtils.execute(dataSource, "DROP TABLE user");
        JdbcUtils.close(dataSource);
    }

    public void test_0() throws Exception {
        XAConnection conn = dataSource.getXAConnection();
        conn.close();
    }
}
