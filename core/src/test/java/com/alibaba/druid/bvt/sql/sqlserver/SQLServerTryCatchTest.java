package com.alibaba.druid.bvt.sql.sqlserver;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import junit.framework.TestCase;

import java.util.List;

public class SQLServerTryCatchTest extends TestCase {
    private final DbType dbType = DbType.sqlserver;

    // Official doc example A: basic divide-by-zero
    public void test_basic_divide_by_zero() throws Exception {
        String sql = "BEGIN TRY\n" +
                "    SELECT 1 / 0;\n" +
                "END TRY\n" +
                "BEGIN CATCH\n" +
                "    SELECT ERROR_NUMBER() AS ErrorNumber, ERROR_MESSAGE() AS ErrorMessage;\n" +
                "END CATCH";

        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        assertEquals(1, stmtList.size());

        String result = SQLUtils.toSQLString(stmtList, dbType);
        assertTrue(result.contains("BEGIN TRY"));
        assertTrue(result.contains("END TRY"));
        assertTrue(result.contains("BEGIN CATCH"));
        assertTrue(result.contains("END CATCH"));
        assertTrue(result.contains("ERROR_NUMBER"));
        assertTrue(result.contains("ERROR_MESSAGE"));
    }

    // Multiple statements in TRY and CATCH blocks
    public void test_multiple_statements() throws Exception {
        String sql = "BEGIN TRY\n" +
                "    INSERT INTO t1 VALUES (1);\n" +
                "    INSERT INTO t1 VALUES (2);\n" +
                "    INSERT INTO t1 VALUES (3);\n" +
                "END TRY\n" +
                "BEGIN CATCH\n" +
                "    DECLARE @msg NVARCHAR(4000);\n" +
                "    SELECT @msg = ERROR_MESSAGE();\n" +
                "END CATCH";

        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        assertEquals(1, stmtList.size());
    }

    // Official doc example B: TRY...CATCH in a transaction
    public void test_with_transaction() throws Exception {
        String sql = "BEGIN TRANSACTION;\n" +
                "BEGIN TRY\n" +
                "    DELETE FROM Production WHERE ProductID = 980;\n" +
                "END TRY\n" +
                "BEGIN CATCH\n" +
                "    SELECT ERROR_NUMBER() AS ErrorNumber, ERROR_SEVERITY() AS ErrorSeverity, ERROR_STATE() AS ErrorState;\n" +
                "END CATCH";

        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        assertEquals(2, stmtList.size()); // BEGIN TRANSACTION + BEGIN TRY...CATCH
    }

    // All ERROR_* functions
    public void test_all_error_functions() throws Exception {
        String sql = "BEGIN TRY\n" +
                "    SELECT 1 / 0;\n" +
                "END TRY\n" +
                "BEGIN CATCH\n" +
                "    SELECT ERROR_NUMBER() AS ErrorNumber,\n" +
                "        ERROR_SEVERITY() AS ErrorSeverity,\n" +
                "        ERROR_STATE() AS ErrorState,\n" +
                "        ERROR_PROCEDURE() AS ErrorProcedure,\n" +
                "        ERROR_LINE() AS ErrorLine,\n" +
                "        ERROR_MESSAGE() AS ErrorMessage;\n" +
                "END CATCH";

        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        assertEquals(1, stmtList.size());

        String result = SQLUtils.toSQLString(stmtList, dbType);
        assertTrue(result.contains("ERROR_NUMBER"));
        assertTrue(result.contains("ERROR_SEVERITY"));
        assertTrue(result.contains("ERROR_STATE"));
        assertTrue(result.contains("ERROR_PROCEDURE"));
        assertTrue(result.contains("ERROR_LINE"));
        assertTrue(result.contains("ERROR_MESSAGE"));
    }

    // Empty CATCH block (valid per spec)
    public void test_empty_catch() throws Exception {
        String sql = "BEGIN TRY\n" +
                "    SELECT 1;\n" +
                "END TRY\n" +
                "BEGIN CATCH\n" +
                "END CATCH";

        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        assertEquals(1, stmtList.size());
    }

    // Nested TRY...CATCH (allowed per spec)
    public void test_nested_try_catch() throws Exception {
        String sql = "BEGIN TRY\n" +
                "    BEGIN TRY\n" +
                "        SELECT 1 / 0;\n" +
                "    END TRY\n" +
                "    BEGIN CATCH\n" +
                "        SELECT ERROR_MESSAGE();\n" +
                "    END CATCH\n" +
                "END TRY\n" +
                "BEGIN CATCH\n" +
                "    SELECT 'outer catch';\n" +
                "END CATCH";

        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        assertEquals(1, stmtList.size());
    }

    // TRY...CATCH with ROLLBACK in CATCH
    public void test_with_rollback() throws Exception {
        String sql = "BEGIN TRY\n" +
                "    BEGIN TRANSACTION;\n" +
                "    INSERT INTO t1 VALUES (1);\n" +
                "    COMMIT;\n" +
                "END TRY\n" +
                "BEGIN CATCH\n" +
                "    ROLLBACK;\n" +
                "END CATCH";

        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        assertEquals(1, stmtList.size());
    }

    // TRY...CATCH output formatting
    public void test_output_format() throws Exception {
        String sql = "BEGIN TRY SELECT 1; END TRY BEGIN CATCH SELECT 2; END CATCH";

        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        assertEquals(1, stmtList.size());

        String result = SQLUtils.toSQLString(stmtList, dbType);
        assertTrue(result.contains("BEGIN TRY"));
        assertTrue(result.contains("END TRY"));
        assertTrue(result.contains("BEGIN CATCH"));
        assertTrue(result.contains("END CATCH"));
    }
}
