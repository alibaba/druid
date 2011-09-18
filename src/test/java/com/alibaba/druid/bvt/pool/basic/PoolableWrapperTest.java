package com.alibaba.druid.bvt.pool.basic;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.mock.MockConnection;
import com.alibaba.druid.pool.PoolableWrapper;


public class PoolableWrapperTest extends TestCase {
    public void test_isWrapper() throws Exception {
        PoolableWrapper wrapper = new PoolableWrapper(new MockConnection());
        
        Assert.assertEquals(false, wrapper.isWrapperFor(null));
        Assert.assertEquals(true, wrapper.isWrapperFor(PoolableWrapper.class));
        Assert.assertEquals(true, wrapper.isWrapperFor(MockConnection.class));
    }
    
    public void test_unwrap() throws Exception {
        PoolableWrapper wrapper = new PoolableWrapper(new MockConnection());
        
        Assert.assertEquals(null, wrapper.unwrap(null));
        Assert.assertEquals(true, wrapper.unwrap(PoolableWrapper.class) != null);
        Assert.assertEquals(true, wrapper.unwrap(MockConnection.class) != null);
    }
}
