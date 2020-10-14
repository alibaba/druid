package com.alibaba.druid.bvt.sql.mysql.select;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.util.JdbcConstants;

import java.util.List;

public class MySqlSelectTest_169_not_in extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "SELECT ((10)!=(( (HEX ('abc' )) is FALSE )) ),( (( (( (( 'a')&& (null )) not in(layer_1_left_tb.bigint_test, layer_0_right_tb.smallint_test,layer_1_left_tb.datetime_test,( EXPORT_SET (6, layer_1_left_tb.year_test,'0', ':', 66 ))) )) <(layer_1_left_tb.decimal_test) )) not in(layer_1_left_tb.mediumint_test, layer_1_left_tb.double_test,layer_1_left_tb.year_test,( 1+'1')) )FROM corona_select_one_db_multi_tb AS layer_1_left_tb RIGHT JOIN corona_select_multi_db_one_tb AS layer_1_right_tb ON layer_1_right_tb.datetime_test=layer_1_left_tb.datetime_test RIGHT JOIN corona_select_one_db_multi_tb AS layer_0_right_tb ON layer_0_right_tb.tinyint_1bit_test=layer_1_left_tb.tinyint_1bit_test;";
//
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
        SQLSelectStatement stmt = (SQLSelectStatement)statementList.get(0);

        assertEquals(1, statementList.size());

        assertEquals("SELECT 10 != (HEX('abc') IS false), ((('a'\n" +
                "\tAND NULL) NOT IN (layer_1_left_tb.bigint_test, layer_0_right_tb.smallint_test, layer_1_left_tb.datetime_test, EXPORT_SET(6, layer_1_left_tb.year_test, '0', ':', 66))) < layer_1_left_tb.decimal_test) NOT IN (layer_1_left_tb.mediumint_test, layer_1_left_tb.double_test, layer_1_left_tb.year_test, 1 + '1')\n" +
                "FROM corona_select_one_db_multi_tb layer_1_left_tb\n" +
                "\tRIGHT JOIN corona_select_multi_db_one_tb layer_1_right_tb ON layer_1_right_tb.datetime_test = layer_1_left_tb.datetime_test\n" +
                "\tRIGHT JOIN corona_select_one_db_multi_tb layer_0_right_tb ON layer_0_right_tb.tinyint_1bit_test = layer_1_left_tb.tinyint_1bit_test;", stmt.toString());
    }
}