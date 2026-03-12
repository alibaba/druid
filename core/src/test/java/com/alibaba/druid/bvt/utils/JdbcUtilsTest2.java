package com.alibaba.druid.bvt.utils;

import com.alibaba.druid.util.JdbcConstants;
import com.alibaba.druid.util.JdbcUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class JdbcUtilsTest2 {
    @Test
    public void test_get_0() throws Exception {
        assertEquals(JdbcConstants.ORACLE_DRIVER, JdbcUtils.getDriverClassName("JDBC:oracle:"));
    }

    @Test
    public void test_gbase() throws Exception {
        assertEquals(JdbcConstants.GBASE_DRIVER, JdbcUtils.getDriverClassName("jdbc:gbase:"));
    }

    @Test
    public void test_kingbase() throws Exception {
        assertEquals(JdbcConstants.KINGBASE_DRIVER, JdbcUtils.getDriverClassName("jdbc:kingbase:"));
    }

    @Test
    public void test_xugu_dbtype() throws Exception {
        assertEquals(JdbcConstants.XUGU, JdbcUtils.getDbTypeRaw("jdbc:xugu://127.0.0.1:5138/TEST", "com.xugu.cloudjdbc.Driver"));
    }

    @Test
    public void test_xugu_driver() throws Exception {
        assertEquals(JdbcConstants.XUGU_DRIVER, JdbcUtils.getDriverClassName("jdbc:xugu:"));
    }

    @Test
    public void test_kdb() throws Exception {
        assertEquals(JdbcConstants.KDB_DRIVER, JdbcUtils.getDriverClassName("jdbc:inspur:"));
    }

    @Test
    public void test_tydb() throws Exception {
        assertEquals(JdbcConstants.TYDB_DRIVER, JdbcUtils.getDriverClassName("jdbc:dbcp:"));
    }
}
