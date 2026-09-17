package com.alibaba.druid.bvt.sql.issues;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOperator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Issue6720 {
    @Test
    public void test_right_or_under_and() {
        assertCombined("colA = 1 AND colB = 2 AND (col1 = 1 OR col2 = 2)",
                "colA = 1 and colB = 2", SQLBinaryOperator.BooleanAnd, "col1 = 1 or col2 = 2");
    }

    @Test
    public void test_left_or_under_and() {
        assertCombined("(a = 1 OR b = 2) AND c = 3", "a = 1 or b = 2",
                SQLBinaryOperator.BooleanAnd, "c = 3");
    }

    @Test
    public void test_explicit_parentheses() {
        assertCombined("a = 1 AND (b = 2 OR c = 3)", "a = 1",
                SQLBinaryOperator.BooleanAnd, "(b = 2 or c = 3)");
        assertCombined("(a = 1 OR b = 2) AND c = 3", "(a = 1 or b = 2)",
                SQLBinaryOperator.BooleanAnd, "c = 3");
    }

    @Test
    public void test_xor_precedence() {
        assertCombined("a = 1 AND (b = 2 XOR c = 3)", "a = 1",
                SQLBinaryOperator.BooleanAnd, "b = 2 xor c = 3");
        assertCombined("(a = 1 XOR b = 2) AND c = 3", "a = 1 xor b = 2",
                SQLBinaryOperator.BooleanAnd, "c = 3");
        assertCombined("a = 1 XOR (b = 2 OR c = 3)", "a = 1",
                SQLBinaryOperator.BooleanXor, "b = 2 or c = 3");
        assertCombined("(a = 1 OR b = 2) XOR c = 3", "a = 1 or b = 2",
                SQLBinaryOperator.BooleanXor, "c = 3");
    }

    @Test
    public void test_higher_precedence_and_same_operator() {
        assertCombined("a = 1 OR b = 2 AND c = 3", "a = 1",
                SQLBinaryOperator.BooleanOr, "b = 2 and c = 3");
        assertCombined("a = 1 AND b = 2 AND c = 3", "a = 1",
                SQLBinaryOperator.BooleanAnd, "b = 2 and c = 3");
    }

    private void assertCombined(String expected, String left, SQLBinaryOperator operator, String right) {
        SQLBinaryOpExpr expression = new SQLBinaryOpExpr(
                SQLUtils.toSQLExpr(left), operator, SQLUtils.toSQLExpr(right));
        assertEquals(expected, SQLUtils.toSQLString(expression).replaceAll("\\s+", " ").trim());
    }
}
