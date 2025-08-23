package com.alibaba.druid.bvt.support.logging;

import static org.junit.Assert.assertEquals;

import junit.framework.TestCase;


import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.logging.Jdk14LoggingImpl;

public class Jdk14LoggingImplTest extends TestCase {
    public void test_0() throws Exception {
        Jdk14LoggingImpl impl = new Jdk14LoggingImpl(DruidDataSource.class.getName());

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
        assertEquals(1, impl.getInfoCount());
        assertEquals(2, impl.getErrorCount());
        assertEquals(2, impl.getWarnCount());
        assertEquals(1, impl.getInfoCount());
    }
}
