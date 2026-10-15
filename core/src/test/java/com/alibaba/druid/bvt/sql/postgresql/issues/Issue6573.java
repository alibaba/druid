package com.alibaba.druid.bvt.sql.postgresql.issues;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * PostgreSQL stores the COLLATE clause on the (character) data type. It was dropped on output for
 * both casts in ORDER BY (e.g. (a)::text COLLATE "x") and column definitions.
 *
 * @see <a href="https://github.com/alibaba/druid/issues/6573">Issue #6573</a>
 */
public class Issue6573 {
    private static String rt(String sql) {
        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, DbType.postgresql);
        assertEquals(1, stmts.size());
        return SQLUtils.toSQLString(stmts.get(0), DbType.postgresql).replaceAll("\\s+", " ").trim();
    }

    @Test
    public void test_order_by_cast_collate() {
        assertEquals("SELECT id FROM t ORDER BY (a)::text COLLATE \"zh-Hans-x-icu\" DESC",
                rt("SELECT id FROM t ORDER BY (a)::text COLLATE \"zh-Hans-x-icu\" DESC"));
    }

    @Test
    public void test_order_by_simple_collate_unchanged() {
        assertTrue(rt("SELECT id FROM t ORDER BY name COLLATE \"x\"").contains("COLLATE \"x\""));
    }

    @Test
    public void test_column_definition_collate() {
        String out = rt("CREATE TABLE t (a VARCHAR(10) COLLATE \"default\", b int)");
        assertTrue(out.contains("VARCHAR(10) COLLATE \"default\""), "column COLLATE must be kept: " + out);
    }
}
