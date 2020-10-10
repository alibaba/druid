package com.alibaba.druid.bvt.sql.mysql.create;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.statement.SQLCreateDatabaseStatement;

public class MySqlCreateDatabaseTest9_issue_3957
        extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "create database user default charset=utf8 collate=utf8_general_ci;";

        SQLCreateDatabaseStatement stmt = (SQLCreateDatabaseStatement) SQLUtils.parseSingleMysqlStatement(sql);

        assertEquals("CREATE DATABASE user CHARACTER SET utf8 COLLATE utf8_general_ci;", stmt.toString());
//
//        assertTrue(stmt.isPrimaryColumn("id"));
//        assertTrue(stmt.isPrimaryColumn("`id`"));
    }
}