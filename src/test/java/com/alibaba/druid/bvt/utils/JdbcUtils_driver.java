package com.alibaba.druid.bvt.utils;

import java.sql.Driver;

import com.alibaba.druid.DbType;
import org.junit.Assert;

import com.alibaba.druid.util.JdbcConstants;
import com.alibaba.druid.util.JdbcUtils;

import junit.framework.TestCase;

public class JdbcUtils_driver extends TestCase {
    
    public void test_null() throws Exception {
        Assert.assertNull(JdbcUtils.getDriverClassName(null));
    }

    public void test_driver() throws Exception {
        String url = "jdbc:odps:xxx";
        String className = JdbcUtils.getDriverClassName(url);
        Class<?> clazz = Class.forName(className);
        Assert.assertNotNull(clazz);
        Driver driver = (Driver) clazz.newInstance();
        Assert.assertNotNull(driver);

        Assert.assertEquals(0, driver.getMajorVersion());
        Assert.assertEquals(1, driver.getMinorVersion());
        
        Assert.assertEquals(JdbcConstants.ODPS, JdbcUtils.getDbTypeRaw(url, className));
    }

    public void test_oceanbase() {
        assertEquals(JdbcConstants.OCEANBASE, DbType.of(JdbcUtils.getDbType("jdbc:oceanbase://127.1:3306/test?a=b", null)));
        assertEquals(JdbcConstants.OCEANBASE_ORACLE, DbType.of(JdbcUtils.getDbType("jdbc:oceanbase:oracle://127.1:3306/test?a=b", null)));
    }

    public void test_log4jdbc_mysql() {
        String jdbcUrl = "jdbc:log4jdbc:mysql://localhost:8066/test";
        DbType dbType = JdbcUtils.getDbTypeRaw(jdbcUrl, null);
        assertEquals("not support log4jdbc mysql, url like jdbc:log4jdbc:mysql:...", DbType.mysql, dbType);
    }

    public void test_log4jdbc_mysql2() throws Exception {
        String jdbcUrl = "jdbc:log4jdbc:mysql://localhost:8066/test";
        DbType dbType = JdbcUtils.getDbTypeRaw(jdbcUrl, "net.sf.log4jdbc.DriverSpy");
        assertEquals("not support log4jdbc mysql, url like jdbc:log4jdbc:mysql:...", DbType.mysql, dbType);
    }

    public void test_log4jdbc_derby() throws Exception {
        String jdbcUrl = "jdbc:log4jdbc:derby://localhost:1527//db-derby-10.2.2.0-bin/databases/MyDatabase";
        DbType dbType = JdbcUtils.getDbTypeRaw(jdbcUrl, "net.sf.log4jdbc.sql.jdbcapi.DriverSpy");
        assertEquals("not support log4jdbc mysql, url like jdbc:log4jdbc:derby:...", DbType.derby, dbType);
    }

    public void test_log4jdbc_es() throws Exception {
        assertEquals(JdbcConstants.ELASTIC_SEARCH
                , JdbcUtils.getDbType("jdbc:elastic://100.69.202.4:9300/tcbuyer?cluster.name=refund_cluster", null));
    }

    public void test_log4jdbc_es_1() throws Exception {
        assertEquals(JdbcConstants.ELASTIC_SEARCH
                , JdbcUtils.getDbType("jdbc:elastic://100.69.202.4:9300/tcbuyer?cluster.name=refund_cluster", "com.alibaba.xdriver.elastic.jdbc.ElasticDriver"));
    }

    public void test_log4jdbc_es_driver() throws Exception {
        assertEquals(JdbcConstants.ELASTIC_SEARCH_DRIVER
                , JdbcUtils.getDriverClassName("jdbc:elastic://100.69.202.4:9300/tcbuyer?cluster.name=refund_cluster"));
    }
}
