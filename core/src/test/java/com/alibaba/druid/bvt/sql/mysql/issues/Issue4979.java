package com.alibaba.druid.bvt.sql.mysql.issues;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * MySQL XA transaction id with up to three comma-separated parts: gtrid[, bqual[, formatID]].
 *
 * @see <a href="https://github.com/alibaba/druid/issues/4979">Issue #4979</a>
 */
public class Issue4979 {
    private static String rt(String sql) {
        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, DbType.mysql);
        assertEquals(1, stmts.size());
        return SQLUtils.toSQLString(stmts.get(0), DbType.mysql).replaceAll("\\s+", " ").trim();
    }

    @Test
    public void test_xa_start_multi_part_xid() {
        assertEquals("XA START 0x31, 0x2d, 0x26", rt("XA START 0x31,0x2d,0x26"));
    }

    @Test
    public void test_xa_start_single_xid_unchanged() {
        assertEquals("XA START 0x31", rt("XA START 0x31"));
    }

    @Test
    public void test_xa_other_commands_share_xid_parsing() {
        // xid parsing is shared across all XA command types
        assertEquals("XA END 0x31, 0x2d, 0x26", rt("XA END 0x31,0x2d,0x26"));
        assertEquals("XA COMMIT 0x31, 0x2d, 0x26", rt("XA COMMIT 0x31,0x2d,0x26"));
    }

    @Test
    public void test_xa_xid_more_than_three_parts_is_error() {
        // MySQL allows at most gtrid, bqual, formatID
        assertThrows(Exception.class, () -> rt("XA START 0x31,0x2d,0x26,0x99"));
    }
}
