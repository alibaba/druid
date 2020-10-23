package com.alibaba.druid.bvt.sql.mysql.param;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.parser.SQLParserFeature;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;
import com.alibaba.druid.support.calcite.CalciteMySqlNodeVisitor;
import com.alibaba.druid.support.calcite.TDDLSqlSelect;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlNodeList;
import org.junit.Assert;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wenshao on 16/8/23.
 */
public class MySqlParameterizedOutputVisitorTest_47_hints extends TestCase {
    public void test_for_parameterize() throws Exception {
        final DbType dbType = JdbcConstants.MYSQL;
        String sql = "/* ///d45b1886/ *//*+TDDL({'extra':{'SOCKET_TIMEOUT':'0'}})*/ DELETE FROM page_scores WHERE createdat <= '2017-06-16 00:39:09.1';";

        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, dbType);
        List<SQLStatement> stmtList = parser.parseStatementList();
        SQLStatement stmt = stmtList.get(0);

        StringBuilder out = new StringBuilder();
        SQLASTOutputVisitor visitor = SQLUtils.createOutputVisitor(out, JdbcConstants.MYSQL);
        List<Object> parameters = new ArrayList<Object>();
        visitor.setParameterized(true);
        visitor.setParameterizedMergeInList(true);
        visitor.setParameters(parameters);
        /*visitor.setPrettyFormat(false);*/
        stmt.accept(visitor);
       /* JSONArray array = new JSONArray();
        for(String table : visitor.getTables()){
            array.add(table.replaceAll("`",""));
        }*/

        String psql = out.toString();

        System.out.println(psql);


        assertEquals("/*+TDDL({'extra':{'SOCKET_TIMEOUT':'0'}})*/\n" +
                "DELETE FROM page_scores\n" +
                "WHERE createdat <= ?;", psql);
    }

    public void test_headHint_2() throws Exception {
        String sql = "/*TDDL : construct()*/ select /*TDDL : add_ms(sort=t.pk, asc=true) add_agg(agg=\"SUM\", group=\"pk\", column=\"c\") add_pj(c=\"c\") add_ts(c, true) */ pk, count(*) c from tb1 where id in(1,2,3,4) group by pk order by pk";

        MySqlStatementParser parser = new MySqlStatementParser(sql, SQLParserFeature.TDDLHint);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement statemen = statementList.get(0);

        CalciteMySqlNodeVisitor visitor = new CalciteMySqlNodeVisitor();
        statemen.accept(visitor);

        SqlNode sqlNode = visitor.getSqlNode();

        System.out.println(sqlNode);

        SqlNodeList hints = ((TDDLSqlSelect) sqlNode).getHints();
        Assert.assertEquals(hints.size(), 1);
        Assert.assertEquals(((SqlNodeList)hints.get(0)).size(), 4);

        SqlNodeList headHints = ((TDDLSqlSelect) sqlNode).getHeadHints();
        Assert.assertEquals(headHints.size(), 1);
        Assert.assertEquals(((SqlNodeList)headHints.get(0)).size(), 1);
    }

    public void test_headHint_3() throws Exception {
        String sql = "/*TDDL: node(0)*/ show datasources";

        MySqlStatementParser parser = new MySqlStatementParser(sql, SQLParserFeature.TDDLHint);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement statement = statementList.get(0);

        System.out.println(statement);

        Assert.assertEquals(statement.getHeadHintsDirect().size(), 1);
    }
}
