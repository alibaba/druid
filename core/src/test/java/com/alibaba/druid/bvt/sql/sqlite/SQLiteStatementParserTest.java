package com.alibaba.druid.bvt.sql.sqlite;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.sqlite.ast.*;
import com.alibaba.druid.sql.dialect.sqlite.visitor.SQLiteSchemaStatVisitor;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import junit.framework.TestCase;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SQLiteStatementParserTest extends TestCase {
    // PRAGMA tests
    public void test_pragma_get() throws Exception {
        String sql = "PRAGMA table_info";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, DbType.sqlite);
        assertEquals(1, stmtList.size());
        assertTrue(stmtList.get(0) instanceof SQLitePragmaStatement);

        SQLitePragmaStatement stmt = (SQLitePragmaStatement) stmtList.get(0);
        assertEquals("table_info", stmt.getName().getSimpleName());
        assertNull(stmt.getValue());

        assertEquals("PRAGMA table_info", SQLUtils.toSQLString(stmt, DbType.sqlite));
    }

    public void test_pragma_set_eq() throws Exception {
        String sql = "PRAGMA cache_size = 10000";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, DbType.sqlite);
        assertEquals(1, stmtList.size());

        SQLitePragmaStatement stmt = (SQLitePragmaStatement) stmtList.get(0);
        assertEquals("cache_size", stmt.getName().getSimpleName());
        assertNotNull(stmt.getValue());

        assertEquals("PRAGMA cache_size = 10000", SQLUtils.toSQLString(stmt, DbType.sqlite));
    }

    public void test_pragma_set_paren() throws Exception {
        String sql = "PRAGMA cache_size(10000)";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, DbType.sqlite);
        assertEquals(1, stmtList.size());

        SQLitePragmaStatement stmt = (SQLitePragmaStatement) stmtList.get(0);
        assertEquals("cache_size", stmt.getName().getSimpleName());
        assertNotNull(stmt.getValue());
    }

    // ATTACH / DETACH tests
    public void test_attach() throws Exception {
        String sql = "ATTACH DATABASE 'test.db' AS test_schema";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, DbType.sqlite);
        assertEquals(1, stmtList.size());
        assertTrue(stmtList.get(0) instanceof SQLiteAttachStatement);

        String output = SQLUtils.toSQLString(stmtList.get(0), DbType.sqlite);
        assertEquals("ATTACH DATABASE 'test.db' AS test_schema", output);
    }

    public void test_detach() throws Exception {
        String sql = "DETACH DATABASE test_schema";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, DbType.sqlite);
        assertEquals(1, stmtList.size());
        assertTrue(stmtList.get(0) instanceof SQLiteDetachStatement);

        String output = SQLUtils.toSQLString(stmtList.get(0), DbType.sqlite);
        assertEquals("DETACH DATABASE test_schema", output);
    }

    // VACUUM test
    public void test_vacuum() throws Exception {
        String sql = "VACUUM";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, DbType.sqlite);
        assertEquals(1, stmtList.size());
        assertTrue(stmtList.get(0) instanceof SQLiteVacuumStatement);

        assertEquals("VACUUM", SQLUtils.toSQLString(stmtList.get(0), DbType.sqlite));
    }

    public void test_vacuum_schema() throws Exception {
        String sql = "VACUUM main";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, DbType.sqlite);
        assertEquals(1, stmtList.size());
        assertTrue(stmtList.get(0) instanceof SQLiteVacuumStatement);

        assertEquals("VACUUM main", SQLUtils.toSQLString(stmtList.get(0), DbType.sqlite));
    }

    // REINDEX test
    public void test_reindex() throws Exception {
        String sql = "REINDEX";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, DbType.sqlite);
        assertEquals(1, stmtList.size());
        assertTrue(stmtList.get(0) instanceof SQLiteReindexStatement);

        assertEquals("REINDEX", SQLUtils.toSQLString(stmtList.get(0), DbType.sqlite));
    }

    public void test_reindex_name() throws Exception {
        String sql = "REINDEX my_index";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, DbType.sqlite);
        assertEquals(1, stmtList.size());
        assertTrue(stmtList.get(0) instanceof SQLiteReindexStatement);

        assertEquals("REINDEX my_index", SQLUtils.toSQLString(stmtList.get(0), DbType.sqlite));
    }

    // CREATE TABLE tests
    public void test_create_table() throws Exception {
        String sql = "CREATE TABLE t1 (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, value REAL)";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, DbType.sqlite);
        assertEquals(1, stmtList.size());

        String output = SQLUtils.toSQLString(stmtList.get(0), DbType.sqlite);
        assertNotNull(output);
        assertTrue(output.contains("AUTOINCREMENT") || output.contains("autoincrement") || output.contains("AUTO_INCREMENT") || output.contains("auto_increment"));
    }

    public void test_create_table_if_not_exists() throws Exception {
        String sql = "CREATE TABLE IF NOT EXISTS t1 (id INTEGER PRIMARY KEY, name TEXT)";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, DbType.sqlite);
        assertEquals(1, stmtList.size());

        String output = SQLUtils.toSQLString(stmtList.get(0), DbType.sqlite);
        assertTrue(output.toUpperCase().contains("IF NOT EXISTS"));
    }

    // INSERT with OR REPLACE/IGNORE
    public void test_insert_or_replace() throws Exception {
        String sql = "INSERT OR REPLACE INTO t1 (id, name) VALUES (1, 'test')";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, DbType.sqlite);
        assertEquals(1, stmtList.size());
    }

    public void test_insert_or_ignore() throws Exception {
        String sql = "INSERT OR IGNORE INTO t1 (id, name) VALUES (1, 'test')";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, DbType.sqlite);
        assertEquals(1, stmtList.size());
    }

    // SELECT with SQLite-specific features
    public void test_select_with_limit_offset() throws Exception {
        String sql = "SELECT * FROM t1 LIMIT 10 OFFSET 5";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, DbType.sqlite);
        assertEquals(1, stmtList.size());

        SchemaStatVisitor visitor = new SQLiteSchemaStatVisitor();
        stmtList.get(0).accept(visitor);
        assertTrue(visitor.getTables().containsKey(new TableStat.Name("t1")));
    }

    // CREATE INDEX
    public void test_create_index() throws Exception {
        String sql = "CREATE INDEX idx_name ON t1 (name)";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, DbType.sqlite);
        assertEquals(1, stmtList.size());
    }

    public void test_create_unique_index() throws Exception {
        String sql = "CREATE UNIQUE INDEX IF NOT EXISTS idx_name ON t1 (name)";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, DbType.sqlite);
        assertEquals(1, stmtList.size());
    }

    // DROP TABLE
    public void test_drop_table() throws Exception {
        String sql = "DROP TABLE IF EXISTS t1";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, DbType.sqlite);
        assertEquals(1, stmtList.size());
    }

    // UPDATE
    public void test_update() throws Exception {
        String sql = "UPDATE t1 SET name = 'new' WHERE id = 1";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, DbType.sqlite);
        assertEquals(1, stmtList.size());

        SchemaStatVisitor visitor = new SQLiteSchemaStatVisitor();
        stmtList.get(0).accept(visitor);
        assertTrue(visitor.getTables().containsKey(new TableStat.Name("t1")));
    }

    // DELETE
    public void test_delete() throws Exception {
        String sql = "DELETE FROM t1 WHERE id = 1";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, DbType.sqlite);
        assertEquals(1, stmtList.size());
    }

    // Backtick quoted identifiers
    public void test_backtick_identifiers() throws Exception {
        String sql = "SELECT `column name` FROM `table name` WHERE `id` = 1";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, DbType.sqlite);
        assertEquals(1, stmtList.size());
    }

    // Square bracket quoted identifiers
    public void test_bracket_identifiers() throws Exception {
        String sql = "SELECT [column name] FROM [table name] WHERE [id] = 1";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, DbType.sqlite);
        assertEquals(1, stmtList.size());
    }

    // Multiple statements
    public void test_multiple_statements() throws Exception {
        String sql = "CREATE TABLE t1 (id INTEGER); INSERT INTO t1 VALUES (1); SELECT * FROM t1";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, DbType.sqlite);
        assertEquals(3, stmtList.size());
    }
}
