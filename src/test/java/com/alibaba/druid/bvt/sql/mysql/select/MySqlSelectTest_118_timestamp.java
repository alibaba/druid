package com.alibaba.druid.bvt.sql.mysql.select;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;

import java.util.List;

public class MySqlSelectTest_118_timestamp extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "select * from test1 where TIMESTAMP '2019-1-1 00:00:00' = timestamp_test";

//        System.out.println(sql);

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLSelectStatement stmt = (SQLSelectStatement)statementList.get(0);

        assertEquals(1, statementList.size());

        assertEquals("SELECT *\n" +
                "FROM test1\n" +
                "WHERE TIMESTAMP '2019-1-1 00:00:00' = timestamp_test", stmt.toString());
    }
}