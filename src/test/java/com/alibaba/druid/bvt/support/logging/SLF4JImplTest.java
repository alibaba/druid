package com.alibaba.druid.bvt.support.logging;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.logging.SLF4JImpl;

public class SLF4JImplTest extends TestCase {

    public void test_0() throws Exception {
        SLF4JImpl impl = new SLF4JImpl(DruidDataSource.class.getName());
       
        impl.isDebugEnabled();
        impl.isInfoEnabled();
        impl.isWarnEnabled();
        impl.debug("");
        impl.debug("", new Exception());
        impl.info("");
        impl.warn("");
        impl.warn("", new Exception());
        impl.error("");
        impl.error("", new Exception());
        Assert.assertEquals(1, impl.getInfoCount());
        Assert.assertEquals(2, impl.getErrorCount());
        Assert.assertEquals(2, impl.getWarnCount());
        Assert.assertEquals(1, impl.getInfoCount());
    }
}
