package com.alibaba.druid.bvt.sql.mysql.select;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLBetweenExpr;
import com.alibaba.druid.sql.ast.expr.SQLInListExpr;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.parser.SQLParserFeature;
import com.alibaba.druid.sql.visitor.ParameterizedOutputVisitorUtils;
import com.alibaba.druid.sql.visitor.VisitorFeature;
import com.alibaba.druid.util.JdbcConstants;

import java.util.List;

public class MySqlSelectTest_157 extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "SELECT 1 " +
                "FROM corona_select_one_db_one_tb AS layer_0_left_tb " +
                "RIGHT JOIN corona_select_multi_db_multi_tb AS layer_0_right_tb " +
                "   ON layer_0_right_tb.mediumint_test=layer_0_right_tb.char_test " +
                "WHERE (layer_0_right_tb.timestamp_test BETWEEN 'x-3' AND ROW(3, 4) NOT IN (ROW(1, 2 ),ROW(3, 4)))";
//
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL, SQLParserFeature.TDDLHint);
        SQLSelectStatement stmt = (SQLSelectStatement)statementList.get(0);

        assertEquals(1, statementList.size());

        SQLBetweenExpr where = (SQLBetweenExpr) stmt.getSelect().getQueryBlock().getWhere();
        assertEquals(SQLInListExpr.class, where.getEndExpr().getClass());

        assertEquals("SELECT 1\n" +
                "FROM corona_select_one_db_one_tb layer_0_left_tb\n" +
                "\tRIGHT JOIN corona_select_multi_db_multi_tb layer_0_right_tb ON layer_0_right_tb.mediumint_test = layer_0_right_tb.char_test\n" +
                "WHERE layer_0_right_tb.timestamp_test BETWEEN 'x-3' AND (ROW(3, 4) NOT IN (ROW(1, 2), ROW(3, 4)))", stmt.toString());

        assertEquals("SELECT ?\n" +
                        "FROM corona_select_one_db_one_tb layer_0_left_tb\n" +
                        "\tRIGHT JOIN corona_select_multi_db_multi_tb layer_0_right_tb ON layer_0_right_tb.mediumint_test = layer_0_right_tb.char_test\n" +
                        "WHERE layer_0_right_tb.timestamp_test BETWEEN ? AND (ROW(?, ?) NOT IN (ROW(?, ?), ROW(?, ?)))"
                , ParameterizedOutputVisitorUtils.parameterize(sql, JdbcConstants.MYSQL, VisitorFeature.OutputParameterizedZeroReplaceNotUseOriginalSql));


    }

}