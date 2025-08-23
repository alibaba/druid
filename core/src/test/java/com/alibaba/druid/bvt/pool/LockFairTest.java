package com.alibaba.druid.bvt.pool;

import static org.junit.Assert.*;


import java.sql.Connection;
import java.util.concurrent.locks.ReentrantLock;

import junit.framework.TestCase;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.util.JdbcUtils;

public class LockFairTest extends TestCase {
    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xx");
    }

    protected void tearDown() throws Exception {
        JdbcUtils.close(dataSource);
    }

    public void test_fair() throws Exception {
        assertEquals(false, ((ReentrantLock) dataSource.getLock()).isFair());
        dataSource.setMaxWait(100);

        assertEquals(false, ((ReentrantLock) dataSource.getLock()).isFair());
        {
            Connection conn = dataSource.getConnection();
            conn.close();
        }
        dataSource.setMaxWait(110);

        assertEquals(false, ((ReentrantLock) dataSource.getLock()).isFair());
        {
            Connection conn = dataSource.getConnection();
            conn.close();
        }

        dataSource.setMaxWait(0);

        assertEquals(false, ((ReentrantLock) dataSource.getLock()).isFair());
        {
            Connection conn = dataSource.getConnection();
            conn.close();
        }
    }

    public void test_fair_1() throws Exception {
        Connection conn = dataSource.getConnection();
        conn.close();

        assertEquals(false, ((ReentrantLock) dataSource.getLock()).isFair());
        dataSource.setMaxWait(100);

        assertEquals(false, ((ReentrantLock) dataSource.getLock()).isFair());
    }
}
