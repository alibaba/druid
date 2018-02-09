package com.alibaba.druid.bvt.pool;

import com.alibaba.druid.PoolTestCase;
import org.junit.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.pool.DataSourceDisableException;

public class DataSourceDisableExceptionTest extends PoolTestCase {
    public void test_0() throws Exception {
        DataSourceDisableException ex = new DataSourceDisableException();
        Assert.assertEquals(null, ex.getMessage());
    }
    
    public void test_1() throws Exception {
        DataSourceDisableException ex = new DataSourceDisableException("XXX");
        Assert.assertEquals("XXX", ex.getMessage());
    }
    
    public void test_2() throws Exception {
        DataSourceDisableException ex = new DataSourceDisableException(new IllegalStateException());
        Assert.assertTrue(ex.getCause() instanceof IllegalStateException);
    }
}
