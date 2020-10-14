package com.alibaba.druid.bvt.sql.mysql.select;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.util.JdbcConstants;

import java.util.List;

public class MySqlSelectTest_179 extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "SELECT zip(ARRAY[1, 2], ARRAY['1b', null, '3b'])";

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
        SQLSelectStatement stmt = (SQLSelectStatement)statementList.get(0);

        assertEquals(1, statementList.size());

        assertEquals("SELECT zip(ARRAY[1, 2], ARRAY['1b', NULL, '3b'])", stmt.toString());


    }

    public void test_1() throws Exception {
        String sql = "SELECT transform(ARRAY [], x -> x + 1);";

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
        SQLSelectStatement stmt = (SQLSelectStatement)statementList.get(0);

        assertEquals(1, statementList.size());

        assertEquals("SELECT transform(ARRAY[], x -> (x + 1));", stmt.toString());


    }

    public void test_2() throws Exception {
        String sql = "SELECT reduce(ARRAY [5, 20, NULL, 50], 0, (s, x) -> IF(x IS NULL, s, s + x), s -> s);";

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
        SQLSelectStatement stmt = (SQLSelectStatement)statementList.get(0);

        assertEquals(1, statementList.size());

        assertEquals("SELECT reduce(ARRAY[5, 20, NULL, 50], 0, (s, x) -> IF(x IS NULL, s, s + x), s -> s);", stmt.toString());


    }

}