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

public class MySqlSelectTest_154 extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "SELECT 1 is NULL,(~(NULLIF(1,1 ))) \n" +
                "FROM corona_select_one_db_one_tb AS layer_0_left_tb \n" +
                "  RIGHT JOIN corona_select_multi_db_one_tb AS layer_0_right_tb \n" +
                "    ON layer_0_right_tb.tinyint_1bit_test=layer_0_right_tb.decimal_test \n" +
                "WHERE 1 + '1' IS NULL != 30-layer_0_right_tb.time_test \n" +
                "  NOT IN (layer_0_left_tb.decimal_test,layer_0_right_tb.tinyint_test,layer_0_left_tb.integer_test, RPAD(NULL,0,layer_0_left_tb.year_test))";
//
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL, SQLParserFeature.TDDLHint);
        SQLSelectStatement stmt = (SQLSelectStatement)statementList.get(0);

        assertEquals(1, statementList.size());

        assertEquals("SELECT 1 IS NULL, ~NULLIF(1, 1)\n" +
                "FROM corona_select_one_db_one_tb layer_0_left_tb\n" +
                "\tRIGHT JOIN corona_select_multi_db_one_tb layer_0_right_tb ON layer_0_right_tb.tinyint_1bit_test = layer_0_right_tb.decimal_test\n" +
                "WHERE (1 + '1' IS NULL) != 30 - layer_0_right_tb.time_test NOT IN (layer_0_left_tb.decimal_test, layer_0_right_tb.tinyint_test, layer_0_left_tb.integer_test, RPAD(NULL, 0, layer_0_left_tb.year_test))", stmt.toString());

        assertEquals("SELECT ? IS NULL, ~NULLIF(?, ?)\n" +
                        "FROM corona_select_one_db_one_tb layer_0_left_tb\n" +
                        "\tRIGHT JOIN corona_select_multi_db_one_tb layer_0_right_tb ON layer_0_right_tb.tinyint_1bit_test = layer_0_right_tb.decimal_test\n" +
                        "WHERE (? + ? IS NULL) != ? - layer_0_right_tb.time_test NOT IN (layer_0_left_tb.decimal_test, layer_0_right_tb.tinyint_test, layer_0_left_tb.integer_test, RPAD(NULL, ?, layer_0_left_tb.year_test))"
                , ParameterizedOutputVisitorUtils.parameterize(sql, JdbcConstants.MYSQL, VisitorFeature.OutputParameterizedZeroReplaceNotUseOriginalSql));


    }

}