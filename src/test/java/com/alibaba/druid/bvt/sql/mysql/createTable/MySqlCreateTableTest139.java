package com.alibaba.druid.bvt.sql.mysql.createTable;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCreateTableStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;

import java.util.List;

public class MySqlCreateTableTest139 extends MysqlTest {

    public void test_0() throws Exception {
        String sql = " CREATE TABLE IF NOT EXISTS simiao_alter_partition2 (id int, id2 int, name varchar(30), time timestamp NOT NULL, PRIMARY KEY (id, time), KEY idx_id_time USING BTREE (id, time)) PARTITION BY RANGE (UNIX_TIMESTAMP(time))( PARTITION p0 VALUES LESS THAN (UNIX_TIMESTAMP('2013-01-01 00:00:00')), PARTITION p1 VALUES LESS THAN (UNIX_TIMESTAMP('2013-02-01 00:00:00')), PARTITION p2 VALUES LESS THAN (UNIX_TIMESTAMP('2013-03-01 00:00:00')), PARTITION p3 VALUES LESS THAN (UNIX_TIMESTAMP('2013-04-01 00:00:00')), PARTITION p4 VALUES LESS THAN (UNIX_TIMESTAMP('2013-05-01 00:00:00')), PARTITION p5 VALUES LESS THAN (UNIX_TIMESTAMP('2013-06-01 00:00:00')), PARTITION p6 VALUES LESS THAN (UNIX_TIMESTAMP('2013-07-01 00:00:00')), PARTITION p7 VALUES LESS THAN (UNIX_TIMESTAMP('2013-08-01 00:00:00')), PARTITION p8 VALUES LESS THAN (UNIX_TIMESTAMP('2013-09-01 00:00:00')), PARTITION p10 VALUES LESS THAN (UNIX_TIMESTAMP('2013-10-01 00:00:00')), PARTITION p11 VALUES LESS THAN (UNIX_TIMESTAMP('2013-11-01 00:00:00')), PARTITION p12 VALUES LESS THAN (UNIX_TIMESTAMP('2013-12-01 00:00:00')), PARTITION p13 VALUES LESS THAN (MAXVALUE) ) dbpartition by hash(id) dbpartitions 4;";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        MySqlCreateTableStatement stmt = (MySqlCreateTableStatement)statementList.get(0);

        assertEquals(1, statementList.size());


        assertEquals("CREATE TABLE IF NOT EXISTS simiao_alter_partition2 (\n" +
                "\tid int,\n" +
                "\tid2 int,\n" +
                "\tname varchar(30),\n" +
                "\ttime timestamp NOT NULL,\n" +
                "\tPRIMARY KEY (id, time),\n" +
                "\tKEY idx_id_time USING BTREE (id, time)\n" +
                ")\n" +
                "PARTITION BY RANGE (UNIX_TIMESTAMP(time)) (\n" +
                "\tPARTITION p0 VALUES LESS THAN (UNIX_TIMESTAMP('2013-01-01 00:00:00')),\n" +
                "\tPARTITION p1 VALUES LESS THAN (UNIX_TIMESTAMP('2013-02-01 00:00:00')),\n" +
                "\tPARTITION p2 VALUES LESS THAN (UNIX_TIMESTAMP('2013-03-01 00:00:00')),\n" +
                "\tPARTITION p3 VALUES LESS THAN (UNIX_TIMESTAMP('2013-04-01 00:00:00')),\n" +
                "\tPARTITION p4 VALUES LESS THAN (UNIX_TIMESTAMP('2013-05-01 00:00:00')),\n" +
                "\tPARTITION p5 VALUES LESS THAN (UNIX_TIMESTAMP('2013-06-01 00:00:00')),\n" +
                "\tPARTITION p6 VALUES LESS THAN (UNIX_TIMESTAMP('2013-07-01 00:00:00')),\n" +
                "\tPARTITION p7 VALUES LESS THAN (UNIX_TIMESTAMP('2013-08-01 00:00:00')),\n" +
                "\tPARTITION p8 VALUES LESS THAN (UNIX_TIMESTAMP('2013-09-01 00:00:00')),\n" +
                "\tPARTITION p10 VALUES LESS THAN (UNIX_TIMESTAMP('2013-10-01 00:00:00')),\n" +
                "\tPARTITION p11 VALUES LESS THAN (UNIX_TIMESTAMP('2013-11-01 00:00:00')),\n" +
                "\tPARTITION p12 VALUES LESS THAN (UNIX_TIMESTAMP('2013-12-01 00:00:00')),\n" +
                "\tPARTITION p13 VALUES LESS THAN MAXVALUE\n" +
                ")\n" +
                "DBPARTITION BY hash(id) DBPARTITIONS 4;", stmt.toString());

        assertEquals("create table if not exists simiao_alter_partition2 (\n" +
                "\tid int,\n" +
                "\tid2 int,\n" +
                "\tname varchar(30),\n" +
                "\ttime timestamp not null,\n" +
                "\tprimary key (id, time),\n" +
                "\tkey idx_id_time using BTREE (id, time)\n" +
                ")\n" +
                "partition by range (UNIX_TIMESTAMP(time)) (\n" +
                "\tpartition p0 values less than (UNIX_TIMESTAMP('2013-01-01 00:00:00')),\n" +
                "\tpartition p1 values less than (UNIX_TIMESTAMP('2013-02-01 00:00:00')),\n" +
                "\tpartition p2 values less than (UNIX_TIMESTAMP('2013-03-01 00:00:00')),\n" +
                "\tpartition p3 values less than (UNIX_TIMESTAMP('2013-04-01 00:00:00')),\n" +
                "\tpartition p4 values less than (UNIX_TIMESTAMP('2013-05-01 00:00:00')),\n" +
                "\tpartition p5 values less than (UNIX_TIMESTAMP('2013-06-01 00:00:00')),\n" +
                "\tpartition p6 values less than (UNIX_TIMESTAMP('2013-07-01 00:00:00')),\n" +
                "\tpartition p7 values less than (UNIX_TIMESTAMP('2013-08-01 00:00:00')),\n" +
                "\tpartition p8 values less than (UNIX_TIMESTAMP('2013-09-01 00:00:00')),\n" +
                "\tpartition p10 values less than (UNIX_TIMESTAMP('2013-10-01 00:00:00')),\n" +
                "\tpartition p11 values less than (UNIX_TIMESTAMP('2013-11-01 00:00:00')),\n" +
                "\tpartition p12 values less than (UNIX_TIMESTAMP('2013-12-01 00:00:00')),\n" +
                "\tpartition p13 values less than maxvalue\n" +
                ")\n" +
                "dbpartition by hash(id) dbpartitions 4;", stmt.toLowerCaseString());

    }





}