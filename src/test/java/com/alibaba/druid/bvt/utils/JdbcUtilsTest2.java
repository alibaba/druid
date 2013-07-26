package com.alibaba.druid.bvt.utils;

import org.junit.Assert;

import com.alibaba.druid.util.JdbcConstants;
import com.alibaba.druid.util.JdbcUtils;

import junit.framework.TestCase;


public class JdbcUtilsTest2 extends TestCase {
    public void test_get_0() throws Exception {
        Assert.assertEquals(JdbcConstants.ORACLE_DRIVER, JdbcUtils.getDriverClassName("JDBC:oracle:"));
    }
}
