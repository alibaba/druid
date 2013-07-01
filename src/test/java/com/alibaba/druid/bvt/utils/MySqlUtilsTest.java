package com.alibaba.druid.bvt.utils;

import java.lang.reflect.Constructor;

import com.alibaba.druid.util.MySqlUtils;
import com.mysql.jdbc.ConnectionImpl;

import junit.framework.TestCase;

public class MySqlUtilsTest extends TestCase {
    public void test_() throws Exception {
        Constructor<ConnectionImpl> constructor = ConnectionImpl.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        ConnectionImpl conn = constructor.newInstance();
        MySqlUtils.createXAConnection(conn);
    }
}
