package com.alibaba.druid.bvt.sql.odps.issues;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableCompact;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableStatement;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @see <a href="https://github.com/alibaba/druid/issues/6708">Issue来源</a>
 */
public class Issue6708 {
    @Test
    public void test_compact_minor() {
        SQLStatement stmt = parse("ALTER TABLE t compact minor");
        assertEquals("ALTER TABLE t\n\tCOMPACT MINOR", stmt.toString());

        SQLAlterTableCompact item = compactItem(stmt);
        assertFalse(item.isMajor());
        assertTrue(item.getPartition().isEmpty());
    }

    @Test
    public void test_compact_major() {
        SQLStatement stmt = parse("ALTER TABLE t compact major");
        assertEquals("ALTER TABLE t\n\tCOMPACT MAJOR", stmt.toString());

        SQLAlterTableCompact item = compactItem(stmt);
        assertTrue(item.isMajor());
        assertTrue(item.getPartition().isEmpty());
    }

    @Test
    public void test_compact_partition_major() {
        SQLStatement stmt = parse("ALTER TABLE t PARTITION (pt='2026') compact major");
        assertEquals("ALTER TABLE t\n\tPARTITION (pt = '2026') COMPACT MAJOR", stmt.toString());

        SQLAlterTableCompact item = compactItem(stmt);
        assertTrue(item.isMajor());
        assertEquals(1, item.getPartition().size());
    }

    @Test
    public void test_compact_partition_minor() {
        SQLStatement stmt = parse("ALTER TABLE t PARTITION (pt='2026') compact minor");
        assertEquals("ALTER TABLE t\n\tPARTITION (pt = '2026') COMPACT MINOR", stmt.toString());

        SQLAlterTableCompact item = compactItem(stmt);
        assertFalse(item.isMajor());
        assertEquals(1, item.getPartition().size());
    }

    private static SQLStatement parse(String sql) {
        return SQLUtils.parseStatements(sql, DbType.odps).get(0);
    }

    private static SQLAlterTableCompact compactItem(SQLStatement stmt) {
        return (SQLAlterTableCompact) ((SQLAlterTableStatement) stmt).getItems().get(0);
    }
}
