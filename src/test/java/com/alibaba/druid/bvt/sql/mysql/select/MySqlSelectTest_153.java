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

public class MySqlSelectTest_153 extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "SELECT ((layer_1_column_0)|(NULLIF(NULL,null )))FROM (SELECT NULL is NULL AS layer_1_column_0 FROM corona_select_multi_db_one_tb WHERE 'a' AND 'b') AS layer_0_table WHERE ! ~ 25 IS NULL;";
//
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL, SQLParserFeature.TDDLHint);
        SQLSelectStatement stmt = (SQLSelectStatement)statementList.get(0);

        assertEquals(1, statementList.size());

        assertEquals("SELECT layer_1_column_0 | NULLIF(NULL, NULL)\n" +
                "FROM (\n" +
                "\tSELECT NULL IS NULL AS layer_1_column_0\n" +
                "\tFROM corona_select_multi_db_one_tb\n" +
                "\tWHERE 'a'\n" +
                "\t\tAND 'b'\n" +
                ") layer_0_table\n" +
                "WHERE (!(~25)) IS NULL;", stmt.toString());

        assertEquals("SELECT layer_1_column_0 | NULLIF(NULL, NULL)\n" +
                        "FROM (\n" +
                        "\tSELECT NULL IS NULL AS layer_1_column_0\n" +
                        "\tFROM corona_select_multi_db_one_tb\n" +
                        "\tWHERE ?\n" +
                        "\t\tAND ?\n" +
                        ") layer_0_table\n" +
                        "WHERE (!(~?)) IS NULL;"
                , ParameterizedOutputVisitorUtils.parameterize(sql, JdbcConstants.MYSQL, VisitorFeature.OutputParameterizedZeroReplaceNotUseOriginalSql));


    }

}