package com.alibaba.druid.bvt.pool;

import java.util.concurrent.locks.ReentrantLock;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.util.JdbcUtils;

public class LockFairTest extends TestCase {

    private DruidDataSource dataSource;
    
    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();
    }
    
    protected void tearDown() throws Exception {
        JdbcUtils.close(dataSource);
    }

    public void test_fair() throws Exception {
        Assert.assertEquals(false, ((ReentrantLock)dataSource.getLock()).isFair());
        dataSource.setMaxWait(100);
        
        Assert.assertEquals(true, ((ReentrantLock)dataSource.getLock()).isFair());
    }
}
