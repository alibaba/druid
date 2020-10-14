package com.alibaba.druid.bvt.sql.mysql.select;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.visitor.ParameterizedOutputVisitorUtils;
import com.alibaba.druid.sql.visitor.VisitorFeature;
import com.alibaba.druid.util.JdbcConstants;

import java.util.List;

public class MySqlSelectTest_148_extract extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "SELECT EXTRACT(YEAR FROM '2008-01-02') >> 3 FROM corona_select_one_db_one_tb";

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
        SQLSelectStatement stmt = (SQLSelectStatement)statementList.get(0);

        assertEquals(1, statementList.size());

        assertEquals("SELECT EXTRACT(YEAR FROM '2008-01-02') >> 3\n" +
                "FROM corona_select_one_db_one_tb", stmt.toString());

        assertEquals("SELECT EXTRACT(YEAR FROM ?) >> ?\n" +
                        "FROM corona_select_one_db_one_tb"
                , ParameterizedOutputVisitorUtils.parameterize(sql, JdbcConstants.MYSQL, VisitorFeature.OutputParameterizedZeroReplaceNotUseOriginalSql));

        SQLSelectQueryBlock queryBlock = stmt.getSelect().getQueryBlock();
        assertEquals(1, queryBlock.getSelectList().size());
        assertEquals("EXTRACT(YEAR FROM '2008-01-02') >> 3"
                , queryBlock.getSelectList().get(0).getExpr().toString()
        );
    }

}