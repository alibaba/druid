package com.alibaba.druid.bvt.sql.mysql.select;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.TDDLHint;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.parser.SQLParserFeature;
import com.alibaba.druid.util.JdbcConstants;

import java.util.List;

public class MySqlSelectTest_170_tddl_hint extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "/*+TDDL({'type':'direct','dbid':'xxx_group'})*/select * from real_table_0;";

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL, SQLParserFeature.TDDLHint);
        SQLSelectStatement stmt = (SQLSelectStatement)statementList.get(0);

        assertEquals(1, statementList.size());

        assertEquals("/*+TDDL({'type':'direct','dbid':'xxx_group'})*/\n" +
                "SELECT *\n" +
                "FROM real_table_0;", stmt.toString());

        TDDLHint hint = (TDDLHint) stmt.getHeadHintsDirect().get(0);
        assertSame(TDDLHint.Type.JSON, hint.getType());
        assertEquals("{'type':'direct','dbid':'xxx_group'}", hint.getJson());
    }

    public void test_1() throws Exception {
        String sql = "/*TDDL:DEFER*/select * from real_table_0;";

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL, SQLParserFeature.TDDLHint);
        SQLSelectStatement stmt = (SQLSelectStatement)statementList.get(0);

        assertEquals(1, statementList.size());

        assertEquals("/*TDDL:DEFER*/\n" +
                "SELECT *\n" +
                "FROM real_table_0;", stmt.toString());

        TDDLHint hint = (TDDLHint) stmt.getHeadHintsDirect().get(0);
        assertSame(TDDLHint.Type.Function, hint.getType());
        assertEquals("DEFER", hint.getFunctions().get(0).getName());
    }

    public void test_2() throws Exception {
        String sql = "/*TDDL:UNDO_LOG_LIMIT=2000*/select * from real_table_0;";

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL, SQLParserFeature.TDDLHint);
        SQLSelectStatement stmt = (SQLSelectStatement)statementList.get(0);

        assertEquals(1, statementList.size());

        assertEquals("/*TDDL:UNDO_LOG_LIMIT=2000*/\n" +
                "SELECT *\n" +
                "FROM real_table_0;", stmt.toString());

        TDDLHint hint = (TDDLHint) stmt.getHeadHintsDirect().get(0);
        assertSame(TDDLHint.Type.Function, hint.getType());
        assertEquals("UNDO_LOG_LIMIT", hint.getFunctions().get(0).getName());
    }
}