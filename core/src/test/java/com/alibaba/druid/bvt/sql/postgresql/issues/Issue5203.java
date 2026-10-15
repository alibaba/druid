package com.alibaba.druid.bvt.sql.postgresql.issues;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * PostgreSQL row-locking clauses: FOR UPDATE / FOR NO KEY UPDATE / FOR SHARE / FOR KEY SHARE.
 *
 * @see <a href="https://github.com/alibaba/druid/issues/5203">Issue #5203</a>
 */
public class Issue5203 {
    private static String rt(String sql) {
        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, DbType.postgresql);
        assertEquals(1, stmts.size());
        return SQLUtils.toSQLString(stmts.get(0), DbType.postgresql).replaceAll("\\s+", " ").trim();
    }

    @Test
    public void test_for_key_share() {
        assertEquals("SELECT id FROM t WHERE id = 1 FOR KEY SHARE",
                rt("SELECT id FROM t WHERE id = 1 FOR KEY SHARE"));
    }

    @Test
    public void test_for_no_key_update() {
        assertEquals("SELECT id FROM t WHERE id = 1 FOR NO KEY UPDATE",
                rt("SELECT id FROM t WHERE id = 1 FOR NO KEY UPDATE"));
    }

    @Test
    public void test_for_update_and_share_unchanged() {
        assertEquals("SELECT id FROM t FOR UPDATE", rt("SELECT id FROM t FOR UPDATE"));
        assertEquals("SELECT id FROM t FOR SHARE", rt("SELECT id FROM t FOR SHARE"));
    }

    @Test
    public void test_for_key_share_with_of() {
        assertEquals("SELECT * FROM t FOR KEY SHARE OF t",
                rt("SELECT * FROM t FOR KEY SHARE OF t"));
    }

    @Test
    public void test_for_no_key_update_with_nowait() {
        assertEquals("SELECT * FROM t FOR NO KEY UPDATE NOWAIT",
                rt("SELECT * FROM t FOR NO KEY UPDATE NOWAIT"));
    }

    @Test
    public void test_for_key_share_skip_locked() {
        assertEquals("SELECT * FROM t FOR KEY SHARE SKIP LOCKED",
                rt("SELECT * FROM t FOR KEY SHARE SKIP LOCKED"));
    }
}
