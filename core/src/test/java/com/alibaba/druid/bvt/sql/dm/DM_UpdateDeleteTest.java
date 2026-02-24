package com.alibaba.druid.bvt.sql.dm;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLParseAssertUtil;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLDeleteStatement;
import com.alibaba.druid.sql.ast.statement.SQLUpdateStatement;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import junit.framework.TestCase;

import java.util.List;

public class DM_UpdateDeleteTest extends TestCase {
    public void test_simple_update() throws Exception {
        String sql = "UPDATE users SET name = '王五', updated_at = SYSDATE WHERE id = 1";
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, DbType.dm);
        assertEquals(1, statementList.size());

        SQLStatement stmt = statementList.get(0);
        assertTrue(stmt instanceof SQLUpdateStatement);

        SQLUpdateStatement updateStmt = (SQLUpdateStatement) stmt;
        assertEquals("users", updateStmt.getTableName().getSimpleName());
        assertEquals(2, updateStmt.getItems().size());
        assertNotNull(updateStmt.getWhere());

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        stmt.accept(visitor);
        assertEquals(1, visitor.getTables().size());
        assertTrue(visitor.getTables().containsKey(new TableStat.Name("users")));

        SQLParseAssertUtil.assertParseSql(sql, DbType.dm);
    }

    public void test_update_with_subquery() throws Exception {
        String sql = "UPDATE users SET status = 'inactive' WHERE id IN (SELECT user_id FROM inactive_users)";
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, DbType.dm);
        assertEquals(1, statementList.size());

        SQLStatement stmt = statementList.get(0);
        assertTrue(stmt instanceof SQLUpdateStatement);

        SQLUpdateStatement updateStmt = (SQLUpdateStatement) stmt;
        assertEquals("users", updateStmt.getTableName().getSimpleName());
        assertEquals(1, updateStmt.getItems().size());
        assertNotNull(updateStmt.getWhere());

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        stmt.accept(visitor);
        assertEquals(2, visitor.getTables().size());
        assertTrue(visitor.getTables().containsKey(new TableStat.Name("users")));
        assertTrue(visitor.getTables().containsKey(new TableStat.Name("inactive_users")));

        SQLParseAssertUtil.assertParseSql(sql, DbType.dm);
    }

    public void test_simple_delete() throws Exception {
        String sql = "DELETE FROM users WHERE id = 1";
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, DbType.dm);
        assertEquals(1, statementList.size());

        SQLStatement stmt = statementList.get(0);
        assertTrue(stmt instanceof SQLDeleteStatement);

        SQLDeleteStatement deleteStmt = (SQLDeleteStatement) stmt;
        assertEquals("users", deleteStmt.getTableName().getSimpleName());
        assertNotNull(deleteStmt.getWhere());

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        stmt.accept(visitor);
        assertEquals(1, visitor.getTables().size());
        assertTrue(visitor.getTables().containsKey(new TableStat.Name("users")));
        assertEquals(1, visitor.getConditions().size());

        SQLParseAssertUtil.assertParseSql(sql, DbType.dm);
    }

    public void test_delete_with_condition() throws Exception {
        String sql = "DELETE FROM users WHERE status = 'deleted' AND updated_at < SYSDATE - 30";
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, DbType.dm);
        assertEquals(1, statementList.size());

        SQLStatement stmt = statementList.get(0);
        assertTrue(stmt instanceof SQLDeleteStatement);

        SQLDeleteStatement deleteStmt = (SQLDeleteStatement) stmt;
        assertEquals("users", deleteStmt.getTableName().getSimpleName());
        assertNotNull(deleteStmt.getWhere());

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        stmt.accept(visitor);
        assertEquals(1, visitor.getTables().size());
        assertTrue(visitor.getTables().containsKey(new TableStat.Name("users")));
        assertEquals(2, visitor.getConditions().size());

        SQLParseAssertUtil.assertParseSql(sql, DbType.dm);
    }

    public void test_delete_all() throws Exception {
        String sql = "DELETE FROM temp_table";
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, DbType.dm);
        assertEquals(1, statementList.size());

        SQLStatement stmt = statementList.get(0);
        assertTrue(stmt instanceof SQLDeleteStatement);

        SQLDeleteStatement deleteStmt = (SQLDeleteStatement) stmt;
        assertEquals("temp_table", deleteStmt.getTableName().getSimpleName());
        assertNull(deleteStmt.getWhere());

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        stmt.accept(visitor);
        assertEquals(1, visitor.getTables().size());
        assertTrue(visitor.getTables().containsKey(new TableStat.Name("temp_table")));
        assertEquals(0, visitor.getConditions().size());

        SQLParseAssertUtil.assertParseSql(sql, DbType.dm);
    }
}
