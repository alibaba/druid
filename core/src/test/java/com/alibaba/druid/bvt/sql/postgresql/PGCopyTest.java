package com.alibaba.druid.bvt.sql.postgresql;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.dialect.postgresql.ast.stmt.PGCopyStatement;
import junit.framework.TestCase;

/**
 * AST property tests for PGCopyStatement.
 * SQL formatting tests are covered by resource file postgresql/23.txt via PGResourceTest.
 */
public class PGCopyTest extends TestCase {
    private final DbType dbType = DbType.postgresql;

    public void test_direction_from() throws Exception {
        String sql = "COPY my_table FROM '/tmp/data.csv' WITH (FORMAT csv, HEADER true)";
        PGCopyStatement stmt = (PGCopyStatement) SQLUtils.parseSingleStatement(sql, dbType);
        assertFalse(stmt.isDirectionTo());
        assertNotNull(stmt.getTable());
        assertNull(stmt.getQuery());
    }

    public void test_direction_to() throws Exception {
        String sql = "COPY my_table TO '/tmp/data.csv' WITH (FORMAT csv)";
        PGCopyStatement stmt = (PGCopyStatement) SQLUtils.parseSingleStatement(sql, dbType);
        assertTrue(stmt.isDirectionTo());
    }

    public void test_columns() throws Exception {
        String sql = "COPY my_table (col1, col2, col3) FROM '/tmp/data.csv'";
        PGCopyStatement stmt = (PGCopyStatement) SQLUtils.parseSingleStatement(sql, dbType);
        assertEquals(3, stmt.getColumns().size());
    }

    public void test_program() throws Exception {
        String sql = "COPY my_table FROM PROGRAM 'gzip -d < /tmp/data.csv.gz'";
        PGCopyStatement stmt = (PGCopyStatement) SQLUtils.parseSingleStatement(sql, dbType);
        assertTrue(stmt.isProgram());
        assertFalse(stmt.isDirectionTo());
    }

    public void test_query() throws Exception {
        String sql = "COPY (SELECT id, name FROM users WHERE active = true) TO '/tmp/users.csv' WITH (FORMAT csv, HEADER true)";
        PGCopyStatement stmt = (PGCopyStatement) SQLUtils.parseSingleStatement(sql, dbType);
        assertNotNull(stmt.getQuery());
        assertNull(stmt.getTable());
        assertTrue(stmt.isDirectionTo());
    }

    public void test_where() throws Exception {
        String sql = "COPY my_table FROM '/tmp/data.csv' WITH (FORMAT csv) WHERE id > 100";
        PGCopyStatement stmt = (PGCopyStatement) SQLUtils.parseSingleStatement(sql, dbType);
        assertNotNull(stmt.getWhere());
    }

    public void test_options_count() throws Exception {
        String sql = "COPY my_table FROM '/tmp/data.csv' WITH (FORMAT csv, DELIMITER ',', NULL 'N', HEADER true, QUOTE '|', ESCAPE '|', ENCODING 'UTF8')";
        PGCopyStatement stmt = (PGCopyStatement) SQLUtils.parseSingleStatement(sql, dbType);
        assertEquals(7, stmt.getOptions().size());
    }
}
