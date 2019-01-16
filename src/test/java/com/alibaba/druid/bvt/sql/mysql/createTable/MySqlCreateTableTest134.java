package com.alibaba.druid.bvt.sql.mysql.createTable;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCreateTableStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;

import java.util.List;

public class MySqlCreateTableTest134 extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "create table xuhan3(\n" +
                "id int not null AUTO_INCREMENT primary key, \n" +
                "name char(40), SimpleDate date, \n" +
                "SimpleDate_dayofweek tinyint(4) GENERATED ALWAYS AS (dayofweek(SimpleDate)) VIRTUAL, \n" +
                "KEY SimpleDate_dayofweek (SimpleDate_dayofweek));";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        MySqlCreateTableStatement stmt = (MySqlCreateTableStatement)statementList.get(0);

        assertEquals(1, statementList.size());


        assertEquals("CREATE TABLE xuhan3 (\n" +
                "\tid int NOT NULL PRIMARY KEY AUTO_INCREMENT,\n" +
                "\tname char(40),\n" +
                "\tSimpleDate date,\n" +
                "\tSimpleDate_dayofweek tinyint(4) GENERATED ALWAYS AS (dayofweek(SimpleDate)) VIRTUAL,\n" +
                "\tKEY SimpleDate_dayofweek (SimpleDate_dayofweek)\n" +
                ");", stmt.toString());

        assertEquals("create table xuhan3 (\n" +
                "\tid int not null primary key auto_increment,\n" +
                "\tname char(40),\n" +
                "\tSimpleDate date,\n" +
                "\tSimpleDate_dayofweek tinyint(4) generated always as (dayofweek(SimpleDate)) virtual,\n" +
                "\tkey SimpleDate_dayofweek (SimpleDate_dayofweek)\n" +
                ");", stmt.toLowerCaseString());

    }




}