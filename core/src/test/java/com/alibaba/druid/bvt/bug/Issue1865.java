package com.alibaba.druid.bvt.bug;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLDeleteStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.ast.statement.SQLUpdateStatement;
import com.alibaba.druid.sql.parser.SQLParserFeature;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

import java.util.List;

public class Issue1865 extends TestCase {
    public void test_for_select() throws Exception {
        final DbType dbType = JdbcConstants.MYSQL;

        String sql = "select * from t where id = 2 and name = 'wenshao'";
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, dbType);
        SQLSelectStatement stmt = (SQLSelectStatement) parser.parseStatement();
        SQLSelectQueryBlock queryBlock = stmt.getSelect().getQueryBlock();

        assertTrue(queryBlock.removeCondition("name = 'wenshao'"));

        assertEquals("SELECT *\n" +
                "FROM t\n" +
                "WHERE id = 2", stmt.toString());

        assertTrue(
                queryBlock.removeCondition("id = 2"));

        assertEquals("SELECT *\n" +
                "FROM t", stmt.toString());
        queryBlock.addCondition("id = 3");
        assertEquals("SELECT *\n" +
                "FROM t\n" +
                "WHERE id = 3", stmt.toString());
    }

    public void test_for_select_group() throws Exception {
        final DbType dbType = JdbcConstants.MYSQL;

        String sql = "select * from t where id = 2 and name = 'wenshao'";
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, dbType, SQLParserFeature.EnableSQLBinaryOpExprGroup);
        SQLSelectStatement stmt = (SQLSelectStatement) parser.parseStatement();
        SQLSelectQueryBlock queryBlock = stmt.getSelect().getQueryBlock();

        assertTrue(queryBlock.removeCondition("name = 'wenshao'"));

        assertEquals("SELECT *\n" +
                "FROM t\n" +
                "WHERE id = 2", stmt.toString());

        assertTrue(
                queryBlock.removeCondition("id = 2"));

        assertEquals("SELECT *\n" +
                "FROM t", stmt.toString());
    }

    public void test_for_delete() throws Exception {
        final DbType dbType = JdbcConstants.MYSQL;

        String sql = "delete from t where id = 2 and name = 'wenshao'";
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, dbType);
        SQLDeleteStatement stmt = (SQLDeleteStatement) parser.parseStatement();

        assertTrue(stmt.removeCondition("name = 'wenshao'"));

        assertEquals("DELETE FROM t\n" +
                "WHERE id = 2", stmt.toString());

        assertTrue(
                stmt.removeCondition("id = 2"));

        assertEquals("DELETE FROM t", stmt.toString());

        stmt.addCondition("id = 3");
        assertEquals("DELETE FROM t\n" +
                "WHERE id = 3", stmt.toString());
    }

    public void test_for_delete_group() throws Exception {
        final DbType dbType = JdbcConstants.MYSQL;

        String sql = "delete from t where id = 2 and name = 'wenshao'";
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, dbType, SQLParserFeature.EnableSQLBinaryOpExprGroup);
        SQLDeleteStatement stmt = (SQLDeleteStatement) parser.parseStatement();

        assertTrue(stmt.removeCondition("name = 'wenshao'"));

        assertEquals("DELETE FROM t\n" +
                "WHERE id = 2", stmt.toString());

        assertTrue(
                stmt.removeCondition("id = 2"));

        assertEquals("DELETE FROM t", stmt.toString());

        stmt.addCondition("id = 3");
        assertEquals("DELETE FROM t\n" +
                "WHERE id = 3", stmt.toString());
    }

    public void test_for_update() throws Exception {
        final DbType dbType = JdbcConstants.MYSQL;

        String sql = "update t set val = ? where id = 2 and name = 'wenshao'";
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, dbType);
        SQLUpdateStatement stmt = (SQLUpdateStatement) parser.parseStatement();

        assertTrue(stmt.removeCondition("name = 'wenshao'"));

        assertEquals("UPDATE t\n" +
                "SET val = ?\n" +
                "WHERE id = 2", stmt.toString());

        assertTrue(
                stmt.removeCondition("id = 2"));

        assertEquals("UPDATE t\n" +
                "SET val = ?", stmt.toString());

        stmt.addCondition("id = 3");
        assertEquals("UPDATE t\n" +
                "SET val = ?\n" +
                "WHERE id = 3", stmt.toString());
    }

    public void test_for_update_group() throws Exception {
        final DbType dbType = JdbcConstants.MYSQL;

        String sql = "update t set val = ? where id = 2 and name = 'wenshao'";
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, dbType, SQLParserFeature.EnableSQLBinaryOpExprGroup);
        SQLUpdateStatement stmt = (SQLUpdateStatement) parser.parseStatement();

        assertTrue(stmt.removeCondition("name = 'wenshao'"));

        assertEquals("UPDATE t\n" +
                "SET val = ?\n" +
                "WHERE id = 2", stmt.toString());

        assertTrue(
                stmt.removeCondition("id = 2"));

        assertEquals("UPDATE t\n" +
                "SET val = ?", stmt.toString());

        stmt.addCondition("id = 3");
        assertEquals("UPDATE t\n" +
                "SET val = ?\n" +
                "WHERE id = 3", stmt.toString());
    }
}
