package com.alibaba.druid.bvt.sql.issues;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOperator;
import com.alibaba.druid.sql.ast.expr.SQLIntegerExpr;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * A programmatically-built SQLBinaryOpExpr must emit parentheses when operator precedence
 * requires them, otherwise the SQL meaning changes (e.g. (5 + 3) / 2 vs 5 + 3 / 2).
 *
 * @see <a href="https://github.com/alibaba/druid/issues/6408">Issue #6408</a>
 */
public class Issue6408 {
    private static SQLBinaryOpExpr bin(Object l, SQLBinaryOperator op, Object r) {
        return new SQLBinaryOpExpr(
                l instanceof Integer ? new SQLIntegerExpr((Integer) l) : (com.alibaba.druid.sql.ast.SQLExpr) l,
                op,
                r instanceof Integer ? new SQLIntegerExpr((Integer) r) : (com.alibaba.druid.sql.ast.SQLExpr) r);
    }

    @Test
    public void test_left_add_under_divide() {
        SQLBinaryOpExpr expr = bin(bin(5, SQLBinaryOperator.Add, 3), SQLBinaryOperator.Divide, 2);
        assertEquals("(5 + 3) / 2", expr.toString());
    }

    @Test
    public void test_right_add_under_divide() {
        SQLBinaryOpExpr expr = bin(2, SQLBinaryOperator.Divide, bin(5, SQLBinaryOperator.Add, 3));
        assertEquals("2 / (5 + 3)", expr.toString());
    }

    @Test
    public void test_left_add_under_multiply() {
        SQLBinaryOpExpr expr = bin(bin(5, SQLBinaryOperator.Add, 3), SQLBinaryOperator.Multiply, 2);
        assertEquals("(5 + 3) * 2", expr.toString());
    }

    @Test
    public void test_right_subtract_associativity() {
        SQLBinaryOpExpr expr = bin(2, SQLBinaryOperator.Subtract, bin(5, SQLBinaryOperator.Subtract, 3));
        assertEquals("2 - (5 - 3)", expr.toString());
    }

    @Test
    public void test_no_spurious_parens_when_not_needed() {
        // 5 + 3 / 2 : divide binds tighter, no parens needed
        SQLBinaryOpExpr expr = bin(5, SQLBinaryOperator.Add, bin(3, SQLBinaryOperator.Divide, 2));
        assertEquals("5 + 3 / 2", expr.toString());
    }

    @Test
    public void test_parsed_sql_roundtrip_unchanged() {
        // parsing must not introduce or drop parentheses (whitespace/newlines are normalized away)
        for (String sql : new String[]{
                "SELECT (5 + 3) / 2",
                "SELECT 5 + 3 / 2",
                "SELECT a - b - c FROM t",
                "SELECT a - (b - c) FROM t",
                "SELECT a + b * c FROM t",
                "SELECT a * (b + c) FROM t"}) {
            List<SQLStatement> stmts = SQLUtils.parseStatements(sql, DbType.mysql);
            String actual = SQLUtils.toSQLString(stmts.get(0), DbType.mysql).replaceAll("\\s+", " ").trim();
            assertEquals(sql, actual, "roundtrip: " + sql);
        }
    }
}
