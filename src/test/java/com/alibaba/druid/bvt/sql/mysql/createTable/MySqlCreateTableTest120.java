package com.alibaba.druid.bvt.sql.mysql.createTable;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCreateTableStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.parser.SQLParserFeature;

import java.util.List;

public class MySqlCreateTableTest120 extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "CREATE TABLE sal_emp (\n" +
                "    name            int auto_increment unit count 2 index 1 step 0,\n" +
                "    pay_by_quarter  int[],\n" +
                "    schedule        long[256]\n" +
                ");";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        MySqlCreateTableStatement stmt = (MySqlCreateTableStatement)statementList.get(0);

        assertEquals(1, statementList.size());


        assertEquals("CREATE TABLE sal_emp (\n" +
                "\tname int AUTO_INCREMENT UNIT COUNT 2 INDEX 1 STEP 0,\n" +
                "\tpay_by_quarter int[],\n" +
                "\tschedule long[256]\n" +
                ");", stmt.toString());

    }

    public void test_1() throws Exception {
        String sql = "CREATE TABLE sal_emp (\n" +
                "\tname int PRIMARY KEY AUTO_INCREMENT UNIT COUNT 666 INDEX 8 STEP 11110,\n" +
                "\tpay_by_quarter ARRAY<int>,\n" +
                "\tschedule ARRAY<long>(256)\n" +
                ");";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        MySqlCreateTableStatement stmt = (MySqlCreateTableStatement)statementList.get(0);

        assertEquals(1, statementList.size());


        assertEquals("CREATE TABLE sal_emp (\n" +
                "\tname int PRIMARY KEY AUTO_INCREMENT UNIT COUNT 666 INDEX 8 STEP 11110,\n" +
                "\tpay_by_quarter ARRAY<int>,\n" +
                "\tschedule ARRAY<long>(256)\n" +
                ");", stmt.toString());

    }

    public void test_2() throws Exception {
        String sql = "CREATE TABLE sal_emp (\n" +
                "\tname int AUTO_INCREMENT UNIT COUNT 666 INDEX 8 STEP 11110,\n" +
                "\tpay_by_quarter ARRAY<int>,\n" +
                "\tschedule ARRAY<long>(256),\n" +
                "primary key (name)" +
                ");";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        MySqlCreateTableStatement stmt = (MySqlCreateTableStatement)statementList.get(0);

        assertEquals(1, statementList.size());


        assertEquals("CREATE TABLE sal_emp (\n" +
                "\tname int AUTO_INCREMENT UNIT COUNT 666 INDEX 8 STEP 11110,\n" +
                "\tpay_by_quarter ARRAY<int>,\n" +
                "\tschedule ARRAY<long>(256),\n" +
                "\tPRIMARY KEY (name)\n" +
                ");", stmt.toString());

    }

    public void test_3() throws Exception {
        String sql = "CREATE TABLE `sch1`.`sal_emp` (\n" +
                "\tname int AUTO_INCREMENT UNIT COUNT 666 INDEX 8 STEP 11110,\n" +
                "\tpay_by_quarter ARRAY<int>,\n" +
                "\tschedule ARRAY<long>(256),\n" +
                "primary key (name)" +
                ");";

        MySqlStatementParser parser = new MySqlStatementParser(sql, SQLParserFeature.IgnoreNameQuotes);
        List<SQLStatement> statementList = parser.parseStatementList();
        MySqlCreateTableStatement stmt = (MySqlCreateTableStatement)statementList.get(0);

        assertEquals(1, statementList.size());


        assertEquals("CREATE TABLE sch1.sal_emp (\n" +
                "\tname int AUTO_INCREMENT UNIT COUNT 666 INDEX 8 STEP 11110,\n" +
                "\tpay_by_quarter ARRAY<int>,\n" +
                "\tschedule ARRAY<long>(256),\n" +
                "\tPRIMARY KEY (name)\n" +
                ");", stmt.toString());

    }

}