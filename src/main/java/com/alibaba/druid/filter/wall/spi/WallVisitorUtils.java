package com.alibaba.druid.filter.wall.spi;

import com.alibaba.druid.filter.wall.IllegalConditionViolation;
import com.alibaba.druid.filter.wall.WallVisitor;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOperator;
import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import com.alibaba.druid.sql.ast.expr.SQLLiteralExpr;
import com.alibaba.druid.sql.ast.expr.SQLNotExpr;
import com.alibaba.druid.sql.dialect.mysql.ast.expr.MySqlBooleanExpr;

public class WallVisitorUtils {

    public static void check(WallVisitor visitor, SQLBinaryOpExpr x) {
        if (Boolean.TRUE == getObject(x)) {
            visitor.getViolations().add(new IllegalConditionViolation(SQLUtils.toSQLString(x)));
        }
    }

    public static Object getObject(SQLBinaryOpExpr x) {
        if (x.getLeft() instanceof SQLLiteralExpr && x.getRight() instanceof SQLLiteralExpr) {
            if (x.getOperator() == SQLBinaryOperator.Equality) {
                String leftLiteral = SQLUtils.toOracleString(x.getLeft());
                String rightLiteral = SQLUtils.toOracleString(x.getRight());

                return leftLiteral.equalsIgnoreCase(rightLiteral);
            }

            if (x.getOperator() == SQLBinaryOperator.NotEqual || x.getOperator() == SQLBinaryOperator.LessThanOrGreater) {
                String leftLiteral = SQLUtils.toSQLString(x.getLeft());
                String rightLiteral = SQLUtils.toSQLString(x.getRight());

                return !leftLiteral.equalsIgnoreCase(rightLiteral);
            }
        }

        if (x.getOperator() == SQLBinaryOperator.Like) {
            if (x.getRight() instanceof SQLCharExpr) {
                String text = ((SQLCharExpr) x.getRight()).getText();

                if (text.length() >= 0) {
                    for (char ch : text.toCharArray()) {
                        if (ch != '%') {
                            return null;
                        }
                    }

                    return true;
                }

            }
        }

        x.getLeft().setParent(x);
        x.getRight().setParent(x);

        if (x.getOperator() == SQLBinaryOperator.BooleanOr) {
            Object leftResult = getValue(x.getLeft());
            Object rightResult = getValue(x.getRight());

            if (Boolean.TRUE == leftResult || Boolean.TRUE == rightResult) {
                return true;
            }
        }

        if (x.getOperator() == SQLBinaryOperator.BooleanAnd) {
            Object leftResult = getValue(x.getLeft());
            Object rightResult = getValue(x.getRight());

            if (Boolean.FALSE == leftResult || Boolean.FALSE == rightResult) {
                return false;
            }

            if (Boolean.TRUE == leftResult && Boolean.TRUE == rightResult) {
                return true;
            }
        }

        return null;
    }

    public static Object getValue(SQLExpr x) {
        if (x instanceof SQLBinaryOpExpr) {
            return getObject((SQLBinaryOpExpr) x);
        }

        if (x instanceof MySqlBooleanExpr) {
            return ((MySqlBooleanExpr) x).getValue();
        }

        if (x instanceof SQLNotExpr) {
            Object result = getValue(((SQLNotExpr) x).getExpr());
            if (result != null && result instanceof Boolean) {
                return !((Boolean) result).booleanValue();
            }
        }

        return null;
    }
}
