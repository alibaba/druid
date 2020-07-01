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

    public void test_xugu_dbtype() throws Exception {
        assertEquals(JdbcConstants.XUGU, JdbcUtils.getDbType("jdbc:xugu://127.0.0.1:5138/TEST", "com.xugu.cloudjdbc.Driver"));
    }

    public void test_xugu_driver() throws Exception {
        assertEquals(JdbcConstants.XUGU_DRIVER, JdbcUtils.getDriverClassName("jdbc:xugu:"));
    }

    public void test_kdb() throws Exception {
        assertEquals(JdbcConstants.KDB_DRIVER, JdbcUtils.getDriverClassName("jdbc:inspur:"));
    }
}
