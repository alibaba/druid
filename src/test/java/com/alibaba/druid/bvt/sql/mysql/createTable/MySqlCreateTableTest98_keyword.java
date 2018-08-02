package com.alibaba.druid.bvt.sql.mysql.createTable;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCreateTableStatement;
import com.alibaba.druid.util.JdbcConstants;

import java.util.List;

public class MySqlCreateTableTest98_keyword extends MysqlTest {

    public void test_0() throws Exception {
        String sql = " CREATE TABLE IF NOT EXISTS meta.view (\n" +
                "                cluster_name varchar(16) ,\n" +
                "                table_schema varchar(128), \n" +
                "                view_name varchar(128), \n" +
                "                column_list text, \n" +
                "                sql text\n" +
                "                )";

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
        MySqlCreateTableStatement stmt = (MySqlCreateTableStatement)statementList.get(0);

        assertEquals(1, statementList.size());
        assertEquals(5, stmt.getTableElementList().size());

        assertEquals("CREATE TABLE IF NOT EXISTS meta.view (\n" +
                "\tcluster_name varchar(16),\n" +
                "\ttable_schema varchar(128),\n" +
                "\tview_name varchar(128),\n" +
                "\tcolumn_list text,\n" +
                "\tsql text\n" +
                ")", stmt.toString());

    }

    public void test_1() throws Exception {
        String sql = " CREATE TABLE IF NOT EXISTS meta.partitions (\n" +
                "                cluster_name varchar(16) ,\n" +
                "                table_schema varchar(128), \n" +
                "                view_name varchar(128), \n" +
                "                column_list text, \n" +
                "                sql text\n" +
                "                )";

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
        MySqlCreateTableStatement stmt = (MySqlCreateTableStatement)statementList.get(0);

        assertEquals(1, statementList.size());
        assertEquals(5, stmt.getTableElementList().size());

        assertEquals("CREATE TABLE IF NOT EXISTS meta.partitions (\n" +
                "\tcluster_name varchar(16),\n" +
                "\ttable_schema varchar(128),\n" +
                "\tview_name varchar(128),\n" +
                "\tcolumn_list text,\n" +
                "\tsql text\n" +
                ")", stmt.toString());

    }

    public void test_2() throws Exception {
        String sql = " CREATE TABLE IF NOT EXISTS meta.partitions (\n" +
                "                cluster_name varchar(16) ,\n" +
                "                table_schema varchar(128), \n" +
                "                partition varchar(128), \n" +
                "                column_list text, \n" +
                "                sql text\n" +
                "                )";

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
        MySqlCreateTableStatement stmt = (MySqlCreateTableStatement)statementList.get(0);

        assertEquals(1, statementList.size());
        assertEquals(5, stmt.getTableElementList().size());

        assertEquals("CREATE TABLE IF NOT EXISTS meta.partitions (\n" +
                "\tcluster_name varchar(16),\n" +
                "\ttable_schema varchar(128),\n" +
                "\tpartition varchar(128),\n" +
                "\tcolumn_list text,\n" +
                "\tsql text\n" +
                ")", stmt.toString());

    }

    public void test_3() throws Exception {
        String sql = "create table IF NOT EXISTS meta.build_table_statistic_info("
                + " cluster_name varchar(16), "
                + " table_schema varchar(128), "
                + " table_name varchar(128), "
                + " key varchar(128), "
                + " value varchar(128), "
                + " table_schema_id varchar(128), "
                + " table_id varchar(128), "
                + " data_version bigint, "
                + " create_time timestamp, "
                + " update_time timestamp"
                + ")";

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
        MySqlCreateTableStatement stmt = (MySqlCreateTableStatement)statementList.get(0);

        assertEquals(1, statementList.size());
        assertEquals(10, stmt.getTableElementList().size());

        assertEquals("CREATE TABLE IF NOT EXISTS meta.build_table_statistic_info (\n" +
                "\tcluster_name varchar(16),\n" +
                "\ttable_schema varchar(128),\n" +
                "\ttable_name varchar(128),\n" +
                "\tkey varchar(128),\n" +
                "\tvalue varchar(128),\n" +
                "\ttable_schema_id varchar(128),\n" +
                "\ttable_id varchar(128),\n" +
                "\tdata_version bigint,\n" +
                "\tcreate_time timestamp,\n" +
                "\tupdate_time timestamp\n" +
                ")", stmt.toString());

    }
}