package com.alibaba.druid.bvt.support.logging;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.logging.Log4jImpl;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class Log4jImplTest {
    @Test
    public void test_0() throws Exception {
        Log4jImpl impl = new Log4jImpl(DruidDataSource.class.getName());

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
