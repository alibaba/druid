package com.alibaba.druid.bvt.pool;

import static org.junit.Assert.*;


import com.alibaba.druid.PoolTestCase;
import junit.framework.TestCase;

import com.alibaba.druid.pool.DataSourceDisableException;

public class DataSourceDisableExceptionTest extends PoolTestCase {
    public void test_0() throws Exception {
        DataSourceDisableException ex = new DataSourceDisableException();
        assertEquals(null, ex.getMessage());
    }

    public void test_1() throws Exception {
        DataSourceDisableException ex = new DataSourceDisableException("XXX");
        assertEquals("XXX", ex.getMessage());
    }

    public void test_2() throws Exception {
        DataSourceDisableException ex = new DataSourceDisableException(new IllegalStateException());
        assertTrue(ex.getCause() instanceof IllegalStateException);
    }
}
