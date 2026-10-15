package com.alibaba.druid.bvt.bug;

import com.alibaba.druid.DbType;
import com.alibaba.druid.util.JdbcUtils;
import junit.framework.TestCase;

import java.sql.SQLException;

public class IssueGbase8s extends TestCase {

    public void test_gbase8s_jdbc_utils() throws SQLException {
        // GBase8s jdbc url
        String url = "jdbc:gbasedbt-sqli://127.0.0.1:9088/testdb:GBASEDBTSERVER=servername;";
        String url2 = "jdbc:gbase8s://127.0.0.1:9088/testdb:GBASEDBTSERVER=servername;";

        // 1. 测试 getDbTypeRaw：命中你新增分支，返回 DbType.gbase8s
        DbType dbType = JdbcUtils.getDbTypeRaw(url, null);
        assertEquals(DbType.gbase8s, dbType);

        DbType dbType2 = JdbcUtils.getDbTypeRaw(url2, null);
        assertEquals(DbType.gbase8s, dbType2);

        // 2. 测试 getDriverClassName：命中你新增分支，返回gbase8s驱动类
        String driverClass = JdbcUtils.getDriverClassName(url);
        assertEquals("com.gbasedbt.jdbc.Driver", driverClass);

        String driverClass2 = JdbcUtils.getDriverClassName(url2);
        assertEquals("com.gbasedbt.jdbc.Driver", driverClass2);

        // 可选：再测一个异常/其他url做兜底（可选）
        DbType otherDbType = JdbcUtils.getDbTypeRaw("jdbc:mysql://127.0.0.1:3306/db", null);
        assertEquals(DbType.mysql, otherDbType);
    }
}
