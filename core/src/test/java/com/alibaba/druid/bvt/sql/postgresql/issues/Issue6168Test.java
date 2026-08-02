package com.alibaba.druid.bvt.sql.postgresql.issues;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLTableLike;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * PostgreSQL CREATE TABLE 的 LIKE 子句 INCLUDING DEFAULTS/CONSTRAINTS/INDEXES 解析失败。
 *
 * @author fudianchn
 * @see <a href="https://github.com/alibaba/druid/issues/6168">Issue #6168</a>
 * @see <a href="https://www.postgresql.org/docs/current/sql-createtable.html">CREATE TABLE ... LIKE</a>
 */
public class Issue6168Test {
    @Test
    public void pgLikeIncludingMultiple() {
        String sql = "CREATE TABLE new_table (\n"
            + "\tLIKE existing_table INCLUDING DEFAULTS INCLUDING CONSTRAINTS INCLUDING INDEXES\n"
            + ")";
        SQLCreateTableStatement stmt = (SQLCreateTableStatement)
                SQLUtils.parseStatements(sql, "postgresql").get(0);

        SQLTableLike tableLike = (SQLTableLike) stmt.getTableElementList().get(0);
        assertTrue(tableLike.isIncludeDefaults());
        assertTrue(tableLike.isIncludeConstraints());
        assertTrue(tableLike.isIncludeIndexes());

        String out = stmt.toString();
        assertTrue(out.contains("INCLUDING DEFAULTS"), out);
        assertTrue(out.contains("INCLUDING CONSTRAINTS"), out);
        assertTrue(out.contains("INCLUDING INDEXES"), out);
    }

    @Test
    public void pgLikeMixedWithColumns() {
        String sql = "CREATE TABLE archived_orders (\n"
            + "\tLIKE orders INCLUDING CONSTRAINTS,\n"
            + "\tarchived_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP\n"
            + ")";
        SQLCreateTableStatement stmt = (SQLCreateTableStatement)
                SQLUtils.parseStatements(sql, "postgresql").get(0);
        assertEquals(2, stmt.getTableElementList().size());
        SQLTableLike tableLike = (SQLTableLike) stmt.getTableElementList().get(0);
        assertTrue(tableLike.isIncludeConstraints());
    }
}
