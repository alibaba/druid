package com.alibaba.druid.bvt.sql.mysql.param;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlExportParameterVisitor;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.visitor.ExportParameterVisitor;
import com.alibaba.druid.sql.visitor.ParameterizedOutputVisitorUtils;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

import java.util.List;

/**
 * Created by wenshao on 16/8/23.
 */
public class MySqlParameterizedOutputVisitorTest_10 extends TestCase {
    public void test_for_parameterize() throws Exception {
         /*String instance = "100.81.152.9"+"_"+3314;
        int urlNum = Math.abs(instance.hashCode()) % 2;
        System.out.println(urlNum);*/
      /* String formattedSql = SQLUtils.format("select * from ? where id = ?", JdbcConstants.MYSQL,
                Arrays.<Object> asList("abc,a"));
        System.out.println(formattedSql);*/

        final String dbType = JdbcConstants.MYSQL;

        String sql = "SELECT `SURVEY_ANSWER`.`TIME_UPDATED`, `SURVEY_ANSWER`.`ANSWER_VALUE` FROM `S_ANSWER_P0115` `SURVEY_ANSWER` WHERE `SURVEY_ANSWER`.`SURVEY_ID` = 11 AND `SURVEY_ANSWER`.`QUESTION_CODE` = 'qq' ORDER BY `SURVEY_ANSWER`.`TIME_UPDATED` DESC LIMIT 1, 2";
        String psql = ParameterizedOutputVisitorUtils.parameterize(sql, dbType);
       /* Assert.assertEquals("SELECT *\n" +
                "FROM t\n" +
                "LIMIT ?, ?", psql);*/

        System.out.println(psql);

        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, dbType);
        List<SQLStatement> stmtList = parser.parseStatementList();

        StringBuilder out = new StringBuilder();
        ExportParameterVisitor visitor = new MySqlExportParameterVisitor(out);
        for (SQLStatement stmt : stmtList) {
            stmt.accept(visitor);
        }

        System.out.println(visitor.getParameters());

        stmtList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);

        visitor = new MySqlExportParameterVisitor();
        for (SQLStatement stmt : stmtList) {
            stmt.accept(visitor);
        }
        System.out.println(visitor.getParameters());
      /*  Assert.assertEquals(2, visitor.getArguments().size());
        Assert.assertEquals(3, visitor.getArguments().get(0));
        Assert.assertEquals(4, visitor.getArguments().get(1));*/
    }
}
