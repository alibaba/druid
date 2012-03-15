package com.alibaba.druid.filter.wall.spi;

import com.alibaba.druid.filter.wall.IllegalSQLObjectViolation;
import com.alibaba.druid.filter.wall.WallVisitor;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.SQLAggregateExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOperator;
import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import com.alibaba.druid.sql.ast.expr.SQLMethodInvokeExpr;
import com.alibaba.druid.sql.ast.expr.SQLNCharExpr;
import com.alibaba.druid.sql.ast.expr.SQLNotExpr;
import com.alibaba.druid.sql.ast.expr.SQLNullExpr;
import com.alibaba.druid.sql.ast.expr.SQLNumericLiteralExpr;
import com.alibaba.druid.sql.ast.expr.SQLQueryExpr;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLSelectQuery;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLSubqueryTableSource;
import com.alibaba.druid.sql.ast.statement.SQLTableSource;
import com.alibaba.druid.sql.dialect.mysql.ast.expr.MySqlBooleanExpr;

public class WallVisitorUtils {

    public static void check(WallVisitor visitor, SQLBinaryOpExpr x) {
        if (Boolean.TRUE == getValue(x)) {
            visitor.getViolations().add(new IllegalSQLObjectViolation(SQLUtils.toSQLString(x)));
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static Object getValue(SQLBinaryOpExpr x) {
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
            return getValue((SQLBinaryOpExpr) x);
        }

        if (x instanceof MySqlBooleanExpr) {
            return ((MySqlBooleanExpr) x).getValue();
        }

        if (x instanceof SQLNumericLiteralExpr) {
            return ((SQLNumericLiteralExpr) x).getNumber();
        }
        
        if (x instanceof SQLCharExpr) {
            return ((SQLCharExpr) x).getText();
        }
        
        if (x instanceof SQLNCharExpr) {
            return ((SQLNCharExpr) x).getText();
        }

        if (x instanceof SQLNotExpr) {
            Object result = getValue(((SQLNotExpr) x).getExpr());
            if (result != null && result instanceof Boolean) {
                return !((Boolean) result).booleanValue();
            }
        }

        if (x instanceof SQLQueryExpr) {
            if (isSimpleCountTableSource(((SQLQueryExpr) x).getSubQuery())) {
                return Integer.valueOf(1);
            }
        }

        if (x instanceof SQLMethodInvokeExpr) {
            return getValue((SQLMethodInvokeExpr) x);
        }

        return null;
    }

    public static Object getValue(SQLMethodInvokeExpr x) {
        String methodName = x.getMethodName();
        if ("len".equalsIgnoreCase(methodName) || "length".equalsIgnoreCase(methodName)) {
            Object firstValue = null;
            if (x.getParameters().size() > 0) {
                firstValue = (getValue(x.getParameters().get(0)));
            }

            if (firstValue instanceof String) {
                return ((String) firstValue).length();
            }
        }

        return null;
    }

    public static boolean isSimpleCountTableSource(SQLTableSource tableSource) {
        if (!(tableSource instanceof SQLSubqueryTableSource)) {
            return false;
        }

        SQLSubqueryTableSource subQuery = (SQLSubqueryTableSource) tableSource;

        return isSimpleCountTableSource(subQuery.getSelect());
    }

    public static boolean isSimpleCountTableSource(SQLSelect select) {
        SQLSelectQuery query = select.getQuery();

        if (query instanceof SQLSelectQueryBlock) {
            SQLSelectQueryBlock queryBlock = (SQLSelectQueryBlock) query;

            boolean allawTrueWhere = false;
            
            if (queryBlock.getWhere() == null) {
                allawTrueWhere = true;
            } else {
                Object whereValue = getValue(queryBlock.getWhere());
                if (whereValue == Boolean.TRUE) {
                    allawTrueWhere = true;
                } else if (whereValue == Boolean.FALSE) {
                    return false;
                }
            }
            boolean simpleCount = false;
            if (queryBlock.getSelectList().size() == 1) {
                SQLExpr selectItemExpr = queryBlock.getSelectList().get(0).getExpr();
                if (selectItemExpr instanceof SQLAggregateExpr) {
                    if (((SQLAggregateExpr) selectItemExpr).getMethodName().equalsIgnoreCase("COUNT")) {
                        simpleCount = true;
                    }
                }
            }

            if (allawTrueWhere && simpleCount) {
                return true;
            }
        }

        return false;
    }
}
