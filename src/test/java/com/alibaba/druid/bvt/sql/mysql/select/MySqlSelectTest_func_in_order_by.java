package com.alibaba.druid.bvt.sql.mysql.select;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.ast.SQLDataTypeRefExpr;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLMethodInvokeExpr;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import org.junit.Assert;

import java.util.List;

/**
 * version 1.0
 * Author zzy
 * Date 2019-07-17 10:00
 */
public class MySqlSelectTest_func_in_order_by extends MysqlTest {

    public void test_0() {
        String sql = "select * from tb order by convert(ifnull(y,'999'),signed);";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);

        Assert.assertEquals(1, statementList.size());

        Assert.assertEquals("SELECT *\n" +
                "FROM tb\n" +
                "ORDER BY convert(ifnull(y, '999'), signed);", stmt.toString());

        Assert.assertTrue(stmt instanceof SQLSelectStatement);

        SQLSelectStatement select = (SQLSelectStatement) stmt;

        Assert.assertTrue(select.getSelect().getQueryBlock().getOrderBy().getItems().get(0).getExpr() instanceof SQLMethodInvokeExpr);

        SQLMethodInvokeExpr expr = (SQLMethodInvokeExpr) select.getSelect().getQueryBlock().getOrderBy().getItems().get(0).getExpr();

        Assert.assertTrue(expr.getArguments().get(1) instanceof SQLDataTypeRefExpr);
    }

}
