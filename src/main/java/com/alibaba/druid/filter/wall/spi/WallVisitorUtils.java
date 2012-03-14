package com.alibaba.druid.filter.wall.spi;

import com.alibaba.druid.filter.wall.IllegalConditionViolation;
import com.alibaba.druid.filter.wall.WallVisitor;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOperator;
import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import com.alibaba.druid.sql.ast.expr.SQLNotExpr;
import com.alibaba.druid.sql.ast.expr.SQLNullExpr;
import com.alibaba.druid.sql.ast.expr.SQLNumericLiteralExpr;
import com.alibaba.druid.sql.dialect.mysql.ast.expr.MySqlBooleanExpr;

public class WallVisitorUtils {

    public static void check(WallVisitor visitor, SQLBinaryOpExpr x) {
        if (Boolean.TRUE == getObject(x)) {
            visitor.getViolations().add(new IllegalConditionViolation(SQLUtils.toSQLString(x)));
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static Object getObject(SQLBinaryOpExpr x) {
        x.getLeft().setParent(x);
        x.getRight().setParent(x);
        Object leftResult = getValue(x.getLeft());
        Object rightResult = getValue(x.getRight());

        if (x.getOperator() == SQLBinaryOperator.BooleanOr) {
            if (Boolean.TRUE == leftResult || Boolean.TRUE == rightResult) {
                return true;
            }
        }

        if (x.getOperator() == SQLBinaryOperator.BooleanAnd) {
            if (Boolean.FALSE == leftResult || Boolean.FALSE == rightResult) {
                return false;
            }

            if (Boolean.TRUE == leftResult && Boolean.TRUE == rightResult) {
                return true;
            }
        }
        
        if (x.getOperator() == SQLBinaryOperator.Equality) {
            if (x.getLeft() instanceof SQLNullExpr && x.getRight() instanceof SQLNullExpr) {
                return true;
            }
            
            if (leftResult == null || rightResult == null) {
                return null;
            }
            
            return leftResult.equals(rightResult);
        }
        
        if (x.getOperator() == SQLBinaryOperator.NotEqual || x.getOperator() == SQLBinaryOperator.LessThanOrGreater) {
            if (x.getLeft() instanceof SQLNullExpr && x.getRight() instanceof SQLNullExpr) {
                return false;
            }
            
            if (leftResult == null || rightResult == null) {
                return null;
            }
            
            return !leftResult.equals(rightResult);
        }
        
        if (x.getOperator() == SQLBinaryOperator.GreaterThan) {
            if (x.getLeft() instanceof SQLNullExpr && x.getRight() instanceof SQLNullExpr) {
                return false;
            }
            
            if (leftResult == null || rightResult == null) {
                return null;
            }
            
            if (leftResult instanceof Comparable) {
                return (((Comparable) leftResult).compareTo(rightResult) > 0);
            }
        }
        
        if (x.getOperator() == SQLBinaryOperator.GreaterThanOrEqual) {
            if (x.getLeft() instanceof SQLNullExpr && x.getRight() instanceof SQLNullExpr) {
                return false;
            }
            
            if (leftResult == null || rightResult == null) {
                return null;
            }
            
            if (leftResult instanceof Comparable) {
                return ((Comparable) leftResult).compareTo(rightResult) >= 0;
            }
        }
        
        if (x.getOperator() == SQLBinaryOperator.LessThan) {
            if (x.getLeft() instanceof SQLNullExpr && x.getRight() instanceof SQLNullExpr) {
                return false;
            }
            
            if (leftResult == null || rightResult == null) {
                return null;
            }
            
            if (leftResult instanceof Comparable) {
                return (((Comparable) leftResult).compareTo(rightResult) < 0);
            }
        }
        
        if (x.getOperator() == SQLBinaryOperator.LessThanOrEqual) {
            if (x.getLeft() instanceof SQLNullExpr && x.getRight() instanceof SQLNullExpr) {
                return false;
            }
            
            if (leftResult == null || rightResult == null) {
                return null;
            }
            
            if (leftResult instanceof Comparable) {
                return ((Comparable) leftResult).compareTo(rightResult) <= 0;
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

        return null;
    }

    public static Object getValue(SQLExpr x) {
        if (x instanceof SQLBinaryOpExpr) {
            return getObject((SQLBinaryOpExpr) x);
        }

        if (x instanceof MySqlBooleanExpr) {
            return ((MySqlBooleanExpr) x).getValue();
        }

        if (x instanceof SQLNumericLiteralExpr) {
            return ((SQLNumericLiteralExpr) x).getNumber();
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
