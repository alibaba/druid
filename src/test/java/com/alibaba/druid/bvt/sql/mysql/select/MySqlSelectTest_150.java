package com.alibaba.druid.bvt.sql.mysql.select;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.visitor.ParameterizedOutputVisitorUtils;
import com.alibaba.druid.sql.visitor.VisitorFeature;
import com.alibaba.druid.util.JdbcConstants;

import java.util.List;

public class MySqlSelectTest_150 extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "(select __aid\n" +
                "  from unidesk_ads.dmj_ex_1_unidesk_tag_all\n" +
                " where unidesk_ads.dmj_ex_1_unidesk_tag_all.pred_career_type in ('test1'))\n" +
                " \n" +
                " union\n" +
                "\n" +
                "(select __aid\n" +
                "  from unidesk_ads.dmj_ex_1_unidesk_tag_all\n" +
                " where unidesk_ads.dmj_ex_1_unidesk_tag_all.pred_career_type in ('test'))\n" +
                "\n" +
                "MINUS\n" +
                "(\n" +
                "select __aid\n" +
                "  from unidesk_ads.dmj_ex_1_unidesk_tag_all\n" +
                " where unidesk_ads.dmj_ex_1_unidesk_tag_all.pred_career_type in ('8', '1')\n" +
                "    )";

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
        SQLSelectStatement stmt = (SQLSelectStatement)statementList.get(0);

        assertEquals(1, statementList.size());

        assertEquals("(SELECT __aid\n" +
                "FROM unidesk_ads.dmj_ex_1_unidesk_tag_all\n" +
                "WHERE unidesk_ads.dmj_ex_1_unidesk_tag_all.pred_career_type IN ('test1'))\n" +
                "UNION\n" +
                "(SELECT __aid\n" +
                "FROM unidesk_ads.dmj_ex_1_unidesk_tag_all\n" +
                "WHERE unidesk_ads.dmj_ex_1_unidesk_tag_all.pred_career_type IN ('test'))\n" +
                "MINUS\n" +
                "(SELECT __aid\n" +
                "FROM unidesk_ads.dmj_ex_1_unidesk_tag_all\n" +
                "WHERE unidesk_ads.dmj_ex_1_unidesk_tag_all.pred_career_type IN ('8', '1'))", stmt.toString());

        assertEquals("(SELECT __aid\n" +
                        "FROM unidesk_ads.dmj_ex_1_unidesk_tag_all\n" +
                        "WHERE unidesk_ads.dmj_ex_1_unidesk_tag_all.pred_career_type IN (?))\n" +
                        "UNION\n" +
                        "(SELECT __aid\n" +
                        "FROM unidesk_ads.dmj_ex_1_unidesk_tag_all\n" +
                        "WHERE unidesk_ads.dmj_ex_1_unidesk_tag_all.pred_career_type IN (?))\n" +
                        "MINUS\n" +
                        "(SELECT __aid\n" +
                        "FROM unidesk_ads.dmj_ex_1_unidesk_tag_all\n" +
                        "WHERE unidesk_ads.dmj_ex_1_unidesk_tag_all.pred_career_type IN (?))"
                , ParameterizedOutputVisitorUtils.parameterize(sql, JdbcConstants.MYSQL, VisitorFeature.OutputParameterizedZeroReplaceNotUseOriginalSql));


    }

}