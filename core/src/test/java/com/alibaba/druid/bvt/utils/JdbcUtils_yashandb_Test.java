package com.alibaba.druid.bvt.utils;

import com.alibaba.druid.DbType;
import com.alibaba.druid.util.JdbcConstants;
import com.alibaba.druid.util.JdbcUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class JdbcUtils_yashandb_Test {

    @Test
    public void test_isYashanDbDriver_exact() {
        assertTrue(JdbcUtils.isYashanDbDriver("com.yashandb.jdbc.Driver"));
    }

    @Test
    public void test_isYashanDbDriver_package_prefix() {
        assertTrue(JdbcUtils.isYashanDbDriver("com.yashandb.jdbc.Driver"));
    }

    @Test
    public void test_isYashanDbDriver_false_for_oracle() {
        assertFalse(JdbcUtils.isYashanDbDriver("oracle.jdbc.OracleDriver"));
    }

    @Test
    public void test_isYashanDbDriver_false_for_null() {
        assertFalse(JdbcUtils.isYashanDbDriver(null));
    }

    @Test
    public void test_isYashanDbDriver_false_for_mysql() {
        assertFalse(JdbcUtils.isYashanDbDriver("com.mysql.cj.jdbc.Driver"));
    }

    @Test
    public void test_isOracleDbType_string_yashandb() {
        assertTrue(JdbcUtils.isOracleDbType("yashandb"));
    }

    @Test
    public void test_isOracleDbType_dbType_yashandb() {
        assertTrue(JdbcUtils.isOracleDbType(DbType.yashandb));
    }

    @Test
    public void test_isOracleDbType_still_true_for_oracle() {
        assertTrue(JdbcUtils.isOracleDbType("oracle"));
        assertTrue(JdbcUtils.isOracleDbType(DbType.oracle));
    }

    @Test
    public void test_isOracleDbType_still_true_for_oceanbase_oracle() {
        assertTrue(JdbcUtils.isOracleDbType("oceanbase_oracle"));
        assertTrue(JdbcUtils.isOracleDbType(DbType.oceanbase_oracle));
    }

    @Test
    public void test_isOracleDbType_still_true_for_ali_oracle() {
        assertTrue(JdbcUtils.isOracleDbType("ali_oracle"));
        assertTrue(JdbcUtils.isOracleDbType(DbType.ali_oracle));
    }

    @Test
    public void test_isOracleDbType_false_for_mysql() {
        assertFalse(JdbcUtils.isOracleDbType("mysql"));
        assertFalse(JdbcUtils.isOracleDbType(DbType.mysql));
    }

    @Test
    public void test_getDriverClassName_yasdb_url() throws Exception {
        assertEquals(JdbcConstants.YASHANDB_DRIVER,
                JdbcUtils.getDriverClassName("jdbc:yasdb://localhost:1688/test"));
    }

    @Test
    public void test_getDbType_yasdb_url() {
        assertEquals(DbType.yashandb,
                JdbcUtils.getDbTypeRaw("jdbc:yasdb://localhost:1688/test", null));
    }

    @Test
    public void test_yashandb_constants() {
        assertEquals(DbType.yashandb, JdbcConstants.YASHANDB);
        assertEquals("com.yashandb.jdbc.Driver", JdbcConstants.YASHANDB_DRIVER);
    }

    @Test
    public void test_dbType_of_yashandb() {
        assertEquals(DbType.yashandb, DbType.of("yashandb"));
    }
}
