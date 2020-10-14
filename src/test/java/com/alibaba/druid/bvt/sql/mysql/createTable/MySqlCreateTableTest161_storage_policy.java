package com.alibaba.druid.bvt.sql.mysql.createTable;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCreateTableStatement;
import com.alibaba.druid.sql.parser.ParserException;


public class MySqlCreateTableTest161_storage_policy extends MysqlTest {

    public void test_0() throws Exception {
        //for ADB
        String sql = "create table event_log(log_id bigint, log_time datetime)\n" +
                "distribute by hash(log_id)\n" +
                "partition by value(date_format('%Y%m%d')) lifecycle 180\n" +
                "storage_policy = 'HOT'";

        MySqlCreateTableStatement stmt = (MySqlCreateTableStatement) SQLUtils.parseSingleMysqlStatement(sql);

        assertEquals("CREATE TABLE event_log (\n" +
                "\tlog_id bigint,\n" +
                "\tlog_time datetime\n" +
                ") STORAGE_POLICY = 'HOT'\n" +
                "DISTRIBUTE BY HASH(log_id)\n" +
                "PARTITION BY VALUE (date_format('%Y%m%d')) LIFECYCLE 180", stmt.toString());
    }

    public void test_1() throws Exception {
        //for ADB
        String sql = "create table event_log(log_id bigint, log_time datetime)\n" +
                "distribute by hash(log_id)\n" +
                "partition by value(date_format('%Y%m%d')) lifecycle 180\n" +
                "storage_policy = 'COLD';";

        MySqlCreateTableStatement stmt = (MySqlCreateTableStatement) SQLUtils.parseSingleMysqlStatement(sql);

        assertEquals("CREATE TABLE event_log (\n" +
                "\tlog_id bigint,\n" +
                "\tlog_time datetime\n" +
                ") STORAGE_POLICY = 'COLD'\n" +
                "DISTRIBUTE BY HASH(log_id)\n" +
                "PARTITION BY VALUE (date_format('%Y%m%d')) LIFECYCLE 180;", stmt.toString());
    }

    public void test_2() throws Exception {
        //for ADB
        String sql = "create table event_log(log_id bigint, log_time datetime)\n" +
                "distribute by hash(log_id)\n" +
                "partition by value(date_format('%Y%m%d')) lifecycle 180\n" +
                "storage_policy = 'MIXED' hot_partition_count = 10;";

        MySqlCreateTableStatement stmt = (MySqlCreateTableStatement) SQLUtils.parseSingleMysqlStatement(sql);

        assertEquals("CREATE TABLE event_log (\n" +
                "\tlog_id bigint,\n" +
                "\tlog_time datetime\n" +
                ") STORAGE_POLICY = 'MIXED' HOT_PARTITION_COUNT = 10\n" +
                "DISTRIBUTE BY HASH(log_id)\n" +
                "PARTITION BY VALUE (date_format('%Y%m%d')) LIFECYCLE 180;", stmt.toString());
    }

    public void test_3() throws Exception {
        //for ADB
        String sql = "create table event_log(log_id bigint, log_time datetime)\n" +
                "distribute by hash(log_id)\n" +
                "partition by value(date_format('%Y%m%d')) lifecycle 180\n" +
                "storage_policy = 'MIXED' hot_partition_count = 0.1;";

        try {
            SQLUtils.parseSingleMysqlStatement(sql);
            fail();
        } catch (ParserException e) {

        }

    }
}