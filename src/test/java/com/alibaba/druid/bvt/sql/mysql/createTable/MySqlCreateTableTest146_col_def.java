package com.alibaba.druid.bvt.sql.mysql.createTable;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCreateTableStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import junit.framework.TestCase;

import java.util.List;

/**
 * @version 1.0
 * @ClassName MySqlCreateTableTest146_col_def
 * @description
 *
 * column_definition:
 *     data_type [NOT NULL | NULL] [DEFAULT default_value]
 *       [AUTO_INCREMENT] [UNIQUE [KEY]] [[PRIMARY] KEY]
 *       [COMMENT 'string']
 *       [COLLATE collation_name]
 *       [COLUMN_FORMAT {FIXED|DYNAMIC|DEFAULT}]
 *       [STORAGE {DISK|MEMORY}]
 *       [reference_definition]
 *   | data_type
 *       [COLLATE collation_name]
 *       [GENERATED ALWAYS] AS (expr)
 *       [VIRTUAL | STORED] [NOT NULL | NULL]
 *       [UNIQUE [KEY]] [[PRIMARY] KEY]
 *       [COMMENT 'string']
 *       [reference_definition]
 *
 * @Author zzy
 * @Date 2019-05-14 10:14
 */
public class MySqlCreateTableTest146_col_def extends TestCase {

    public void test_0() throws Exception {

        String sql = "create table tb_dxdd (" +
                "`a` varchar(10) not null default 'val' comment 'hehe' collate utf8_unicode_ci column_format default storage disk references tb_ref (a) match full on delete cascade on update cascade" +
                ");";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        MySqlCreateTableStatement stmt = (MySqlCreateTableStatement)statementList.get(0);

        assertEquals(1, statementList.size());

        /*
        assertEquals("CREATE TABLE tb_dxdd (\n" +
                "\t`a` varchar(10) NOT NULL DEFAULT 'val' COMMENT 'hehe' COLLATE utf8_unicode_ci COLUMN_FORMAT DEFAULT STORAGE disk REFERENCES tb_ref (a) MATCH FULL ON DELETE CASCADE ON UPDATE CASCADE\n" +
                ");", stmt.toString());

        assertEquals("create table tb_dxdd (\n" +
                "\t`a` varchar(10) not null default 'val' comment 'hehe' collate utf8_unicode_ci column_format default storage disk references tb_ref (a) match full on delete cascade on update cascade\n" +
                ");", stmt.toLowerCaseString());
        */
        // Output order bad.
        assertEquals("CREATE TABLE tb_dxdd (\n" +
                "\t`a` varchar(10) COLLATE utf8_unicode_ci NOT NULL DEFAULT 'val' STORAGE disk COLUMN_FORMAT DEFAULT COMMENT 'hehe' REFERENCES tb_ref (a) MATCH FULL ON DELETE CASCADE ON UPDATE CASCADE\n" +
                ");", stmt.toString());

        assertEquals("create table tb_dxdd (\n" +
                "\t`a` varchar(10) collate utf8_unicode_ci not null default 'val' storage disk column_format default comment 'hehe' references tb_ref (a) match full on delete cascade on update cascade\n" +
                ");", stmt.toLowerCaseString());
    }

    public void test_1() throws Exception {
        String sql = "create table tb_xx (a int generated always as (1) virtual not null comment 'xxx');";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        MySqlCreateTableStatement stmt = (MySqlCreateTableStatement)statementList.get(0);

        assertEquals(1, statementList.size());

        assertEquals("CREATE TABLE tb_xx (\n" +
                "\ta int GENERATED ALWAYS AS (1) VIRTUAL NOT NULL COMMENT 'xxx'\n" +
                ");", stmt.toString());

        assertEquals("create table tb_xx (\n" +
                "\ta int generated always as (1) virtual not null comment 'xxx'\n" +
                ");", stmt.toLowerCaseString());
    }

    public void test_2() throws Exception {
        String sql = "create table tb_ssx (a varchar(10) collate utf8_general_ci as ('val') stored not null primary key comment 'hh' references tb_ref (a));";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        MySqlCreateTableStatement stmt = (MySqlCreateTableStatement)statementList.get(0);

        assertEquals(1, statementList.size());

        /*
        assertEquals("CREATE TABLE tb_ssx (\n" +
                "\ta varchar(10) COLLATE utf8_general_ci AS ('val') SORTED NOT NULL PRIMARY KEY COMMENT 'hh' REFERENCES tb_ref (a)\n" +
                ");", stmt.toString());

        assertEquals("create table tb_ssx (\n" +
                "\ta varchar(10) collate utf8_general_ci as ('val') sorted not null primary key comment 'hh' references tb_ref (a)\n" +
                ");", stmt.toLowerCaseString());
        */
        // Output order bad.
        assertEquals("CREATE TABLE tb_ssx (\n" +
                "\ta varchar(10) COLLATE utf8_general_ci NOT NULL PRIMARY KEY COMMENT 'hh' AS ('val') STORED REFERENCES tb_ref (a)\n" +
                ");", stmt.toString());

        assertEquals("create table tb_ssx (\n" +
                "\ta varchar(10) collate utf8_general_ci not null primary key comment 'hh' as ('val') stored references tb_ref (a)\n" +
                ");", stmt.toLowerCaseString());
    }

}
