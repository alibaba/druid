package com.alibaba.druid.bvt.sql.oracle.issues;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Oracle PL/SQL SELECT ... INTO with multiple targets must render the targets comma-separated
 * without surrounding parentheses (INTO a, b), not INTO (a, b).
 *
 * @see <a href="https://github.com/alibaba/druid/issues/6435">Issue #6435</a>
 */
public class Issue6435 {
    @Test
    public void test_select_into_multiple_targets() {
        String sql = "DECLARE\n"
                + "\ta int;\n"
                + "\tb VARCHAR(10);\n"
                + "BEGIN\n"
                + "\tSELECT 1, 'a' INTO a, b FROM dual;\n"
                + "END;";
        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, DbType.oracle);
        assertEquals(1, stmts.size());
        String out = SQLUtils.toSQLString(stmts.get(0), DbType.oracle);
        assertTrue(out.contains("INTO a, b"), "expected `INTO a, b`, got:\n" + out);
        assertFalse(out.contains("INTO (a, b)"), "INTO targets must not be parenthesized:\n" + out);
    }

    @Test
    public void test_select_into_single_target_unchanged() {
        String sql = "BEGIN SELECT 1 INTO a FROM dual; END;";
        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, DbType.oracle);
        assertEquals(1, stmts.size());
        assertTrue(SQLUtils.toSQLString(stmts.get(0), DbType.oracle).contains("INTO a"));
    }
}
