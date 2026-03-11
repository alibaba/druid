package com.alibaba.druid.bvt.sql.postgresql;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.postgresql.ast.stmt.PGCopyStatement;
import junit.framework.TestCase;

import java.util.List;

public class PGCopyTest extends TestCase {
    private final DbType dbType = DbType.postgresql;

    public void test_copy_from_file() throws Exception {
        String sql = "COPY my_table FROM '/tmp/data.csv' WITH (FORMAT csv, HEADER true)";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        assertEquals(1, stmtList.size());
        PGCopyStatement stmt = (PGCopyStatement) stmtList.get(0);
        assertFalse(stmt.isDirectionTo());
        String result = SQLUtils.toPGString(stmt);
        assertTrue(result.contains("FROM"));
        assertTrue(result.contains("FORMAT"));
    }

    public void test_copy_to_file() throws Exception {
        String sql = "COPY my_table TO '/tmp/data.csv' WITH (FORMAT csv)";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        assertEquals(1, stmtList.size());
        PGCopyStatement stmt = (PGCopyStatement) stmtList.get(0);
        assertTrue(stmt.isDirectionTo());
        String result = SQLUtils.toPGString(stmt);
        assertTrue(result.contains("TO"));
        assertFalse(result.contains("FROM"));
    }

    public void test_copy_with_columns() throws Exception {
        String sql = "COPY my_table (col1, col2, col3) FROM '/tmp/data.csv'";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        assertEquals(1, stmtList.size());
        PGCopyStatement stmt = (PGCopyStatement) stmtList.get(0);
        assertEquals(3, stmt.getColumns().size());
    }

    public void test_copy_from_stdin() throws Exception {
        String sql = "COPY my_table FROM STDIN";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        assertEquals(1, stmtList.size());
        String result = SQLUtils.toPGString(stmtList.get(0));
        assertTrue(result.contains("STDIN"));
    }

    public void test_copy_to_stdout() throws Exception {
        String sql = "COPY my_table TO STDOUT";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        assertEquals(1, stmtList.size());
        String result = SQLUtils.toPGString(stmtList.get(0));
        assertTrue(result.contains("TO"));
        assertTrue(result.contains("STDOUT"));
    }

    public void test_copy_from_program() throws Exception {
        String sql = "COPY my_table FROM PROGRAM 'gzip -d < /tmp/data.csv.gz'";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        assertEquals(1, stmtList.size());
        PGCopyStatement stmt = (PGCopyStatement) stmtList.get(0);
        assertTrue(stmt.isProgram());
        String result = SQLUtils.toPGString(stmt);
        assertTrue(result.contains("PROGRAM"));
    }

    public void test_copy_query_to_file() throws Exception {
        String sql = "COPY (SELECT id, name FROM users WHERE active = true) TO '/tmp/users.csv' WITH (FORMAT csv, HEADER true)";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        assertEquals(1, stmtList.size());
        PGCopyStatement stmt = (PGCopyStatement) stmtList.get(0);
        assertNotNull(stmt.getQuery());
        assertTrue(stmt.isDirectionTo());
        String result = SQLUtils.toPGString(stmt);
        assertTrue(result.contains("TO"));
        assertTrue(result.contains("SELECT"));
    }

    public void test_copy_from_with_where() throws Exception {
        String sql = "COPY my_table FROM '/tmp/data.csv' WITH (FORMAT csv) WHERE id > 100";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        assertEquals(1, stmtList.size());
        PGCopyStatement stmt = (PGCopyStatement) stmtList.get(0);
        assertNotNull(stmt.getWhere());
        String result = SQLUtils.toPGString(stmt);
        assertTrue(result.contains("WHERE"));
    }

    public void test_copy_with_all_options() throws Exception {
        String sql = "COPY my_table FROM '/tmp/data.csv' WITH (FORMAT csv, DELIMITER ',', NULL 'N', HEADER true, QUOTE '|', ESCAPE '|', ENCODING 'UTF8')";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        assertEquals(1, stmtList.size());
        PGCopyStatement stmt = (PGCopyStatement) stmtList.get(0);
        assertEquals(7, stmt.getOptions().size());
    }

    public void test_copy_query_to_stdout() throws Exception {
        String sql = "COPY (SELECT * FROM orders WHERE date > '2024-01-01') TO STDOUT WITH (FORMAT csv)";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        assertEquals(1, stmtList.size());
        PGCopyStatement stmt = (PGCopyStatement) stmtList.get(0);
        assertNotNull(stmt.getQuery());
        assertTrue(stmt.isDirectionTo());
        String result = SQLUtils.toPGString(stmt);
        assertTrue(result.contains("STDOUT"));
        assertTrue(result.contains("TO"));
    }
}
