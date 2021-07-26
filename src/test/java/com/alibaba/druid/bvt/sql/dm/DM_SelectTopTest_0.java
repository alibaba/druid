package com.alibaba.druid.bvt.sql.dm;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import junit.framework.TestCase;

/**
 * @author two brother
 * @date 2021/7/26 10:29
 */
public class DM_SelectTopTest_0 extends TestCase {

    public void test_0() throws Exception {
        String sql = "select top 12 a, b from sys_user_online";
        SQLStatement stmt = SQLUtils.parseSingleStatement(sql, DbType.dm);

        assertEquals("SELECT TOP 12 a, b\n"
                +"FROM sys_user_online", stmt.toString());

    }
    public void test_1() throws Exception {
        String sql = "select distinct top 12 a, b from sys_user_online";
        SQLStatement stmt = SQLUtils.parseSingleStatement(sql, DbType.dm);

        assertEquals("SELECT DISTINCT TOP 12 a, b\n"
                +"FROM sys_user_online", stmt.toString());

    }

    public void test_2() throws Exception {
        String sql = "select distinct top 1 PERCENT WITH TIES a, b from sys_user_online";
        SQLStatement stmt = SQLUtils.parseSingleStatement(sql, DbType.dm);

        assertEquals("SELECT DISTINCT TOP 1 PERCENT WITH TIES a, b\n"
                +"FROM sys_user_online", stmt.toString());

    }

    public void test_3() throws Exception {
        String sql = "select distinct top 1 PERCENT WITH TIES a, b from (select distinct top 1 PERCENT WITH TIES a, b from sys_user_online) t";
        SQLStatement stmt = SQLUtils.parseSingleStatement(sql, DbType.dm);

        assertEquals("SELECT DISTINCT TOP 1 PERCENT WITH TIES a, b\n" +
                "FROM (\n" +
                "\tSELECT DISTINCT TOP 1 PERCENT WITH TIES a, b\n" +
                "\tFROM sys_user_online\n" +
                ") t", stmt.toString());

    }
}
