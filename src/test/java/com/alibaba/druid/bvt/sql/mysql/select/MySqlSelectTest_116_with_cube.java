package com.alibaba.druid.bvt.sql.mysql.select;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;

import java.util.List;

public class MySqlSelectTest_116_with_cube extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "SELECT string_test, time_test, sum(int_test2) as sum_int FROM test_realtime1 GROUP BY ROLLUP (string_test, time_test) ORDER BY sum_int, string_test, time_test";

//        System.out.println(sql);

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLSelectStatement stmt = (SQLSelectStatement)statementList.get(0);

        assertEquals(1, statementList.size());

        assertEquals("SELECT string_test, time_test, sum(int_test2) AS sum_int\n" +
                "FROM test_realtime1\n" +
                "GROUP BY ROLLUP (string_test, time_test)\n" +
                "ORDER BY sum_int, string_test, time_test", stmt.toString());
    }
}