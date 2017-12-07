package com.alibaba.druid.bvt.utils;

import org.junit.Assert;

import com.alibaba.druid.util.JdbcConstants;
import com.alibaba.druid.util.JdbcUtils;

import junit.framework.TestCase;


public class JdbcUtilsTest2 extends TestCase {
    public void test_get_0() throws Exception {
        assertEquals(JdbcConstants.ORACLE_DRIVER, JdbcUtils.getDriverClassName("JDBC:oracle:"));
    }

    public void test_gbase() throws Exception {
        assertEquals(JdbcConstants.GBASE_DRIVER, JdbcUtils.getDriverClassName("jdbc:gbase:"));
    }

    public void test_kingbase() throws Exception {
        assertEquals(JdbcConstants.KINGBASE_DRIVER, JdbcUtils.getDriverClassName("jdbc:kingbase:"));
    }
}
