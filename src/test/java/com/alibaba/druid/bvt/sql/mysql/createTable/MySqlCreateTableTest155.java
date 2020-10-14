package com.alibaba.druid.bvt.sql.mysql.createTable;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCreateTableStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;

public class MySqlCreateTableTest155
        extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "create table pk(id int primary key , name varchar)";

        SQLCreateTableStatement stmt = (SQLCreateTableStatement) SQLUtils.parseSingleMysqlStatement(sql);

        assertTrue(stmt.isPrimaryColumn("id"));
        assertTrue(stmt.isPrimaryColumn("`id`"));
    }

    public void test_1() throws Exception {
        String sql = "create table pk(id int, name varchar, primary key(id, name))";

        SQLCreateTableStatement stmt = (SQLCreateTableStatement) SQLUtils.parseSingleMysqlStatement(sql);

        assertTrue(stmt.isPrimaryColumn("id"));
        assertTrue(stmt.isPrimaryColumn("`id`"));
    }
}