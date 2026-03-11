package com.alibaba.druid.bvt.sql.sqlserver.issues;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLCreateProcedureStatement;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @see <a href="https://github.com/alibaba/druid/issues/6590">Issue 6590: SQL Server ALTER PROCEDURE with WITH EXECUTE AS CALLER</a>
 */
public class Issue6590 {
    @Test
    public void test_alter_procedure_with_execute_as_caller() {
        String sql = "ALTER PROCEDURE [dbo].[sp_FJZ_UKB]\n" +
                "    @OIS_Flag INT,\n" +
                "    @OIS_Type INT,\n" +
                "    @OIS_Style INT,\n" +
                "    @VoucherCode VARCHAR(100),\n" +
                "    @VoucherCodeNew VARCHAR(100),\n" +
                "    @ExecutedBy VARCHAR(20)\n" +
                "    WITH EXECUTE AS CALLER\n" +
                "AS\n" +
                "BEGIN\n" +
                "    SET NOCOUNT ON;\n" +
                "    SET XACT_ABORT ON;\n" +
                "    BEGIN TRY\n" +
                "        BEGIN TRANSACTION;\n" +
                "        SELECT 1;\n" +
                "        COMMIT TRANSACTION;\n" +
                "    END TRY\n" +
                "    BEGIN CATCH\n" +
                "        ROLLBACK TRANSACTION;\n" +
                "    END CATCH\n" +
                "END";

        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, DbType.sqlserver);
        List<SQLStatement> statementList = parser.parseStatementList();
        assertEquals(1, statementList.size());

        SQLStatement stmt = statementList.get(0);
        assertTrue(stmt instanceof SQLCreateProcedureStatement);

        SQLCreateProcedureStatement proc = (SQLCreateProcedureStatement) stmt;
        assertFalse(proc.isCreate());
        assertEquals("[dbo].[sp_FJZ_UKB]", proc.getName().toString());
        assertEquals(6, proc.getParameters().size());
        assertNotNull(proc.getAuthid());
        assertEquals("CALLER", proc.getAuthid().toString());
        assertNotNull(proc.getBlock());
    }

    @Test
    public void test_create_procedure() {
        String sql = "CREATE PROCEDURE [dbo].[sp_Test]\n" +
                "    @Param1 INT,\n" +
                "    @Param2 VARCHAR(100)\n" +
                "AS\n" +
                "BEGIN\n" +
                "    SET NOCOUNT ON;\n" +
                "END";

        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, DbType.sqlserver);
        List<SQLStatement> statementList = parser.parseStatementList();
        assertEquals(1, statementList.size());

        SQLStatement stmt = statementList.get(0);
        assertTrue(stmt instanceof SQLCreateProcedureStatement);

        SQLCreateProcedureStatement proc = (SQLCreateProcedureStatement) stmt;
        assertTrue(proc.isCreate());
        assertEquals(2, proc.getParameters().size());
        assertNotNull(proc.getBlock());
    }

    @Test
    public void test_alter_procedure_simple() {
        String sql = "ALTER PROCEDURE [dbo].[sp_Test]\n" +
                "    @Param1 INT\n" +
                "AS\n" +
                "BEGIN\n" +
                "    SELECT 1;\n" +
                "END";

        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, DbType.sqlserver);
        List<SQLStatement> statementList = parser.parseStatementList();
        assertEquals(1, statementList.size());

        SQLCreateProcedureStatement proc = (SQLCreateProcedureStatement) statementList.get(0);
        assertFalse(proc.isCreate());
        assertEquals(1, proc.getParameters().size());
    }
}
