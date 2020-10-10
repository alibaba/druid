package com.alibaba.druid.bvt.sql.mysql.create;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.statement.SQLCreateDatabaseStatement;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;

public class MySqlCreateDatabaseTest9_issue_3975
        extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "create database user default charset=utf8 collate=utf8_general_ci;";

        SQLCreateDatabaseStatement stmt = (SQLCreateDatabaseStatement) SQLUtils.parseSingleMysqlStatement(sql);

        assertEquals("CREATE DATABASE user CHARACTER SET utf8 COLLATE utf8_general_ci;", stmt.toString());
    }

    public void test_1() throws Exception {
        String sql = "create database user COLLATE=utf8_bin default CHARSET=utf8";

        SQLCreateDatabaseStatement stmt = (SQLCreateDatabaseStatement) SQLUtils.parseSingleMysqlStatement(sql);

        assertEquals("CREATE DATABASE user CHARACTER SET utf8 COLLATE utf8_bin", stmt.toString());
    }

    public void test_craeteTable() throws Exception {
        String sql = "create table test_option(a int) COLLATE=utf8_bin default CHARSET=utf8; ";

        SQLCreateTableStatement stmt = (SQLCreateTableStatement) SQLUtils.parseSingleMysqlStatement(sql);

        assertEquals("CREATE TABLE test_option (\n" +
                "\ta int\n" +
                ") COLLATE = utf8_bin CHARSET = utf8;", stmt.toString());
    }
}