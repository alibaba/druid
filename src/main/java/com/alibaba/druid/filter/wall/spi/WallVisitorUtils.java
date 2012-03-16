package com.alibaba.druid.filter.wall.spi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Enumeration;
import java.util.Set;

import com.alibaba.druid.filter.wall.IllegalSQLObjectViolation;
import com.alibaba.druid.filter.wall.WallProvider;
import com.alibaba.druid.filter.wall.WallVisitor;
import com.alibaba.druid.logging.Log;
import com.alibaba.druid.logging.LogFactory;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.expr.SQLAggregateExpr;
import com.alibaba.druid.sql.ast.expr.SQLAllColumnExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOperator;
import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLIntegerExpr;
import com.alibaba.druid.sql.ast.expr.SQLMethodInvokeExpr;
import com.alibaba.druid.sql.ast.expr.SQLNCharExpr;
import com.alibaba.druid.sql.ast.expr.SQLNotExpr;
import com.alibaba.druid.sql.ast.expr.SQLNullExpr;
import com.alibaba.druid.sql.ast.expr.SQLNumericLiteralExpr;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.ast.expr.SQLQueryExpr;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLSelectItem;
import com.alibaba.druid.sql.ast.statement.SQLSelectQuery;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLSubqueryTableSource;
import com.alibaba.druid.sql.ast.statement.SQLTableSource;
import com.alibaba.druid.sql.dialect.mysql.ast.expr.MySqlBooleanExpr;
import com.alibaba.druid.util.JdbcUtils;

public class WallVisitorUtils {

    private final static Log LOG = LogFactory.getLog(WallVisitorUtils.class);

    public static void check(WallVisitor visitor, SQLPropertyExpr x) {
        if (x.getOwner() instanceof SQLIdentifierExpr) {
            String owner = x.getOwner().toString();
            owner = WallVisitorUtils.form(owner);
            if (visitor.containsPermitObjects(owner)) {
                visitor.getViolations().add(new IllegalSQLObjectViolation(visitor.toSQL(x)));
            }
        }
    }

    public static void checkSelelctCondition(WallVisitor visitor, SQLExpr x) {
        if (x == null) {
            return;
        }
        
        WallProvider provider = visitor.getProvider();
        if (!provider.isCheckSelectAlwayTrueCondition()) {
            return;
        }

        if (Boolean.TRUE == getValue(x)) {
            if (x instanceof SQLBinaryOpExpr) {
                SQLBinaryOpExpr binaryOpExpr = (SQLBinaryOpExpr) x;
                if (binaryOpExpr.getOperator() == SQLBinaryOperator.Equality || binaryOpExpr.getOperator() == SQLBinaryOperator.NotEqual) {
                    if (binaryOpExpr.getLeft() instanceof SQLIntegerExpr && binaryOpExpr.getRight() instanceof SQLIntegerExpr) {
                        return;
                    }
                }
            }

            visitor.getViolations().add(new IllegalSQLObjectViolation(SQLUtils.toSQLString(x)));
        }
    }

