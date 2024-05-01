package com.alibaba.druid.bvt.sql.mysql.param;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.parser.SQLParserFeature;
import com.alibaba.fastjson2.JSON;
import com.alibaba.druid.sql.visitor.ParameterizedOutputVisitorUtils;
import com.alibaba.druid.sql.visitor.VisitorFeature;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

public class MySqlParameterizedOutputVisitorTest_74 extends TestCase {
    public void test_in() throws Exception {
        String sql = "select 0 from corona_select_multi_db_one_tb "
            + "where( 9 =( (3,4) not in ((1,2 ),( 3,5)) ) ) =bigint_test";

        List<SQLStatement> stmtList111 = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
        SQLStatement stmt111 = stmtList111.get(0);
        System.out.println(stmt111.toString());
        List<Object> outParameters = new ArrayList<Object>(0);

        String psql = ParameterizedOutputVisitorUtils.parameterize(sql, JdbcConstants.MYSQL, outParameters, VisitorFeature.OutputParameterizedQuesUnMergeInList,
                VisitorFeature.OutputParameterizedUnMergeShardingTable);
        assertEquals("SELECT ?\n" +
                "FROM corona_select_multi_db_one_tb\n" +
                "WHERE ? = ((?, ?) NOT IN ((?, ?), (?, ?))) = bigint_test", psql);

        assertEquals("[0,9,3,4,1,2,3,5]", JSON.toJSONString(outParameters));
    }

    public void test_between() throws Exception {
        String sql = "select 0 from corona_select_multi_db_one_tb where( 9 =( 3 not between 1 and 5 ) ) =bigint_test";

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL, SQLParserFeature.TDDLHint);
        SQLSelectStatement stmt = (SQLSelectStatement) statementList.get(0);

        List<Object> outParameters = new ArrayList<Object>(0);

        String psql = ParameterizedOutputVisitorUtils.parameterize(sql, JdbcConstants.MYSQL, outParameters, VisitorFeature.OutputParameterizedQuesUnMergeInList,
                VisitorFeature.OutputParameterizedUnMergeShardingTable);
        assertEquals("SELECT ?\n" +
                "FROM corona_select_multi_db_one_tb\n" +
                "WHERE ? = (? NOT BETWEEN ? AND ?) = bigint_test", psql);

        assertEquals("[0,9,3,1,5]", JSON.toJSONString(outParameters));
    }
}
