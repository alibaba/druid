package com.alibaba.druid.bvt.sql.postgresql.issues;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLDropTableStatement;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * PostgreSQL 解析 DROP FOREIGN TABLE 报错的问题。
 *
 * <p>FOREIGN TABLE 是 PostgreSQL 外部表（FDW）的独立语法，druid 原本在 DROP 语句中
 * 未识别 FOREIGN 关键字，抛出 "unsupported DROP target 'FOREIGN'"。
 *
 * @author fudianchn
 * @see <a href="https://github.com/alibaba/druid/issues/6180">Issue #6180</a>
 * @see <a href="https://www.postgresql.org/docs/current/sql-droptable.html">DROP TABLE</a>
 */
public class Issue6180Test {
    @Test
    public void dropForeignTable() {
        String sql = "DROP FOREIGN TABLE odps_user_test";
        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, "postgresql");

        assertEquals(1, stmts.size());
        SQLDropTableStatement drop = (SQLDropTableStatement) stmts.get(0);
        assertTrue(drop.isForeign());
        assertFalse(drop.isExternal());
        // round-trip keeps FOREIGN (PG syntax), must not be rewritten to EXTERNAL
        assertEquals(sql, drop.toString());
    }

    @Test
    public void dropForeignTableIfExistsCascade() {
        String sql = "DROP FOREIGN TABLE IF EXISTS t1, t2 CASCADE";
        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, "postgresql");

        SQLDropTableStatement drop = (SQLDropTableStatement) stmts.get(0);
        assertTrue(drop.isForeign());
        assertTrue(drop.isIfExists());
        assertTrue(drop.isCascade());
        assertEquals(2, drop.getTableSources().size());
    }

    @Test
    public void plainDropTableUnaffected() {
        // regression guard: ordinary DROP TABLE must not set the foreign flag
        SQLDropTableStatement drop = (SQLDropTableStatement)
                SQLUtils.parseStatements("DROP TABLE t1", "postgresql").get(0);
        assertFalse(drop.isForeign());
    }
}
