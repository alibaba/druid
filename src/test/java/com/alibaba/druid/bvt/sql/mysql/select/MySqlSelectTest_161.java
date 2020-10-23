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

public class MySqlSelectTest_161 extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "SELECT\n" +
                "DATE_FORMAT(FROM_UNIXTIME(`time` / 1000), '%Y-%m-%d %H:%i:%s'), `time`\n" +
                "FROM pvtz_day\n" +
                "ORDER BY `time` DESC\n" +
                "\n";
//
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL, SQLParserFeature.TDDLHint);
        SQLSelectStatement stmt = (SQLSelectStatement)statementList.get(0);

        assertEquals(1, statementList.size());

        assertEquals("SELECT DATE_FORMAT(FROM_UNIXTIME(`time` / 1000), '%Y-%m-%d %H:%i:%s')\n" +
                "\t, `time`\n" +
                "FROM pvtz_day\n" +
                "ORDER BY `time` DESC", stmt.toString());

        assertEquals("SELECT DATE_FORMAT(FROM_UNIXTIME(`time` / ?), '%Y-%m-%d %H:%i:%s')\n" +
                        "\t, `time`\n" +
                        "FROM pvtz_day\n" +
                        "ORDER BY `time` DESC"
                , ParameterizedOutputVisitorUtils.parameterize(sql, JdbcConstants.MYSQL, VisitorFeature.OutputParameterizedZeroReplaceNotUseOriginalSql));

        List<Object> params = new ArrayList<Object>();
        assertEquals("SELECT DATE_FORMAT(FROM_UNIXTIME(`time` / ?), '%Y-%m-%d %H:%i:%s')\n" +
                        "\t, `time`\n" +
                        "FROM pvtz_day\n" +
                        "ORDER BY `time` DESC"
                , ParameterizedOutputVisitorUtils.parameterize(sql, JdbcConstants.MYSQL, params, VisitorFeature.OutputParameterizedZeroReplaceNotUseOriginalSql));

        assertEquals(1, params.size());
        assertEquals("1000", JSON.toJSONString(params.get(0)));


    }

}