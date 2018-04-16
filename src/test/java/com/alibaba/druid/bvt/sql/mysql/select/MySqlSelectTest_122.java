package com.alibaba.druid.bvt.sql.mysql.select;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;

import java.util.List;

public class MySqlSelectTest_122 extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "SELECT 'test1123' from dual WHERE 'test1123' like \"%\"'test'\"%\"";

//        System.out.println(sql);

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLSelectStatement stmt = (SQLSelectStatement)statementList.get(0);

        assertEquals(1, statementList.size());

        assertEquals("SELECT 'test1123'\n" +
                "FROM dual\n" +
                "WHERE 'test1123' LIKE '%test%'", stmt.toString());
    }
}