package com.alibaba.druid.bvt.sql.dm;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLParseAssertUtil;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DM_DDLTest {
    private final DbType dbType = DbType.dm;

    @Test
    public void test_drop_table() {
        String sql = "DROP TABLE IF EXISTS t1";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        assertEquals(1, stmtList.size());
        SQLParseAssertUtil.assertParseSql(sql, dbType);
    }

    @Test
    public void test_alter_table_add_column() {
        String sql = "ALTER TABLE t1 ADD COLUMN age INT";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        assertEquals(1, stmtList.size());
    }

    @Test
    public void test_alter_table_drop_column() {
        String sql = "ALTER TABLE t1 DROP COLUMN age";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        assertEquals(1, stmtList.size());
    }

    @Test
    public void test_create_index() {
        String sql = "CREATE INDEX idx_name ON t1 (name)";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        assertEquals(1, stmtList.size());
        SQLParseAssertUtil.assertParseSql(sql, dbType);
    }

    @Test
    public void test_create_unique_index() {
        String sql = "CREATE UNIQUE INDEX idx_id ON t1 (id)";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        assertEquals(1, stmtList.size());
        SQLParseAssertUtil.assertParseSql(sql, dbType);
    }

    @Test
    public void test_drop_index() {
        String sql = "DROP INDEX idx_name";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        assertEquals(1, stmtList.size());
    }

    @Test
    public void test_truncate_table() {
        String sql = "TRUNCATE TABLE t1";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        assertEquals(1, stmtList.size());
        SQLParseAssertUtil.assertParseSql(sql, dbType);
    }

    @Test
    public void test_create_view() {
        String sql = "CREATE VIEW v1 AS SELECT id, name FROM t1 WHERE status = 1";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        assertEquals(1, stmtList.size());
        SQLParseAssertUtil.assertParseSql(sql, dbType);
    }

    @Test
    public void test_create_or_replace_view() {
        String sql = "CREATE OR REPLACE VIEW v1 AS SELECT id, name FROM t1 WHERE status = 1";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        assertEquals(1, stmtList.size());
    }
}
