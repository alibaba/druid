package com.alibaba.druid.bvt.utils;

import com.alibaba.druid.DbType;
import com.alibaba.druid.util.JdbcConstants;
import com.alibaba.druid.util.JdbcUtils;
import org.junit.jupiter.api.Test;

import java.sql.Driver;

import static org.junit.jupiter.api.Assertions.*;

public class JdbcUtils_driver {
    @Test
    public void test_null() throws Exception {
        assertNull(JdbcUtils.getDriverClassName(null));
    }

    @Test
    public void test_driver() throws Exception {
        String url = "jdbc:odps:xxx";
        String className = JdbcUtils.getDriverClassName(url);
        Class<?> clazz = Class.forName(className);
        assertNotNull(clazz);
        Driver driver = (Driver) clazz.newInstance();
        assertNotNull(driver);

        assertEquals(3, driver.getMajorVersion());
        assertEquals(4, driver.getMinorVersion());

        assertEquals(JdbcConstants.ODPS, JdbcUtils.getDbTypeRaw(url, className));
    }

    @Test
    public void test_oceanbase() {
        assertEquals(JdbcConstants.OCEANBASE, DbType.of(JdbcUtils.getDbType("jdbc:oceanbase://127.1:3306/test?a=b", null)));
        assertEquals(JdbcConstants.OCEANBASE_ORACLE, DbType.of(JdbcUtils.getDbType("jdbc:oceanbase:oracle://127.1:3306/test?a=b", null)));
    }

    @Test
    public void test_yashandb() throws Exception {
        String url = "jdbc:yasdb://127.0.0.1:1688/REGRESS?productName=Oracle";
        assertEquals("com.yashandb.jdbc.Driver", JdbcUtils.getDriverClassName(url));
        assertEquals(DbType.oracle, JdbcUtils.getDbTypeRaw(url, null));
        assertEquals(DbType.oracle, DbType.of(JdbcUtils.getDbType(url, null)));
    }

    @Test
    public void test_log4jdbc_mysql() {
        String jdbcUrl = "jdbc:log4jdbc:mysql://localhost:8066/test";
        DbType dbType = JdbcUtils.getDbTypeRaw(jdbcUrl, null);
        assertEquals(DbType.mysql, dbType, "not support log4jdbc mysql, url like jdbc:log4jdbc:mysql:...");
    }

    @Test
    public void test_log4jdbc_mysql2() throws Exception {
        String jdbcUrl = "jdbc:log4jdbc:mysql://localhost:8066/test";
        DbType dbType = JdbcUtils.getDbTypeRaw(jdbcUrl, "net.sf.log4jdbc.DriverSpy");
        assertEquals(DbType.mysql, dbType, "not support log4jdbc mysql, url like jdbc:log4jdbc:mysql:...");
    }

    @Test
    public void test_log4jdbc_derby() throws Exception {
        String jdbcUrl = "jdbc:log4jdbc:derby://localhost:1527//db-derby-10.2.2.0-bin/databases/MyDatabase";
        DbType dbType = JdbcUtils.getDbTypeRaw(jdbcUrl, "net.sf.log4jdbc.sql.jdbcapi.DriverSpy");
        assertEquals(DbType.derby, dbType, "not support log4jdbc mysql, url like jdbc:log4jdbc:derby:...");
    }

    @Test
    public void test_log4jdbc_es() throws Exception {
        assertEquals(JdbcConstants.ELASTIC_SEARCH,
                JdbcUtils.getDbType("jdbc:elastic://100.69.202.4:9300/tcbuyer?cluster.name=refund_cluster", null));
    }

    @Test
    public void test_log4jdbc_es_1() throws Exception {
        assertEquals(JdbcConstants.ELASTIC_SEARCH,
                JdbcUtils.getDbType("jdbc:elastic://100.69.202.4:9300/tcbuyer?cluster.name=refund_cluster", "com.alibaba.xdriver.elastic.jdbc.ElasticDriver"));
    }

    @Test
    public void test_log4jdbc_es_driver() throws Exception {
        assertEquals(JdbcConstants.ELASTIC_SEARCH_DRIVER,
                JdbcUtils.getDriverClassName("jdbc:elastic://100.69.202.4:9300/tcbuyer?cluster.name=refund_cluster"));
    }
}
