package com.alibaba.druid.bvt.sql.mysql.createTable;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCreateTableStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;

import java.util.List;

public class MySqlCreateTableTest118 extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "CREATE TABLE sal_emp (\n" +
                "    name            int,\n" +
                "    pay_by_quarter  int[],\n" +
                "    schedule        long[256]\n" +
                ");";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        MySqlCreateTableStatement stmt = (MySqlCreateTableStatement)statementList.get(0);

        assertEquals(1, statementList.size());


        assertEquals("CREATE TABLE sal_emp (\n" +
                "\tname int,\n" +
                "\tpay_by_quarter int[],\n" +
                "\tschedule long[256]\n" +
                ");", stmt.toString());

    }

    public void test_1() throws Exception {
        String sql = "CREATE TABLE sal_emp (\n" +
                "    name            int,\n" +
                "    pay_by_quarter  array<int>,\n" +
                "    schedule        array<long>(256)\n" +
                ");";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        MySqlCreateTableStatement stmt = (MySqlCreateTableStatement)statementList.get(0);

        assertEquals(1, statementList.size());


        assertEquals("CREATE TABLE sal_emp (\n" +
                "\tname int,\n" +
                "\tpay_by_quarter ARRAY<int>,\n" +
                "\tschedule ARRAY<long>(256)\n" +
                ");", stmt.toString());

    }

}