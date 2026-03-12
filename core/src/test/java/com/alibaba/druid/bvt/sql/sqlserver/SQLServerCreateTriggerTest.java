package com.alibaba.druid.bvt.sql.sqlserver;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLCreateTriggerStatement;
import com.alibaba.druid.sql.dialect.sqlserver.parser.SQLServerStatementParser;
import com.alibaba.druid.sql.dialect.sqlserver.visitor.SQLServerOutputVisitor;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SQLServerCreateTriggerTest {
    @Test
    public void testCreateTriggerAfterInsert() {
        String sql = "CREATE TRIGGER trg_audit ON dbo.orders AFTER INSERT AS BEGIN INSERT INTO audit_log SELECT * FROM inserted; END";
        SQLServerStatementParser parser = new SQLServerStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        assertEquals(1, stmtList.size());
        SQLCreateTriggerStatement stmt = (SQLCreateTriggerStatement) stmtList.get(0);
        assertEquals("trg_audit", stmt.getName().getSimpleName());
        assertTrue(stmt.isInsert());
        assertFalse(stmt.isUpdate());
        assertFalse(stmt.isDelete());
        assertEquals(SQLCreateTriggerStatement.TriggerType.AFTER, stmt.getTriggerType());

        StringBuilder out = new StringBuilder();
        SQLServerOutputVisitor visitor = new SQLServerOutputVisitor(out);
        stmt.accept(visitor);
        String result = out.toString();
        assertTrue(result.contains("CREATE TRIGGER"));
        assertTrue(result.contains("ON dbo.orders"));
        assertTrue(result.contains("AFTER INSERT"));
    }

    @Test
    public void testCreateOrAlterTriggerInsteadOfDelete() {
        String sql = "CREATE OR ALTER TRIGGER trg_check ON products INSTEAD OF DELETE AS BEGIN SELECT 'Cannot delete'; END";
        SQLServerStatementParser parser = new SQLServerStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        assertEquals(1, stmtList.size());
        SQLCreateTriggerStatement stmt = (SQLCreateTriggerStatement) stmtList.get(0);
        assertEquals("trg_check", stmt.getName().getSimpleName());
        assertTrue(stmt.isOrReplace());
        assertTrue(stmt.isDelete());
        assertEquals(SQLCreateTriggerStatement.TriggerType.INSTEAD_OF, stmt.getTriggerType());

        StringBuilder out = new StringBuilder();
        SQLServerOutputVisitor visitor = new SQLServerOutputVisitor(out);
        stmt.accept(visitor);
        String result = out.toString();
        assertTrue(result.contains("CREATE OR ALTER TRIGGER"));
        assertTrue(result.contains("INSTEAD OF DELETE"));
    }

    @Test
    public void testCreateTriggerMultipleEvents() {
        String sql = "CREATE TRIGGER trg_update ON employees AFTER UPDATE, INSERT AS BEGIN SELECT 1; END";
        SQLServerStatementParser parser = new SQLServerStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        assertEquals(1, stmtList.size());
        SQLCreateTriggerStatement stmt = (SQLCreateTriggerStatement) stmtList.get(0);
        assertEquals("trg_update", stmt.getName().getSimpleName());
        assertTrue(stmt.isInsert());
        assertTrue(stmt.isUpdate());
        assertFalse(stmt.isDelete());
        assertEquals(SQLCreateTriggerStatement.TriggerType.AFTER, stmt.getTriggerType());

        StringBuilder out = new StringBuilder();
        SQLServerOutputVisitor visitor = new SQLServerOutputVisitor(out);
        stmt.accept(visitor);
        String result = out.toString();
        assertTrue(result.contains("INSERT"));
        assertTrue(result.contains("UPDATE"));
    }

    @Test
    public void testCreateTriggerForInsert() {
        String sql = "CREATE TRIGGER trg_for ON dbo.orders FOR INSERT AS BEGIN SELECT 1; END";
        SQLServerStatementParser parser = new SQLServerStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        assertEquals(1, stmtList.size());
        SQLCreateTriggerStatement stmt = (SQLCreateTriggerStatement) stmtList.get(0);
        assertTrue(stmt.isInsert());
        assertEquals(SQLCreateTriggerStatement.TriggerType.AFTER, stmt.getTriggerType());
    }

    @Test
    public void testCreateTriggerAllEvents() {
        String sql = "CREATE TRIGGER trg_all ON dbo.orders AFTER INSERT, UPDATE, DELETE AS BEGIN SELECT 1; END";
        SQLServerStatementParser parser = new SQLServerStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        assertEquals(1, stmtList.size());
        SQLCreateTriggerStatement stmt = (SQLCreateTriggerStatement) stmtList.get(0);
        assertTrue(stmt.isInsert());
        assertTrue(stmt.isUpdate());
        assertTrue(stmt.isDelete());
    }

    @Test
    public void testCreateTriggerRoundTrip() {
        String sql = "CREATE TRIGGER trg_audit ON dbo.orders AFTER INSERT AS BEGIN INSERT INTO audit_log SELECT * FROM inserted; END";
        SQLServerStatementParser parser = new SQLServerStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        StringBuilder out = new StringBuilder();
        SQLServerOutputVisitor visitor = new SQLServerOutputVisitor(out);
        stmtList.get(0).accept(visitor);
        String output = out.toString();

        // Parse the output again to verify round-trip
        SQLServerStatementParser parser2 = new SQLServerStatementParser(output);
        List<SQLStatement> stmtList2 = parser2.parseStatementList();
        assertEquals(1, stmtList2.size());
        assertTrue(stmtList2.get(0) instanceof SQLCreateTriggerStatement);
    }
}
