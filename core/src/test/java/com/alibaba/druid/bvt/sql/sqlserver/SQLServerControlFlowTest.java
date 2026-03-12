package com.alibaba.druid.bvt.sql.sqlserver;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLReturnStatement;
import com.alibaba.druid.sql.ast.statement.SQLWhileStatement;
import com.alibaba.druid.sql.dialect.sqlserver.ast.stmt.SQLServerThrowStatement;
import com.alibaba.druid.sql.dialect.sqlserver.ast.stmt.SQLServerTryCatchStatement;
import com.alibaba.druid.sql.dialect.sqlserver.parser.SQLServerStatementParser;
import com.alibaba.druid.sql.dialect.sqlserver.visitor.SQLServerOutputVisitor;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SQLServerControlFlowTest {
    private String output(List<SQLStatement> stmtList) {
        StringBuilder out = new StringBuilder();
        SQLServerOutputVisitor visitor = new SQLServerOutputVisitor(out);
        for (SQLStatement stmt : stmtList) {
            stmt.accept(visitor);
        }
        return out.toString();
    }

    @Test
    public void testWhileSimple() {
        String sql = "WHILE @i < 10 BEGIN SET @i = @i + 1 END";
        SQLServerStatementParser parser = new SQLServerStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        assertEquals(1, stmtList.size());
        assertTrue(stmtList.get(0) instanceof SQLWhileStatement);

        String result = output(stmtList);
        assertTrue(result.contains("WHILE"));
        assertTrue(result.contains("BEGIN"));
        assertTrue(result.contains("END"));
    }

    @Test
    public void testWhileWithBreak() {
        String sql = "WHILE @x > 0 BEGIN IF @x = 5 BREAK; SET @x = @x - 1 END";
        SQLServerStatementParser parser = new SQLServerStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        assertEquals(1, stmtList.size());
        assertTrue(stmtList.get(0) instanceof SQLWhileStatement);

        SQLWhileStatement whileStmt = (SQLWhileStatement) stmtList.get(0);
        // Should contain IF statement and SET statement
        assertEquals(2, whileStmt.getStatements().size());

        String result = output(stmtList);
        assertTrue(result.contains("BREAK"));
    }

    @Test
    public void testWhileWithBreakContinue() {
        String sql = "WHILE 1 = 1 BEGIN IF @done = 1 BREAK; CONTINUE END";
        SQLServerStatementParser parser = new SQLServerStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        assertEquals(1, stmtList.size());
        assertTrue(stmtList.get(0) instanceof SQLWhileStatement);

        String result = output(stmtList);
        assertTrue(result.contains("BREAK"));
        assertTrue(result.contains("CONTINUE"));
    }

    @Test
    public void testReturnNoValue() {
        String sql = "RETURN";
        SQLServerStatementParser parser = new SQLServerStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        assertEquals(1, stmtList.size());
        assertTrue(stmtList.get(0) instanceof SQLReturnStatement);
        assertNull(((SQLReturnStatement) stmtList.get(0)).getExpr());

        assertEquals("RETURN", output(stmtList));
    }

    @Test
    public void testReturnZero() {
        String sql = "RETURN 0";
        SQLServerStatementParser parser = new SQLServerStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        assertEquals(1, stmtList.size());
        assertTrue(stmtList.get(0) instanceof SQLReturnStatement);
        assertNotNull(((SQLReturnStatement) stmtList.get(0)).getExpr());

        assertEquals("RETURN 0", output(stmtList));
    }

    @Test
    public void testReturnVariable() {
        String sql = "RETURN @result";
        SQLServerStatementParser parser = new SQLServerStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        assertEquals(1, stmtList.size());
        assertTrue(stmtList.get(0) instanceof SQLReturnStatement);

        assertEquals("RETURN @result", output(stmtList));
    }

    @Test
    public void testThrowRethrow() {
        String sql = "BEGIN TRY SELECT 1; END TRY BEGIN CATCH THROW; END CATCH";
        SQLServerStatementParser parser = new SQLServerStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        assertEquals(1, stmtList.size());
        assertTrue(stmtList.get(0) instanceof SQLServerTryCatchStatement);

        SQLServerTryCatchStatement tryCatch = (SQLServerTryCatchStatement) stmtList.get(0);
        assertEquals(1, tryCatch.getCatchStatements().size());
        assertTrue(tryCatch.getCatchStatements().get(0) instanceof SQLServerThrowStatement);

        SQLServerThrowStatement throwStmt = (SQLServerThrowStatement) tryCatch.getCatchStatements().get(0);
        assertNull(throwStmt.getErrorNumber());

        String result = output(stmtList);
        assertTrue(result.contains("THROW"));
    }

    @Test
    public void testThrowWithArgs() {
        String sql = "THROW 50000, 'Error message', 1";
        SQLServerStatementParser parser = new SQLServerStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        assertEquals(1, stmtList.size());
        assertTrue(stmtList.get(0) instanceof SQLServerThrowStatement);

        SQLServerThrowStatement throwStmt = (SQLServerThrowStatement) stmtList.get(0);
        assertNotNull(throwStmt.getErrorNumber());
        assertNotNull(throwStmt.getMessage());
        assertNotNull(throwStmt.getState());

        String result = output(stmtList);
        assertEquals("THROW 50000, 'Error message', 1", result);
    }
}
