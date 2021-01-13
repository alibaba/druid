package com.alibaba.druid.bvt.sql.mysql.select;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;

import java.util.List;

public class MySqlSelectTest_190_drds extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "/*TDDL:SOCKET_TIMEOUT=0*/\n" +
                "/*+TDDL({\"extra\":{\"ALLOW_FULL_TABLE_SCAN\":\"TRUE\"}})*/  \n" +
                "/*+TDDL_GROUP({groupIndex:0})*/\n" +
                "select * from xxx_tb;";

//        System.out.println(sql);

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLSelectStatement stmt = (SQLSelectStatement)statementList.get(0);

        assertEquals(1, statementList.size());

        assertEquals("/*TDDL:SOCKET_TIMEOUT=0*/\n" +
                "/*+TDDL({\"extra\":{\"ALLOW_FULL_TABLE_SCAN\":\"TRUE\"}})*/\n" +
                "/*+TDDL_GROUP({groupIndex:0})*/\n" +
                "SELECT *\n" +
                "FROM xxx_tb;", stmt.toString());
    }
}