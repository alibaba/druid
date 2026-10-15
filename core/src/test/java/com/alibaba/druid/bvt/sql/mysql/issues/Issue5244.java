package com.alibaba.druid.bvt.sql.mysql.issues;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLHexExpr;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * MySQL empty hex literal X'' must not be rendered as the invalid 0x (0x needs at least one digit).
 *
 * @see <a href="https://github.com/alibaba/druid/issues/5244">Issue #5244</a>
 */
public class Issue5244 {
    private static String rt(String sql) {
        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, DbType.mysql);
        assertEquals(1, stmts.size());
        return SQLUtils.toSQLString(stmts.get(0), DbType.mysql).replaceAll("\\s+", " ").trim();
    }

    @Test
    public void test_empty_hex_literal() {
        String out = rt("INSERT INTO `t` (`id`, `v`) VALUES (NULL, X'')");
        assertTrue(out.contains("X''"), out);
        assertFalse(out.contains("0x)") || out.contains("0x "), "must not emit a bare 0x: " + out);
    }

    @Test
    public void test_non_empty_hex_literal_unchanged() {
        assertTrue(rt("INSERT INTO t (v) VALUES (X'41')").contains("0x41"));
    }

    @Test
    public void test_empty_hex_tostring_path() {
        // SQLObjectImpl.toString() goes through SQLHexExpr.output(), not the visitor
        assertEquals("X''", new SQLHexExpr("").toString());
        assertEquals("X''", new SQLHexExpr((String) null).toString());
        assertEquals("0x41", new SQLHexExpr("41").toString());
    }
}
