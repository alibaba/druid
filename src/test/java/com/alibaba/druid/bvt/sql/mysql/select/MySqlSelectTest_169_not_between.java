package com.alibaba.druid.bvt.sql.mysql.select;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.util.JdbcConstants;

import java.util.List;

public class MySqlSelectTest_169_not_between extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "select * from t where (a && b) not between 1 and 2;";
//
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
        SQLSelectStatement stmt = (SQLSelectStatement)statementList.get(0);

        assertEquals(1, statementList.size());

        assertEquals("SELECT *\n" +
                "FROM t\n" +
                "WHERE (a\n" +
                "AND b) NOT BETWEEN 1 AND 2;", stmt.toString());
    }

    public void test_1() throws Exception {
        String sql = "select (a && b) not between 1 and 2;";
//
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
        SQLSelectStatement stmt = (SQLSelectStatement)statementList.get(0);

        assertEquals(1, statementList.size());

        assertEquals("SELECT (a\n" +
                "\tAND b) NOT BETWEEN 1 AND 2;", stmt.toString());
    }

}