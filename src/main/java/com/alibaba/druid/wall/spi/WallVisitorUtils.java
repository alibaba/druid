/*
 * Copyright 1999-2011 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.druid.wall.spi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.expr.SQLAggregateExpr;
import com.alibaba.druid.sql.ast.expr.SQLAllColumnExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOperator;
import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLInListExpr;
import com.alibaba.druid.sql.ast.expr.SQLIntegerExpr;
import com.alibaba.druid.sql.ast.expr.SQLMethodInvokeExpr;
import com.alibaba.druid.sql.ast.expr.SQLNCharExpr;
import com.alibaba.druid.sql.ast.expr.SQLNotExpr;
import com.alibaba.druid.sql.ast.expr.SQLNullExpr;
import com.alibaba.druid.sql.ast.expr.SQLNumberExpr;
import com.alibaba.druid.sql.ast.expr.SQLNumericLiteralExpr;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.ast.expr.SQLQueryExpr;
import com.alibaba.druid.sql.ast.statement.SQLDeleteStatement;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLInsertInto;
import com.alibaba.druid.sql.ast.statement.SQLJoinTableSource;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLSelectItem;
import com.alibaba.druid.sql.ast.statement.SQLSelectQuery;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLSubqueryTableSource;
import com.alibaba.druid.sql.ast.statement.SQLTableSource;
import com.alibaba.druid.sql.ast.statement.SQLUnionQuery;
import com.alibaba.druid.sql.ast.statement.SQLUpdateStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.expr.MySqlBooleanExpr;
import com.alibaba.druid.sql.visitor.ExportParameterVisitor;
import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;
import com.alibaba.druid.util.JdbcUtils;
import com.alibaba.druid.util.ServletPathMatcher;
import com.alibaba.druid.wall.WallProvider;
import com.alibaba.druid.wall.WallVisitor;
import com.alibaba.druid.wall.violation.IllegalSQLObjectViolation;

public class WallVisitorUtils {

    private final static Log                          LOG          = LogFactory.getLog(WallVisitorUtils.class);

    public static void check(WallVisitor visitor, SQLInListExpr x) {

    }

    public static void check(WallVisitor visitor, SQLBinaryOpExpr x) {

    }

    public static void check(WallVisitor visitor, SQLSelectItem x) {
        if (visitor.getConfig().isSelectAllColumnAllow()) {
            return;
        }

        if (x.getExpr() instanceof SQLAllColumnExpr //
            && x.getParent() instanceof SQLSelectQueryBlock) {
            SQLSelectQueryBlock queryBlock = (SQLSelectQueryBlock) x.getParent();
            SQLTableSource from = queryBlock.getFrom();

            if (from instanceof SQLExprTableSource) {
                addViolation(visitor, x);
            }
        }
    }

    public static void check(WallVisitor visitor, SQLPropertyExpr x) {
        checkSchema(visitor, x.getOwner());
    }

    public static void checkInsert(WallVisitor visitor, SQLInsertInto x) {
        checkReadOnly(visitor, x.getTableSource());

        if (!visitor.getConfig().isInsertAllow()) {
            addViolation(visitor, x);
        }
    }

    public static void checkSelelct(WallVisitor visitor, SQLSelectQueryBlock x) {
        for (SQLSelectItem item : x.getSelectList()) {
            item.setParent(x);
        }

        if (x.getInto() != null) {
            checkReadOnly(visitor, x.getInto());
        }

        if (!visitor.getConfig().isSelectIntoAllow() && x.getInto() != null) {
            addViolation(visitor, x);
            return;
        }

        if (!visitor.getConfig().isSelectWhereAlwayTrueCheck()) {
            return;
        }

        SQLExpr where = x.getWhere();

        if (where != null) {
            x.getWhere().setParent(x);
            checkCondition(visitor, x.getWhere());

            if (Boolean.TRUE == getConditionValue(visitor, where)) {
                boolean isSimpleConstExpr = false;
                if (where instanceof SQLBinaryOpExpr) {
                    SQLBinaryOpExpr binaryOpExpr = (SQLBinaryOpExpr) where;
                    if (binaryOpExpr.getOperator() == SQLBinaryOperator.Equality
                        || binaryOpExpr.getOperator() == SQLBinaryOperator.NotEqual) {
                        if (binaryOpExpr.getLeft() instanceof SQLIntegerExpr
                            && binaryOpExpr.getRight() instanceof SQLIntegerExpr) {
                            isSimpleConstExpr = true;
                        }
                    }
                }

                if (!isSimpleConstExpr) {
                    addViolation(visitor, x);
                }
            }

        }
        checkConditionForMultiTenant(visitor, x.getWhere(), x);
    }

    public static void checkHaving(WallVisitor visitor, SQLExpr x) {
        if (x == null) {
            return;
        }

        if (!visitor.getConfig().isSelectHavingAlwayTrueCheck()) {
            return;
        }

        if (Boolean.TRUE == getConditionValue(visitor, x)) {
            addViolation(visitor, x);
        }
    }

    public static void checkDelete(WallVisitor visitor, SQLDeleteStatement x) {
        checkReadOnly(visitor, x.getExprTableSource());

        if (!visitor.getConfig().isDeleteAllow()) {
            addViolation(visitor, x);
            return;
        }

        if (!visitor.getConfig().isDeleteWhereAlwayTrueCheck()) {
            return;
        }

        if (x.getWhere() == null || Boolean.TRUE == getConditionValue(visitor, x.getWhere())) {
            addViolation(visitor, x);
            return;
        }

        x.getWhere().setParent(x);
        checkCondition(visitor, x.getWhere());
        checkConditionForMultiTenant(visitor, x.getWhere(), x);
    }

    private static void checkCondition(WallVisitor visitor, SQLExpr x) {
        if (x == null) {
            return;
        }

        if (visitor.getConfig().isMustParameterized()) {
            ExportParameterVisitor exportParameterVisitor = visitor.getProvider().createExportParameterVisitor();
            x.accept(exportParameterVisitor);

            if (exportParameterVisitor.getParameters().size() > 0) {
                addViolation(visitor, x);
                return;
            }
        }

    }

    public static void checkConditionForMultiTenant(WallVisitor visitor, SQLExpr x, SQLObject parent) {
        String tenantTablePattern = visitor.getConfig().getTenantTablePattern();
        if (tenantTablePattern == null || tenantTablePattern.length() == 0) {
            return;
        }

        if (parent == null) {
            throw new IllegalStateException("parent is null");
        }

        String alias = null;
        SQLTableSource tableSource;
        if (parent instanceof SQLDeleteStatement) {
            tableSource = ((SQLDeleteStatement) parent).getTableSource();
        } else if (parent instanceof SQLUpdateStatement) {
            tableSource = ((SQLUpdateStatement) parent).getTableSource();
        } else if (parent instanceof SQLSelectQueryBlock) {
            tableSource = ((SQLSelectQueryBlock) parent).getFrom();
        } else {
            throw new IllegalStateException("not support parent : " + parent.getClass());
        }

        String matchTableName = null;
        if (tableSource instanceof SQLExprTableSource) {
            SQLExpr tableExpr = ((SQLExprTableSource) tableSource).getExpr();

            if (tableExpr instanceof SQLIdentifierExpr) {
                String tableName = ((SQLIdentifierExpr) tableExpr).getName();
                if (ServletPathMatcher.getInstance().matches(tenantTablePattern, tableName)) {
                    matchTableName = tableName;
                    alias = tableSource.getAlias();
                }
            }
        } else if (tableSource instanceof SQLJoinTableSource) {
            SQLJoinTableSource join = (SQLJoinTableSource) tableSource;
            if (join.getLeft() instanceof SQLExprTableSource) {
                SQLExpr tableExpr = ((SQLExprTableSource) join.getLeft()).getExpr();

                if (tableExpr instanceof SQLIdentifierExpr) {
                    String tableName = ((SQLIdentifierExpr) tableExpr).getName();
                    if (ServletPathMatcher.getInstance().matches(tenantTablePattern, tableName)) {
                        matchTableName = tableName;
                        alias = join.getLeft().getAlias();
                    }
                }

                checkJoinConditionForMultiTenant(visitor, join, false);
            } else {
                checkJoinConditionForMultiTenant(visitor, join, true);
            }
        }

        if (matchTableName == null) {
            return;
        }

        SQLBinaryOpExpr tenantCondition = cretateTenantCondition(visitor, alias);

        SQLExpr condition;
        if (x == null) {
            condition = tenantCondition;
        } else {
            condition = new SQLBinaryOpExpr(tenantCondition, SQLBinaryOperator.BooleanAnd, x);
        }

        if (parent instanceof SQLDeleteStatement) {
            ((SQLDeleteStatement) parent).setWhere(condition);
        } else if (parent instanceof SQLUpdateStatement) {
            ((SQLUpdateStatement) parent).setWhere(condition);
        } else if (parent instanceof SQLSelectQueryBlock) {
            ((SQLSelectQueryBlock) parent).setWhere(condition);
        }
    }

    public static void checkJoinConditionForMultiTenant(WallVisitor visitor, SQLJoinTableSource join, boolean checkLeft) {
        String tenantTablePattern = visitor.getConfig().getTenantTablePattern();
        if (tenantTablePattern == null || tenantTablePattern.length() == 0) {
            return;
        }

        SQLExpr condition = join.getCondition();

        if (checkLeft) {

        }

        SQLTableSource right = join.getRight();
        if (right instanceof SQLExprTableSource) {
            SQLExpr tableExpr = ((SQLExprTableSource) right).getExpr();

            if (tableExpr instanceof SQLIdentifierExpr) {
                String tableName = ((SQLIdentifierExpr) tableExpr).getName();
                if (ServletPathMatcher.getInstance().matches(tenantTablePattern, tableName)) {
                    String alias = right.getAlias();
                    if (alias == null) {
                        alias = tableName;
                    }
                    SQLBinaryOpExpr tenantCondition = cretateTenantCondition(visitor, alias);

                    if (condition == null) {
                        condition = tenantCondition;
                    } else {
                        condition = new SQLBinaryOpExpr(tenantCondition, SQLBinaryOperator.BooleanAnd, condition);
                    }
                }
            }
        }

        if (condition != join.getCondition()) {
            join.setCondition(condition);
        }
    }

    private static SQLBinaryOpExpr cretateTenantCondition(WallVisitor visitor, String alias) {
        SQLExpr left, right;
        if (alias != null) {
            left = new SQLPropertyExpr(new SQLIdentifierExpr(alias), visitor.getConfig().getTenantColumn());
        } else {
            left = new SQLIdentifierExpr(visitor.getConfig().getTenantColumn());
        }

        Object tenantValue = WallProvider.getTenantValue();
        if (tenantValue instanceof Number) {
            right = new SQLNumberExpr((Number) tenantValue);
        } else if (tenantValue instanceof String) {
            right = new SQLCharExpr((String) tenantValue);
        } else {
            throw new IllegalStateException("tenant value not support type " + tenantValue);
        }

        SQLBinaryOpExpr tenantCondition = new SQLBinaryOpExpr(left, SQLBinaryOperator.Equality, right);
        return tenantCondition;
    }

    public static void checkReadOnly(WallVisitor visitor, SQLTableSource tableSource) {
        if (tableSource instanceof SQLExprTableSource) {
            String tableName = null;
            SQLExpr tableNameExpr = ((SQLExprTableSource) tableSource).getExpr();
            if (tableNameExpr instanceof SQLName) {
                tableName = ((SQLName) tableNameExpr).getSimleName();
            }

            boolean readOnlyValid = visitor.getProvider().checkReadOnlyTable(tableName);
            if (!readOnlyValid) {
                addViolation(visitor, tableSource);
            }
        } else if (tableSource instanceof SQLJoinTableSource) {
            SQLJoinTableSource join = (SQLJoinTableSource) tableSource;

            checkReadOnly(visitor, join.getLeft());
            checkReadOnly(visitor, join.getRight());
        }
    }

    public static void checkUpdate(WallVisitor visitor, SQLUpdateStatement x) {
        checkReadOnly(visitor, x.getTableSource());

        if (!visitor.getConfig().isUpdateAllow()) {
            addViolation(visitor, x);
            return;
        }

        if (!visitor.getConfig().isUpdateWhereAlayTrueCheck()) {
            return;
        }

        if (x.getWhere() == null || Boolean.TRUE == getConditionValue(visitor, x.getWhere())) {
            addViolation(visitor, x);
            return;
        }

        x.getWhere().setParent(x);
        checkCondition(visitor, x.getWhere());
        checkConditionForMultiTenant(visitor, x.getWhere(), x);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static Object getValue(SQLBinaryOpExpr x) {
        x.getLeft().setParent(x);
        x.getRight().setParent(x);

        if (x.getLeft() instanceof SQLName && x.getRight() instanceof SQLName) {
            if (x.getLeft().toString().equalsIgnoreCase(x.getRight().toString())) {
                if (x.getOperator() == SQLBinaryOperator.Equality) {
                    return Boolean.TRUE;
                } else if (x.getOperator() == SQLBinaryOperator.NotEqual) {
                    return Boolean.FALSE;
                }

                switch (x.getOperator()) {
                    case Equality:
                    case Like:
                        return Boolean.TRUE;
                    case NotEqual:
                    case GreaterThan:
                    case GreaterThanOrEqual:
                    case LessThan:
                    case LessThanOrEqual:
                    case LessThanOrGreater:
                    case NotLike:
                        return Boolean.FALSE;
                    default:
                        break;
                }
            }
        }

        if (x.getOperator() == SQLBinaryOperator.BooleanOr) {
            List<SQLExpr> groupList = new ArrayList<SQLExpr>();
            SQLExpr left = x.getLeft();
            for (;;) {
                if (left instanceof SQLBinaryOpExpr && ((SQLBinaryOpExpr) left).getOperator() == x.getOperator()) {
                    SQLBinaryOpExpr binaryLeft = (SQLBinaryOpExpr) left;
                    groupList.add(binaryLeft.getRight());
                    left = binaryLeft.getLeft();
                } else {
                    groupList.add(left);
                    break;
                }
            }
            groupList.add(x.getRight());

            boolean allFalse = true;
            for (int i = groupList.size() - 1; i >= 0; --i) {
                Object result = getValue(groupList.get(i));
                if (Boolean.TRUE == result) {
                    final WallConditionContext wallContext = WallVisitorUtils.getWallConditionContext();
                    if (wallContext != null) {
                        wallContext.setPartAllowTrue(true);
                    }
                    return true;
                }

                if (Boolean.FALSE != result) {
                    allFalse = false;
                }
            }

            if (allFalse) {
                return false;
            }

            return null;
        }

        Object leftResult = getValue(x.getLeft());
        Object rightResult = getValue(x.getRight());

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

        if (x.getOperator() == SQLBinaryOperator.Add) {
            if (leftResult == null || rightResult == null) {
                return null;
            }

            if (leftResult instanceof String || rightResult instanceof String) {
                return leftResult.toString() + rightResult.toString();
            }

            if (leftResult instanceof Number || rightResult instanceof Number) {
                return add((Number) leftResult, (Number) rightResult);
            }
        }

        return null;
    }

    private static Number add(Number a, Number b) {
        if (a instanceof BigDecimal) {
            return ((BigDecimal) a).add(new BigDecimal(b.toString()));
        }

        return a.longValue() + b.longValue();
    }

    public static class WallConditionContext {

        private boolean partAllowTrue;

        public boolean isPartAllowTrue() {
            return partAllowTrue;
        }

        public void setPartAllowTrue(boolean partAllowTrue) {
            this.partAllowTrue = partAllowTrue;
        }
    }

    private static ThreadLocal<WallConditionContext> wallConditionContextLocal = new ThreadLocal<WallConditionContext>();
    
    public static WallConditionContext getWallConditionContext() {
        return wallConditionContextLocal.get();
    }

    public static Object getConditionValue(WallVisitor visitor, SQLExpr x) {
        final WallConditionContext old = wallConditionContextLocal.get();
        try {
            wallConditionContextLocal.set(new WallConditionContext());
            final Object value = getValue(x);
            
            final WallConditionContext current = wallConditionContextLocal.get();
            if (current.isPartAllowTrue() && visitor.getConfig().isUpdateWhereAlayTrueCheck()) {
                addViolation(visitor, x);
            }
            
            return value;
        } finally {
            wallConditionContextLocal.set(old);
        }
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
            if (result instanceof Boolean) {
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

    public static void checkFunction(WallVisitor visitor, SQLMethodInvokeExpr x) {
        checkSchema(visitor, x.getOwner());

        if (!visitor.getConfig().isFunctionCheck()) {
            return;
        }

        String methodName = x.getMethodName();

        if (!visitor.getProvider().checkDenyFunction(methodName)) {
            addViolation(visitor, x);
        }

    }

    private static boolean checkSchema(WallVisitor visitor, SQLExpr x) {
        if (x instanceof SQLName) {
            String owner = ((SQLName) x).getSimleName();
            owner = WallVisitorUtils.form(owner);
            if (!visitor.getProvider().checkDenySchema(owner)) {
                addViolation(visitor, x);
                return false;
            }

            if (visitor.getConfig().isDenyObjects(owner)) {
                addViolation(visitor, x);
                return false;
            }
        }

        // if (ownerExpr instanceof SQLPropertyExpr) {
        if (x instanceof SQLPropertyExpr) {
            return checkSchema(visitor, ((SQLPropertyExpr) x).getOwner());
        }
        
        return true;
    }

    public static boolean check(WallVisitor visitor, SQLExprTableSource x) {
        SQLExpr expr = x.getExpr();

        if (expr instanceof SQLPropertyExpr) {
            boolean checkResult = checkSchema(visitor, ((SQLPropertyExpr) expr).getOwner());
            if (!checkResult) {
                return false;
            }
        }

        if (expr instanceof SQLName) {
            String tableName = ((SQLName) expr).getSimleName();
            if (visitor.isDenyTable(tableName)) {
                addViolation(visitor, x);
                return false;
            }
        }
        
        return true;
    }

    private static void addViolation(WallVisitor visitor, SQLObject x) {
        visitor.addViolation(new IllegalSQLObjectViolation(visitor.toSQL(x)));
    }

    public static void checkUnion(WallVisitor visitor, SQLUnionQuery x) {
        if (!visitor.getConfig().isSelectUnionCheck()) {
            return;
        }

        if (WallVisitorUtils.queryBlockFromIsNull(x.getLeft()) || WallVisitorUtils.queryBlockFromIsNull(x.getRight())) {
            addViolation(visitor, x);
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
            LOG.error("load oracle deny tables errror", e);
        }
    }
}
