package com.alibaba.druid.bvt.sql.mysql.select;

import com.alibaba.fastjson.JSON;
import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.parser.SQLParserFeature;
import com.alibaba.druid.sql.visitor.ParameterizedOutputVisitorUtils;
import com.alibaba.druid.sql.visitor.VisitorFeature;
import com.alibaba.druid.util.JdbcConstants;

import java.util.ArrayList;
import java.util.List;

public class MySqlSelectTest_162 extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "WITH RECURSIVE cte (n) AS\n" +
                "(\n" +
                "  SELECT 1\n" +
                "  UNION ALL\n" +
                "  SELECT n + 1 FROM cte\n" +
                ")\n" +
                "SELECT /*+ MAX_EXECUTION_TIME(1000) */ * FROM cte;";
//
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL, SQLParserFeature.TDDLHint);
        SQLSelectStatement stmt = (SQLSelectStatement)statementList.get(0);

        assertEquals(1, statementList.size());

        assertEquals("WITH RECURSIVE cte (n) AS (\n" +
                "\t\tSELECT 1\n" +
                "\t\tUNION ALL\n" +
                "\t\tSELECT n + 1\n" +
                "\t\tFROM cte\n" +
                "\t)\n" +
                "SELECT /*+ MAX_EXECUTION_TIME(1000) */ *\n" +
                "FROM cte;", stmt.toString());

        assertEquals("WITH RECURSIVE cte (n) AS (\n" +
                        "\t\tSELECT ?\n" +
                        "\t\tUNION ALL\n" +
                        "\t\tSELECT n + ?\n" +
                        "\t\tFROM cte\n" +
                        "\t)\n" +
                        "SELECT /*+ MAX_EXECUTION_TIME(1000) */ *\n" +
                        "FROM cte;"
                , ParameterizedOutputVisitorUtils.parameterize(sql, JdbcConstants.MYSQL, VisitorFeature.OutputParameterizedZeroReplaceNotUseOriginalSql));

        List<Object> params = new ArrayList<Object>();
        assertEquals("WITH RECURSIVE cte (n) AS (\n" +
                        "\t\tSELECT ?\n" +
                        "\t\tUNION ALL\n" +
                        "\t\tSELECT n + ?\n" +
                        "\t\tFROM cte\n" +
                        "\t)\n" +
                        "SELECT /*+ MAX_EXECUTION_TIME(1000) */ *\n" +
                        "FROM cte;"
                , ParameterizedOutputVisitorUtils.parameterize(sql, JdbcConstants.MYSQL, params, VisitorFeature.OutputParameterizedZeroReplaceNotUseOriginalSql));

        assertEquals(2, params.size());
        assertEquals("1", JSON.toJSONString(params.get(0)));


    }

}