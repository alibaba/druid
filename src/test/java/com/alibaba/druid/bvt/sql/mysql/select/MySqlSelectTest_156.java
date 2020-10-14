package com.alibaba.druid.bvt.sql.mysql.select;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.parser.SQLParserFeature;
import com.alibaba.druid.sql.visitor.ParameterizedOutputVisitorUtils;
import com.alibaba.druid.sql.visitor.VisitorFeature;
import com.alibaba.druid.util.JdbcConstants;

import java.util.List;

public class MySqlSelectTest_156 extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "SELECT SQL_SMALL_RESULT ((NULL) is  not  FALSE) \n" +
                "FROM corona_select_multi_db_one_tb AS layer_0_left_tb \n" +
                "RIGHT JOIN corona_select_one_db_multi_tb AS layer_0_right_tb \n" +
                "   ON layer_0_right_tb.smallint_test=layer_0_right_tb.date_test \n" +
                "WHERE layer_0_right_tb.time_test='x6' NOT BETWEEN 96 AND layer_0_right_tb.bigint_test;\n";
//
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL, SQLParserFeature.TDDLHint);
        SQLSelectStatement stmt = (SQLSelectStatement)statementList.get(0);

        assertEquals(1, statementList.size());

        assertEquals("SELECT SQL_SMALL_RESULT NULL IS NOT false\n" +
                "FROM corona_select_multi_db_one_tb layer_0_left_tb\n" +
                "\tRIGHT JOIN corona_select_one_db_multi_tb layer_0_right_tb ON layer_0_right_tb.smallint_test = layer_0_right_tb.date_test\n" +
                "WHERE layer_0_right_tb.time_test = 'x6' NOT BETWEEN 96 AND layer_0_right_tb.bigint_test;", stmt.toString());

        assertEquals("SELECT SQL_SMALL_RESULT NULL IS NOT false\n" +
                        "FROM corona_select_multi_db_one_tb layer_0_left_tb\n" +
                        "\tRIGHT JOIN corona_select_one_db_multi_tb layer_0_right_tb ON layer_0_right_tb.smallint_test = layer_0_right_tb.date_test\n" +
                        "WHERE layer_0_right_tb.time_test = ? NOT BETWEEN ? AND layer_0_right_tb.bigint_test;"
                , ParameterizedOutputVisitorUtils.parameterize(sql, JdbcConstants.MYSQL, VisitorFeature.OutputParameterizedZeroReplaceNotUseOriginalSql));


    }

}