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

public class MySqlSelectTest_155 extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "SELECT SQL_NO_CACHE ((layer_0_right_tb.integer_test) is TRUE)FROM corona_select_multi_db_multi_tb AS layer_0_left_tb LEFT JOIN corona_select_one_db_one_tb AS layer_0_right_tb ON layer_0_right_tb.decimal_test=layer_0_left_tb.varchar_test WHERE '18015376320243458'=18015376320243458 NOT BETWEEN layer_0_right_tb.tinyint_1bit_test AND 'x-3'";
//
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL, SQLParserFeature.TDDLHint);
        SQLSelectStatement stmt = (SQLSelectStatement)statementList.get(0);

        assertEquals(1, statementList.size());

        assertEquals("SELECT SQL_NO_CACHE layer_0_right_tb.integer_test IS true\n" +
                "FROM corona_select_multi_db_multi_tb layer_0_left_tb\n" +
                "\tLEFT JOIN corona_select_one_db_one_tb layer_0_right_tb ON layer_0_right_tb.decimal_test = layer_0_left_tb.varchar_test\n" +
                "WHERE '18015376320243458' = 18015376320243458 NOT BETWEEN layer_0_right_tb.tinyint_1bit_test AND 'x-3'", stmt.toString());

        assertEquals("SELECT SQL_NO_CACHE layer_0_right_tb.integer_test IS true\n" +
                        "FROM corona_select_multi_db_multi_tb layer_0_left_tb\n" +
                        "\tLEFT JOIN corona_select_one_db_one_tb layer_0_right_tb ON layer_0_right_tb.decimal_test = layer_0_left_tb.varchar_test\n" +
                        "WHERE ? = ? NOT BETWEEN layer_0_right_tb.tinyint_1bit_test AND ?"
                , ParameterizedOutputVisitorUtils.parameterize(sql, JdbcConstants.MYSQL, VisitorFeature.OutputParameterizedZeroReplaceNotUseOriginalSql));


    }

}