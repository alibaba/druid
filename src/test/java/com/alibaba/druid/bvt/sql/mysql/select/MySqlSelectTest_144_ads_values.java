package com.alibaba.druid.bvt.sql.mysql.select;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.visitor.ParameterizedOutputVisitorUtils;
import com.alibaba.druid.sql.visitor.VisitorFeature;
import com.alibaba.druid.util.JdbcConstants;

import java.util.List;

public class MySqlSelectTest_144_ads_values extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "SELECT * FROM (\n" +
                "VALUES\n" +
                "    (1, 'a'),\n" +
                "    (2, 'b'),\n" +
                "    (3, 'c')\n" +
                "    ) AS t (id, name)";

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
        SQLStatement stmt = statementList.get(0);
        System.out.println(stmt);

        assertEquals(1, statementList.size());

        assertEquals("SELECT *\n" +
                "FROM (\n" +
                "\tVALUES (1, 'a'), \n" +
                "\t(2, 'b'), \n" +
                "\t(3, 'c')\n" +
                ") AS t (id, name)", stmt.toString());

        assertEquals("SELECT *\n" +
                        "FROM (\n" +
                        "\tVALUES (?, ?), \n" +
                        "\t(?, ?), \n" +
                        "\t(?, ?)\n" +
                        ") AS t (id, name)"
                , ParameterizedOutputVisitorUtils.parameterize(sql, JdbcConstants.MYSQL, VisitorFeature.OutputParameterizedZeroReplaceNotUseOriginalSql));
    }


}