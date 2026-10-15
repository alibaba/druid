package com.alibaba.druid.bvt.sql.oracle.issues;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Oracle TRUNCATE TABLE ... DROP STORAGE / REUSE STORAGE.
 *
 * @see <a href="https://github.com/alibaba/druid/issues/4972">Issue #4972</a>
 */
public class Issue4972 {
    private static String rt(String sql) {
        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, DbType.oracle);
        assertEquals(1, stmts.size());
        return SQLUtils.toSQLString(stmts.get(0), DbType.oracle).replaceAll("\\s+", " ").trim();
    }

    @Test
    public void test_drop_storage() {
        assertEquals("TRUNCATE TABLE xxx DROP STORAGE", rt("TRUNCATE TABLE xxx DROP STORAGE"));
    }

    @Test
    public void test_reuse_storage() {
        assertEquals("TRUNCATE TABLE xxx REUSE STORAGE", rt("TRUNCATE TABLE xxx REUSE STORAGE"));
    }

    @Test
    public void test_plain_truncate_unchanged() {
        assertEquals("TRUNCATE TABLE xxx", rt("TRUNCATE TABLE xxx"));
    }
}