    public static void checkCondition(WallVisitor visitor, SQLExpr x) {
        if (x == null) {
            return;
        }

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

        if (leftResult == null || rightResult == null) {
            return null;
        }

        if (x.getOperator() == SQLBinaryOperator.Equality) {
            if (x.getLeft() instanceof SQLNullExpr && x.getRight() instanceof SQLNullExpr) {
                return true;
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

        if (x.getOperator() == SQLBinaryOperator.Concat) {
            return leftResult.toString() + rightResult.toString();
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

        if ("if".equalsIgnoreCase(methodName) && x.getParameters().size() == 3) {
            SQLExpr first = x.getParameters().get(0);
            Object firstResult = getValue(first);

            if (Boolean.TRUE == firstResult) {
                return getValue(x.getParameters().get(1));
            }

            if (Boolean.FALSE == firstResult) {
                getValue(x.getParameters().get(2));
            }
        }

        if ("chr".equalsIgnoreCase(methodName) && x.getParameters().size() == 1) {
            SQLExpr first = x.getParameters().get(0);
            Object firstResult = getValue(first);
            if (firstResult instanceof Number) {
                int intValue = ((Number) firstResult).intValue();
                char ch = (char) intValue;

                return "" + ch;
            }
        }

        if ("concat".equalsIgnoreCase(methodName)) {
            StringBuffer buf = new StringBuffer();
            for (SQLExpr expr : x.getParameters()) {
                Object value = getValue(expr);
                if (value == null) {
                    return null;
                }

                buf.append(value.toString());
            }
            return buf.toString();
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

    public static void check(WallVisitor visitor, SQLMethodInvokeExpr x) {
        if (x.getOwner() instanceof SQLIdentifierExpr) {
            String owner = x.getOwner().toString();
            owner = WallVisitorUtils.form(owner);
            if (visitor.containsPermitObjects(owner)) {
                visitor.getViolations().add(new IllegalSQLObjectViolation(visitor.toSQL(x)));
            }
        }

        String methodName = x.getMethodName();

        if (visitor.getProvider().getPermitFunctions().contains(methodName.toLowerCase())) {
            visitor.getViolations().add(new IllegalSQLObjectViolation(visitor.toSQL(x)));
        }

    }

    public static void check(WallVisitor visitor, SQLExprTableSource x) {
        SQLExpr expr = x.getExpr();

        String tableName = null;
        if (expr instanceof SQLPropertyExpr) {
            SQLPropertyExpr propExpr = (SQLPropertyExpr) expr;

            if (propExpr.getOwner() instanceof SQLIdentifierExpr) {
                String ownerName = ((SQLIdentifierExpr) propExpr.getOwner()).getName();

                ownerName = form(ownerName);

                if (visitor.getProvider().getPermitSchemas().contains(ownerName.toLowerCase())) {
                    visitor.getViolations().add(new IllegalSQLObjectViolation(visitor.toSQL(x)));
                }
            }

            tableName = propExpr.getName();
        }

        if (expr instanceof SQLIdentifierExpr) {
            tableName = ((SQLIdentifierExpr) expr).getName();
        }

        if (tableName != null) {
            tableName = form(tableName);
            if (visitor.containsPermitTable(tableName)) {
                visitor.getViolations().add(new IllegalSQLObjectViolation(visitor.toSQL(x)));
            }
        }
    }

    public static boolean queryBlockFromIsNull(SQLSelectQuery query) {
        if (query instanceof SQLSelectQueryBlock) {
            SQLSelectQueryBlock queryBlock = (SQLSelectQueryBlock) query;
            SQLTableSource from = queryBlock.getFrom();

            if (from == null) {
                return true;
            }

            if (from instanceof SQLExprTableSource) {
                SQLExpr fromExpr = ((SQLExprTableSource) from).getExpr();
                if (fromExpr instanceof SQLName) {
                    String name = fromExpr.toString();

                    name = form(name);

                    if (name.equalsIgnoreCase("DUAL")) {
                        return true;
                    }
                }
            }

            if (queryBlock.getSelectList().size() == 1
                && queryBlock.getSelectList().get(0).getExpr() instanceof SQLAllColumnExpr) {
                if (from instanceof SQLSubqueryTableSource) {
                    SQLSelectQuery subQuery = ((SQLSubqueryTableSource) from).getSelect().getQuery();
                    if (queryBlockFromIsNull(subQuery)) {
                        return true;
                    }
                }
            }

            boolean allIsConst = true;
            for (SQLSelectItem item : queryBlock.getSelectList()) {
                if (getValue(item.getExpr()) == null) {
                    allIsConst = false;
                    break;
                }
            }
            if (allIsConst) {
                return true;
            }
        }

        return false;
    }

    public static String form(String name) {
        if (name.startsWith("\"") && name.endsWith("\"")) {
            name = name.substring(1, name.length() - 1);
        }
        if (name.startsWith("`") && name.endsWith("`")) {
            name = name.substring(1, name.length() - 1);
        }

        name = name.toLowerCase();
        return name;
    }

    public static void loadResource(Set<String> names, String resource) {
        try {
            Enumeration<URL> e = Thread.currentThread().getContextClassLoader().getResources(resource);
            while (e.hasMoreElements()) {
                URL url = e.nextElement();
                InputStream in = null;
                BufferedReader reader = null;
                try {
                    in = url.openStream();
                    reader = new BufferedReader(new InputStreamReader(in));
                    for (;;) {
                        String line = reader.readLine();
                        if (line == null) {
                            break;
                        }
                        line = line.trim();
                        if (line.length() > 0) {
                            line = line.toLowerCase();
                            names.add(line);
                        }
                    }

                    url.openStream();
                } finally {
                    JdbcUtils.close(reader);
                    JdbcUtils.close(in);
                }
            }
        } catch (IOException e) {
            LOG.error("load oracle permit tables errror", e);
        }
    }
}
