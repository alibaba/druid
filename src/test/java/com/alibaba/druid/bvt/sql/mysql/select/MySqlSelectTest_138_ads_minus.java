package com.alibaba.druid.bvt.sql.mysql.select;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.ast.statement.SQLUnionQuery;
import com.alibaba.druid.util.JdbcConstants;

import java.util.List;

public class MySqlSelectTest_138_ads_minus extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "SELECT DISTINCT o_orderkey\n" +
                "FROM simple_query2.orders__0\n" +
                "WHERE o_orderdate > '1995-09-31'\n" +
                "UNION\n" +
                "SELECT DISTINCT o_orderkey\n" +
                "FROM simple_query2.orders__0\n" +
                "WHERE o_orderdate > '1994-09-31'\n" +
                "INTERSECT\n" +
                "SELECT DISTINCT o_orderkey\n" +
                "FROM simple_query2.orders__0\n" +
                "WHERE o_orderdate > '1996-09-31'\n" +
                "MINUS\n" +
                "SELECT DISTINCT o_orderkey\n" +
                "FROM simple_query2.orders__0\n" +
                "WHERE o_orderdate > '1994-09-31'\n";

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
        SQLSelectStatement stmt = (SQLSelectStatement)statementList.get(0);

        assertEquals(1, statementList.size());

        assertEquals("SELECT DISTINCT o_orderkey\n" +
                "FROM simple_query2.orders__0\n" +
                "WHERE o_orderdate > '1995-09-31'\n" +
                "UNION\n" +
                "SELECT DISTINCT o_orderkey\n" +
                "FROM simple_query2.orders__0\n" +
                "WHERE o_orderdate > '1994-09-31'\n" +
                "INTERSECT\n" +
                "SELECT DISTINCT o_orderkey\n" +
                "FROM simple_query2.orders__0\n" +
                "WHERE o_orderdate > '1996-09-31'\n" +
                "MINUS\n" +
                "SELECT DISTINCT o_orderkey\n" +
                "FROM simple_query2.orders__0\n" +
                "WHERE o_orderdate > '1994-09-31'", stmt.toString());

        SQLUnionQuery q1 = (SQLUnionQuery) stmt.getSelect().getQuery();
        assertTrue(q1.getRight() instanceof SQLSelectQueryBlock);

        SQLUnionQuery q2 = (SQLUnionQuery) q1.getLeft();
        assertTrue(q2.getRight() instanceof SQLSelectQueryBlock);


        SQLUnionQuery q3 = (SQLUnionQuery) q2.getLeft();
        assertTrue(q3.getRight() instanceof SQLSelectQueryBlock);
        assertTrue(q3.getLeft() instanceof SQLSelectQueryBlock);
    }


}