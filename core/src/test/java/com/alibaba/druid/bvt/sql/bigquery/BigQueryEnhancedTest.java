package com.alibaba.druid.bvt.sql.bigquery;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLCallStatement;
import com.alibaba.druid.sql.ast.statement.SQLCreateDatabaseStatement;
import com.alibaba.druid.sql.ast.statement.SQLCreateFunctionStatement;
import com.alibaba.druid.sql.ast.statement.SQLCreateProcedureStatement;
import com.alibaba.druid.sql.ast.statement.SQLSetStatement;
import com.alibaba.druid.sql.dialect.bigquery.ast.BigQueryExportDataStatement;
import com.alibaba.druid.sql.dialect.bigquery.visitor.BigQuerySchemaStatVisitor;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BigQueryEnhancedTest {
    private static final DbType DB_TYPE = DbType.bigquery;

    private SQLStatement parseOne(String sql) {
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, DB_TYPE);
        SQLStatement stmt = parser.parseStatement();
        return stmt;
    }

    private List<SQLStatement> parseList(String sql) {
        return SQLUtils.parseStatements(sql, DB_TYPE);
    }

    @Test
    public void testCreateSchema() {
        SQLStatement stmt = parseOne("CREATE SCHEMA my_project.my_dataset");
        assertInstanceOf(SQLCreateDatabaseStatement.class, stmt);
    }

    @Test
    public void testCreateSchemaIfNotExists() {
        SQLStatement stmt = parseOne("CREATE SCHEMA IF NOT EXISTS my_project.my_dataset OPTIONS(location = 'us')");
        SQLCreateDatabaseStatement createDb = (SQLCreateDatabaseStatement) stmt;
        assertTrue(createDb.isIfNotExists());
        assertNotNull(createDb.getOptions().get("location"));
    }

    @Test
    public void testCreateProcedure() {
        String sql = "CREATE OR REPLACE PROCEDURE my_proj.my_ds.my_proc(x INT64, y STRING)\n"
                + "BEGIN\n  SELECT x;\nEND";
        SQLStatement stmt = parseOne(sql);
        assertInstanceOf(SQLCreateProcedureStatement.class, stmt);
        SQLCreateProcedureStatement proc = (SQLCreateProcedureStatement) stmt;
        assertTrue(proc.isOrReplace());
        assertEquals(2, proc.getParameters().size());
    }

    @Test
    public void testCreateFunction() {
        String sql = "CREATE TEMP FUNCTION add_one(x INT64) RETURNS INT64 AS (x + 1)";
        SQLStatement stmt = parseOne(sql);
        assertInstanceOf(SQLCreateFunctionStatement.class, stmt);
        SQLCreateFunctionStatement func = (SQLCreateFunctionStatement) stmt;
        assertTrue(func.isTemporary());
        assertEquals(1, func.getParameters().size());
    }

    @Test
    public void testCreateOrReplaceFunction() {
        String sql = "CREATE OR REPLACE FUNCTION my_udf(a STRING) RETURNS STRING LANGUAGE js AS \"\"\"\n  return a;\n\"\"\"";
        SQLStatement stmt = parseOne(sql);
        assertInstanceOf(SQLCreateFunctionStatement.class, stmt);
        SQLCreateFunctionStatement func = (SQLCreateFunctionStatement) stmt;
        assertTrue(func.isOrReplace());
        assertEquals("js", func.getLanguage());
    }

    @Test
    public void testCreateFunctionIfNotExists() {
        String sql = "CREATE FUNCTION IF NOT EXISTS my_func(x INT64) RETURNS INT64 AS (x * 2)";
        SQLStatement stmt = parseOne(sql);
        assertInstanceOf(SQLCreateFunctionStatement.class, stmt);
        SQLCreateFunctionStatement func = (SQLCreateFunctionStatement) stmt;
        assertTrue(func.isIfNotExists());
    }

    @Test
    public void testExportData() {
        String sql = "EXPORT DATA OPTIONS (uri = 'gs://bucket/*.csv', format = 'CSV') AS (SELECT * FROM t)";
        SQLStatement stmt = parseOne(sql);
        assertInstanceOf(BigQueryExportDataStatement.class, stmt);
        BigQueryExportDataStatement exportStmt = (BigQueryExportDataStatement) stmt;
        assertEquals(2, exportStmt.getOptions().size());
        assertNotNull(exportStmt.getAsSelect());
    }

    @Test
    public void testExportDataWithConnection() {
        String sql = "EXPORT DATA WITH CONNECTION my_conn OPTIONS (uri = 'gs://bucket/*.csv') AS (SELECT 1)";
        SQLStatement stmt = parseOne(sql);
        assertInstanceOf(BigQueryExportDataStatement.class, stmt);
        BigQueryExportDataStatement exportStmt = (BigQueryExportDataStatement) stmt;
        assertNotNull(exportStmt.getConnection());
    }

    @Test
    public void testCallStatement() {
        String sql = "CALL my_project.my_dataset.my_proc(1, 'hello')";
        SQLStatement stmt = parseOne(sql);
        assertInstanceOf(SQLCallStatement.class, stmt);
    }

    @Test
    public void testSetStatement() {
        SQLStatement stmt = parseOne("SET x = 5");
        assertInstanceOf(SQLSetStatement.class, stmt);
    }

    @Test
    public void testSchemaStatVisitor() {
        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(DB_TYPE);
        assertInstanceOf(BigQuerySchemaStatVisitor.class, visitor);

        String sql = "SELECT a, b FROM my_project.my_dataset.my_table WHERE c > 1";
        SQLStatement stmt = parseOne(sql);
        stmt.accept(visitor);
        assertEquals(1, visitor.getTables().size());
        assertTrue(visitor.getColumns().size() >= 2);
    }

    @Test
    public void testDataTypeAliases() {
        // BigQuery supports type aliases like INT, INTEGER, FLOAT, DECIMAL, BOOLEAN
        String sql = "CREATE TABLE t (a INT64, b FLOAT64, c NUMERIC, d BOOL, e STRING, f BYTES, g JSON, h GEOGRAPHY)";
        SQLStatement stmt = parseOne(sql);
        String output = SQLUtils.toSQLString(stmt, DB_TYPE);
        assertNotNull(output);
        // Round-trip parse should succeed
        SQLStatement stmt2 = parseOne(output);
        assertEquals(SQLUtils.toSQLString(stmt, DB_TYPE), SQLUtils.toSQLString(stmt2, DB_TYPE));
    }

    @Test
    public void testComplexCreateTable() {
        String sql = "CREATE TABLE t (\n"
                + "  id INT64 NOT NULL,\n"
                + "  data STRUCT<name STRING, value FLOAT64>,\n"
                + "  tags ARRAY<STRING>,\n"
                + "  PRIMARY KEY (id) NOT ENFORCED\n"
                + ") PARTITION BY DATE(created_at) CLUSTER BY name OPTIONS (description = 'test')";
        SQLStatement stmt = parseOne(sql);
        String output = SQLUtils.toSQLString(stmt, DB_TYPE);
        assertTrue(output.contains("PARTITION BY"));
        assertTrue(output.contains("CLUSTER BY"));
        assertTrue(output.contains("OPTIONS"));
    }

    @Test
    public void testProceduralBlock() {
        String sql = "BEGIN\n  DECLARE x INT64 DEFAULT 0;\n  SET x = 10;\n  IF x > 5 THEN\n    SELECT x;\n  END IF;\nEND";
        List<SQLStatement> stmts = parseList(sql);
        assertEquals(1, stmts.size());
        String output = SQLUtils.toSQLString(stmts, DB_TYPE);
        assertTrue(output.contains("DECLARE"));
        assertTrue(output.contains("SET"));
        assertTrue(output.contains("IF"));
    }

    @Test
    public void testExecuteImmediate() {
        String sql = "EXECUTE IMMEDIATE 'SELECT ?' INTO result USING 42 AS x";
        SQLStatement stmt = parseOne(sql);
        String output = SQLUtils.toSQLString(stmt, DB_TYPE);
        assertTrue(output.contains("EXECUTE IMMEDIATE"));
        assertTrue(output.contains("INTO result"));
        assertTrue(output.contains("USING 42 AS x"));
    }

    @Test
    public void testMergeStatement() {
        String sql = "MERGE INTO t USING s ON t.id = s.id "
                + "WHEN MATCHED THEN UPDATE SET t.val = s.val "
                + "WHEN NOT MATCHED THEN INSERT (id, val) VALUES (s.id, s.val)";
        SQLStatement stmt = parseOne(sql);
        String output = SQLUtils.toSQLString(stmt, DB_TYPE);
        assertTrue(output.contains("MERGE INTO"));
        assertTrue(output.contains("WHEN MATCHED"));
        assertTrue(output.contains("WHEN NOT MATCHED"));
    }

    @Test
    public void testTruncateTable() {
        SQLStatement stmt = parseOne("TRUNCATE TABLE my_project.my_dataset.my_table");
        String output = SQLUtils.toSQLString(stmt, DB_TYPE);
        assertTrue(output.contains("TRUNCATE TABLE"));
    }

    @Test
    public void testExportDataRoundTrip() {
        String sql = "EXPORT DATA OPTIONS (uri = 'gs://bucket/data', format = 'JSON') AS (SELECT * FROM t)";
        SQLStatement stmt = parseOne(sql);
        String output = SQLUtils.toSQLString(stmt, DB_TYPE).trim();
        // Round-trip
        SQLStatement stmt2 = parseOne(output);
        String output2 = SQLUtils.toSQLString(stmt2, DB_TYPE).trim();
        assertEquals(output, output2);
    }

    @Test
    public void testCreateProcedureRoundTrip() {
        String sql = "CREATE PROCEDURE my_proc(x INT64) BEGIN SELECT x; END";
        SQLStatement stmt = parseOne(sql);
        String output = SQLUtils.toSQLString(stmt, DB_TYPE).trim();
        // Verify no IS keyword
        assertFalse(output.contains("\nIS\n"));
    }
}
