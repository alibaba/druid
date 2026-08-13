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
 * Round-trip test for Oracle {@code AT LOCAL} rendering.
 *
 * <p>Regression for issue #6533: lowercase rendering ({@code ucase=false}) of {@code AT LOCAL}
 * emitted {@code "alter session set "} (a stray copy from {@code visit(OracleAlterSessionStatement)})
 * instead of {@code " at local"}, corrupting the generated SQL.
 *
 * @see <a href="https://github.com/alibaba/druid/issues/6533">issue #6533</a>
 */
public class Issue6533 {
    private static List<SQLStatement> parse(String sql) {
        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, DbType.oracle);
        assertEquals(1, stmts.size(), () -> "expected exactly one statement for: " + sql);
        return stmts;
    }

    // The parentheses are required: a bare "d AT LOCAL" as a select item hits an unrelated
    // select-item parser quirk; "(d AT LOCAL)" parses cleanly into an OracleDatetimeExpr.
    private static final String SQL = "SELECT (d AT LOCAL) FROM DUAL";

    @Test
    public void atLocal_lowercaseRender() {
        // Exercises the full parse -> AST -> output path, not a hand-built node.
        List<SQLStatement> stmts = parse(SQL);
        String out = SQLUtils.toSQLString(stmts, DbType.oracle, new SQLUtils.FormatOption(false));

        // " at local" can only be emitted by visit(OracleDatetimeExpr), so its presence
        // proves the parser produced an OracleDatetimeExpr and the lowercase branch is correct.
        assertTrue(out.contains(" at local"), () -> "lowercase render should contain ' at local', got: " + out);
        assertFalse(out.contains("alter session set"), () -> "lowercase render polluted by 'alter session set': " + out);
    }

    @Test
    public void atLocal_uppercaseRender() {
        List<SQLStatement> stmts = parse(SQL);
        String out = SQLUtils.toSQLString(stmts, DbType.oracle);

        assertTrue(out.contains(" AT LOCAL"), () -> "uppercase render should contain ' AT LOCAL', got: " + out);
    }
}
