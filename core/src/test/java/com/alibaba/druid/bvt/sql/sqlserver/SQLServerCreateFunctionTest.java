package com.alibaba.druid.bvt.sql.sqlserver;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLCreateFunctionStatement;
import com.alibaba.druid.sql.dialect.sqlserver.parser.SQLServerStatementParser;
import com.alibaba.druid.sql.dialect.sqlserver.visitor.SQLServerOutputVisitor;
import junit.framework.TestCase;

import java.util.List;

public class SQLServerCreateFunctionTest extends TestCase {
    public void testCreateScalarFunction() {
        String sql = "CREATE FUNCTION dbo.fn_add (@a INT, @b INT) RETURNS INT AS BEGIN RETURN @a + @b; END";
        SQLServerStatementParser parser = new SQLServerStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        assertEquals(1, stmtList.size());
        SQLCreateFunctionStatement stmt = (SQLCreateFunctionStatement) stmtList.get(0);
        assertEquals("dbo.fn_add", stmt.getName().toString());
        assertEquals(2, stmt.getParameters().size());
        assertEquals("@a", stmt.getParameters().get(0).getName().getSimpleName());
        assertEquals("@b", stmt.getParameters().get(1).getName().getSimpleName());
        assertNotNull(stmt.getReturnDataType());
        assertEquals("INT", stmt.getReturnDataType().getName().toUpperCase());

        StringBuilder out = new StringBuilder();
        SQLServerOutputVisitor visitor = new SQLServerOutputVisitor(out);
        stmt.accept(visitor);
        String result = out.toString();
        assertTrue(result.contains("CREATE FUNCTION"));
        assertTrue(result.contains("RETURNS"));
    }

    public void testCreateInlineTableFunction() {
        String sql = "CREATE FUNCTION dbo.fn_orders (@customer_id INT) RETURNS TABLE AS RETURN (SELECT * FROM orders WHERE customer_id = @customer_id)";
        SQLServerStatementParser parser = new SQLServerStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        assertEquals(1, stmtList.size());
        SQLCreateFunctionStatement stmt = (SQLCreateFunctionStatement) stmtList.get(0);
        assertEquals("dbo.fn_orders", stmt.getName().toString());
        assertEquals(1, stmt.getParameters().size());
        assertNotNull(stmt.getReturnDataType());
        assertEquals("TABLE", stmt.getReturnDataType().getName().toUpperCase());

        StringBuilder out = new StringBuilder();
        SQLServerOutputVisitor visitor = new SQLServerOutputVisitor(out);
        stmt.accept(visitor);
        String result = out.toString();
        assertTrue(result.contains("RETURNS TABLE"));
        assertTrue(result.contains("RETURN"));
    }

    public void testCreateOrAlterFunction() {
        String sql = "CREATE OR ALTER FUNCTION dbo.fn_format (@val VARCHAR(100)) RETURNS VARCHAR(200) AS BEGIN RETURN UPPER(@val); END";
        SQLServerStatementParser parser = new SQLServerStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        assertEquals(1, stmtList.size());
        SQLCreateFunctionStatement stmt = (SQLCreateFunctionStatement) stmtList.get(0);
        assertTrue(stmt.isOrReplace());
        assertEquals("dbo.fn_format", stmt.getName().toString());
        assertEquals(1, stmt.getParameters().size());

        StringBuilder out = new StringBuilder();
        SQLServerOutputVisitor visitor = new SQLServerOutputVisitor(out);
        stmt.accept(visitor);
        String result = out.toString();
        assertTrue(result.contains("CREATE OR ALTER FUNCTION"));
    }

    public void testCreateFunctionNoParams() {
        String sql = "CREATE FUNCTION dbo.fn_getdate () RETURNS DATETIME AS BEGIN RETURN GETDATE(); END";
        SQLServerStatementParser parser = new SQLServerStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        assertEquals(1, stmtList.size());
        SQLCreateFunctionStatement stmt = (SQLCreateFunctionStatement) stmtList.get(0);
        assertEquals(0, stmt.getParameters().size());
    }

    public void testCreateFunctionWithDefaultParam() {
        String sql = "CREATE FUNCTION dbo.fn_test (@x INT = 0) RETURNS INT AS BEGIN RETURN @x + 1; END";
        SQLServerStatementParser parser = new SQLServerStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        assertEquals(1, stmtList.size());
        SQLCreateFunctionStatement stmt = (SQLCreateFunctionStatement) stmtList.get(0);
        assertEquals(1, stmt.getParameters().size());
        assertNotNull(stmt.getParameters().get(0).getDefaultValue());
    }

    public void testCreateFunctionRoundTrip() {
        String sql = "CREATE FUNCTION dbo.fn_add (@a INT, @b INT) RETURNS INT AS BEGIN RETURN @a + @b; END";
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
        assertTrue(stmtList2.get(0) instanceof SQLCreateFunctionStatement);
    }
}
