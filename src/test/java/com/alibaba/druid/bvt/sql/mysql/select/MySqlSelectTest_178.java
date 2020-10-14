package com.alibaba.druid.bvt.sql.mysql.select;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLJoinTableSource;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.parser.SQLParserFeature;
import com.alibaba.druid.util.JdbcConstants;

import java.util.List;

public class MySqlSelectTest_178 extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "select rt_dws_csn_sta_lgt_ord_ri.metrics_id as yujiu from rt_dws_csn_sta_lgt_ord_ri CROSS JOIN rt_dws_csn_sta_lgt_ord_mi ;";
//
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL, SQLParserFeature.EnableSQLBinaryOpExprGroup,
                SQLParserFeature.OptimizedForParameterized);
        SQLSelectStatement stmt = (SQLSelectStatement)statementList.get(0);

        assertEquals(1, statementList.size());

        assertEquals("SELECT rt_dws_csn_sta_lgt_ord_ri.metrics_id AS yujiu\n" +
                "FROM rt_dws_csn_sta_lgt_ord_ri\n" +
                "\tCROSS JOIN rt_dws_csn_sta_lgt_ord_mi;", stmt.toString());

        assertEquals("select rt_dws_csn_sta_lgt_ord_ri.metrics_id as yujiu\n" +
                "from rt_dws_csn_sta_lgt_ord_ri\n" +
                "\tcross join rt_dws_csn_sta_lgt_ord_mi;", stmt.toLowerCaseString());


        assertEquals("SELECT rt_dws_csn_sta_lgt_ord_ri.metrics_id AS yujiu\n" +
                "FROM rt_dws_csn_sta_lgt_ord_ri\n" +
                "\tCROSS JOIN rt_dws_csn_sta_lgt_ord_mi;", stmt.toParameterizedString());

        SQLJoinTableSource join = (SQLJoinTableSource) stmt.getSelect().getQueryBlock().getFrom();
        assertEquals(SQLJoinTableSource.JoinType.CROSS_JOIN, join.getJoinType());
        assertNull(join.getLeft().getAlias());
    }


}