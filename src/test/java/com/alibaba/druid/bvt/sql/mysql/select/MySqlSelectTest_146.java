package com.alibaba.druid.bvt.sql.mysql.select;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.parser.SQLParserFeature;
import com.alibaba.druid.sql.visitor.ParameterizedOutputVisitorUtils;
import com.alibaba.druid.sql.visitor.VisitorFeature;
import com.alibaba.druid.util.JdbcConstants;

import java.util.List;

public class MySqlSelectTest_146 extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "select '18015376320243458'=18015376320243459 is  not  NULL from select_base_two_multi_db_multi_tb limit 1;";

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
        SQLSelectStatement stmt = (SQLSelectStatement)statementList.get(0);

        assertEquals(1, statementList.size());

        assertEquals("SELECT '18015376320243458' = 18015376320243459 IS NOT NULL\n" +
                "FROM select_base_two_multi_db_multi_tb\n" +
                "LIMIT 1;", stmt.toString());

        assertEquals("SELECT ? = ? IS NOT NULL\n" +
                        "FROM select_base_two_multi_db_multi_tb\n" +
                        "LIMIT ?;"
                , ParameterizedOutputVisitorUtils.parameterize(sql, JdbcConstants.MYSQL, VisitorFeature.OutputParameterizedZeroReplaceNotUseOriginalSql));
    }

}