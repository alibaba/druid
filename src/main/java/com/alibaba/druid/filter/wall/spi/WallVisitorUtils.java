package com.alibaba.druid.filter.wall.spi;

import com.alibaba.druid.filter.wall.IllegalConditionViolation;
import com.alibaba.druid.filter.wall.WallVisitor;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOperator;
import com.alibaba.druid.sql.ast.expr.SQLLiteralExpr;

public class WallVisitorUtils {

    public static void check(WallVisitor visitor, SQLBinaryOpExpr x) {
        if (x.getLeft() instanceof SQLLiteralExpr && x.getRight() instanceof SQLLiteralExpr) {
            if (x.getOperator() == SQLBinaryOperator.Equality) {
                String leftLiteral = SQLUtils.toOracleString(x.getLeft());
                String rightLiteral = SQLUtils.toOracleString(x.getRight());

                if (leftLiteral.equalsIgnoreCase(rightLiteral)) {
                    visitor.getViolations().add(new IllegalConditionViolation(SQLUtils.toOracleString(x)));
                }
            } else if (x.getOperator() == SQLBinaryOperator.NotEqual) {
                String leftLiteral = SQLUtils.toOracleString(x.getLeft());
                String rightLiteral = SQLUtils.toOracleString(x.getRight());

                if (!leftLiteral.equalsIgnoreCase(rightLiteral)) {
                    visitor.getViolations().add(new IllegalConditionViolation(SQLUtils.toOracleString(x)));
                }
            }
        }
    }
}
