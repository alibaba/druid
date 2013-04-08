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
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.SQLStatement;
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
import com.alibaba.druid.sql.ast.expr.SQLNumberExpr;
import com.alibaba.druid.sql.ast.expr.SQLNumericLiteralExpr;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.ast.expr.SQLQueryExpr;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLCallStatement;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLDeleteStatement;
import com.alibaba.druid.sql.ast.statement.SQLDropTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLInsertInto;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import com.alibaba.druid.sql.ast.statement.SQLJoinTableSource;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLSelectItem;
import com.alibaba.druid.sql.ast.statement.SQLSelectQuery;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.ast.statement.SQLSetStatement;
import com.alibaba.druid.sql.ast.statement.SQLSubqueryTableSource;
import com.alibaba.druid.sql.ast.statement.SQLTableSource;
import com.alibaba.druid.sql.ast.statement.SQLTruncateStatement;
import com.alibaba.druid.sql.ast.statement.SQLUnionOperator;
import com.alibaba.druid.sql.ast.statement.SQLUnionQuery;
import com.alibaba.druid.sql.ast.statement.SQLUpdateStatement;
import com.alibaba.druid.sql.ast.statement.SQLUseStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.expr.MySqlBooleanExpr;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCommitStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlDescribeStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlReplaceStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSetCharSetStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSetNamesStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlUpdateStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleMergeStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleMultiInsertStatement;
import com.alibaba.druid.sql.visitor.ExportParameterVisitor;
import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;
import com.alibaba.druid.util.JdbcUtils;
import com.alibaba.druid.util.ServletPathMatcher;
import com.alibaba.druid.wall.WallConfig;
import com.alibaba.druid.wall.WallContext;
import com.alibaba.druid.wall.WallProvider;
import com.alibaba.druid.wall.WallSqlTableStat;
import com.alibaba.druid.wall.WallVisitor;
import com.alibaba.druid.wall.violation.IllegalSQLObjectViolation;

public class WallVisitorUtils {

    private final static Log LOG = LogFactory.getLog(WallVisitorUtils.class);

    public static void check(WallVisitor visitor, SQLInListExpr x) {

    }

    public static void check(WallVisitor visitor, SQLBinaryOpExpr x) {

    }

    public static void check(WallVisitor visitor, SQLCreateTableStatement x) {
        String tableName = ((SQLName) x.getName()).getSimleName();
        WallContext context = WallContext.current();
        if (context != null) {
            WallSqlTableStat tableStat = context.getTableStat(tableName);
            if (tableStat != null) {
                tableStat.incrementCreateCount();
            }
        }
    }

    public static void check(WallVisitor visitor, SQLAlterTableStatement x) {
        String tableName = ((SQLName) x.getName()).getSimleName();
        WallContext context = WallContext.current();
        if (context != null) {
            WallSqlTableStat tableStat = context.getTableStat(tableName);
            if (tableStat != null) {
                tableStat.incrementAlterCount();
            }
        }
    }

    public static void check(WallVisitor visitor, SQLDropTableStatement x) {
        for (SQLTableSource item : x.getTableSources()) {
            if (item instanceof SQLExprTableSource) {
                SQLExpr expr = ((SQLExprTableSource) item).getExpr();
                String tableName = ((SQLName) expr).getSimleName();
                WallContext context = WallContext.current();
                if (context != null) {
                    WallSqlTableStat tableStat = context.getTableStat(tableName);
                    if (tableStat != null) {
                        tableStat.incrementDropCount();
                    }
                }
            }
        }
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
                addViolation(visitor, "'SELECT *' not allow", x);
            }
        }
    }

    public static void check(WallVisitor visitor, SQLPropertyExpr x) {
        checkSchema(visitor, x.getOwner());
    }

    public static void checkInsert(WallVisitor visitor, SQLInsertInto x) {
        checkReadOnly(visitor, x.getTableSource());

        if (!visitor.getConfig().isInsertAllow()) {
            addViolation(visitor, "insert not allow", x);
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
            addViolation(visitor, "select into not allow", x);
            return;
        }

        if (x.getFrom() != null) {
            x.getFrom().setParent(x);
        }

        SQLExpr where = x.getWhere();

        if (where != null) {
            x.getWhere().setParent(x);
            checkCondition(visitor, x.getWhere());

            if (Boolean.TRUE == getConditionValue(visitor, where, visitor.getConfig().isSelectWhereAlwayTrueCheck())) {
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
                    addViolation(visitor, "select alway true condition not allow", x);
                }
            }

        }
        checkConditionForMultiTenant(visitor, x.getWhere(), x);
    }

    public static void checkHaving(WallVisitor visitor, SQLExpr x) {
        if (x == null) {
            return;
        }

        if (Boolean.TRUE == getConditionValue(visitor, x, visitor.getConfig().isSelectHavingAlwayTrueCheck())) {
            addViolation(visitor, "having alway true condition not allow", x);
        }
    }

    public static void checkDelete(WallVisitor visitor, SQLDeleteStatement x) {
        checkReadOnly(visitor, x.getTableSource());

        WallConfig config = visitor.getConfig();
        if (!config.isDeleteAllow()) {
            addViolation(visitor, "delete not allow", x);
            return;
        }

        if (x.getWhere() == null && config.isDeleteWhereNoneCheck()) {
            addViolation(visitor, "delete none condition not allow", x);
            return;
        }

        if (Boolean.TRUE == getConditionValue(visitor, x.getWhere(), config.isDeleteWhereAlwayTrueCheck())) {
            addViolation(visitor, "delete alway true condition not allow", x);
            return;
        }

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
                addViolation(visitor, "sql must parameterized", x);
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
                addViolation(visitor, "table readonly : " + tableName, tableSource);
            }
        } else if (tableSource instanceof SQLJoinTableSource) {
            SQLJoinTableSource join = (SQLJoinTableSource) tableSource;

            checkReadOnly(visitor, join.getLeft());
            checkReadOnly(visitor, join.getRight());
        }
    }

    public static void checkUpdate(WallVisitor visitor, SQLUpdateStatement x) {
        checkReadOnly(visitor, x.getTableSource());

        WallConfig config = visitor.getConfig();
        if (!config.isUpdateAllow()) {
            addViolation(visitor, "update not allow", x);
            return;
        }

        if (x.getWhere() == null && config.isUpdateWhereNoneCheck()) {
            if (x instanceof MySqlUpdateStatement) {
                MySqlUpdateStatement mysqlUpdate = (MySqlUpdateStatement) x;
                if (mysqlUpdate.getLimit() == null) {
                    addViolation(visitor, "update none condition not allow", x);
                    return;
                }
            } else {
                addViolation(visitor, "update none condition not allow", x);
                return;
            }
        }

        if (config.isUpdateWhereAlayTrueCheck()) {
            if (Boolean.TRUE == getConditionValue(visitor, x.getWhere(), true)) {
                addViolation(visitor, "update alway true condition not allow", x);
                return;
            }
        }

        if (x.getWhere() != null) {
            x.getWhere().setParent(x);
        }
        checkCondition(visitor, x.getWhere());
        checkConditionForMultiTenant(visitor, x.getWhere(), x);
    }

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
                        wallContext.setPartAlwayTrue(true);
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

            if (leftResult == Boolean.TRUE) {
                if (!isFirst(x.getLeft())) {
                    final WallConditionContext current = wallConditionContextLocal.get();
                    if (current != null) {
                        current.setPartAlwayTrue(true);
                    }
                }
            } else if (rightResult == Boolean.TRUE) {
                final WallConditionContext current = wallConditionContextLocal.get();
                if (current != null) {
                    current.setPartAlwayTrue(true);
                }
            }

            if (Boolean.TRUE == leftResult && Boolean.TRUE == rightResult) {
                return true;
            }
        }

        String dbType = null;
        WallContext wallContext = WallContext.current();
        if (wallContext != null) {
            dbType = wallContext.getDbType();
        }

        return SQLEvalVisitorUtils.eval(dbType, x, Collections.emptyList(), false);
    }

    public static boolean isFirst(SQLObject x) {
        if (x == null) {
            return true;
        }

        SQLObject parent = x.getParent();
        if (!(parent instanceof SQLExpr)) {
            return true;
        }

        if (parent instanceof SQLBinaryOpExpr) {
            SQLBinaryOpExpr binaryExpr = (SQLBinaryOpExpr) parent;
            if (isFirst(binaryExpr) && x == binaryExpr.getLeft()) {
                return true;
            }
        }

        return false;
    }

    public static class WallConditionContext {

        private boolean partAlwayrue;
        private boolean constArithmetic = false;
        private boolean xor             = false;
        private boolean bitwise         = false;

        public boolean hasPartAlwayTrue() {
            return partAlwayrue;
        }

        public void setPartAlwayTrue(boolean partAllowTrue) {
            this.partAlwayrue = partAllowTrue;
        }

        public boolean hasConstArithmetic() {
            return constArithmetic;
        }

        public void setConstArithmetic(boolean constArithmetic) {
            this.constArithmetic = constArithmetic;
        }

        public boolean hasXor() {
            return xor;
        }

        public void setXor(boolean xor) {
            this.xor = xor;
        }

        public boolean hasBitwise() {
            return bitwise;
        }

        public void setBitwise(boolean bitwise) {
            this.bitwise = bitwise;
        }
    }

    private static ThreadLocal<WallConditionContext> wallConditionContextLocal = new ThreadLocal<WallConditionContext>();

    public static WallConditionContext getWallConditionContext() {
        return wallConditionContextLocal.get();
    }

    public static Object getConditionValue(WallVisitor visitor, SQLExpr x, boolean alwayTrueCheck) {
        final WallConditionContext old = wallConditionContextLocal.get();
        try {
            wallConditionContextLocal.set(new WallConditionContext());
            final Object value = getValue(x);

            final WallConditionContext current = wallConditionContextLocal.get();
            if (current.hasPartAlwayTrue() && alwayTrueCheck) {
                addViolation(visitor, "part alway true condition not allow", x);
            }

            if (current.hasConstArithmetic() && !visitor.getConfig().isConstArithmeticAllow()) {
                addViolation(visitor, "const arithmetic not allow", x);
            }

            if (current.hasXor() && !visitor.getConfig().isConditionOpXorAllow()) {
                addViolation(visitor, "xor not allow", x);
            }

            if (current.hasBitwise() && !visitor.getConfig().isConditionOpBitwseAllow()) {
                addViolation(visitor, "bitwise operator not allow", x);
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
        
        WallContext context = WallContext.current();
        if (context != null) {
            context.incrementFunctionInvoke(methodName);
        }

        if (!visitor.getProvider().checkDenyFunction(methodName)) {
            addViolation(visitor, "deny function : " + methodName, x);
        }

    }

    private static boolean checkSchema(WallVisitor visitor, SQLExpr x) {
        if (x instanceof SQLName) {
            String owner = ((SQLName) x).getSimleName();
            owner = WallVisitorUtils.form(owner);
            if (!visitor.getProvider().checkDenySchema(owner)) {
                addViolation(visitor, "deny schema : " + owner, x);
                return false;
            }

            if (visitor.getConfig().isDenyObjects(owner)) {
                addViolation(visitor, "deny object : " + owner, x);
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

            WallContext context = WallContext.current();
            if (context != null) {
                WallSqlTableStat tableStat = context.getTableStat(tableName);
                if (tableStat != null) {
                    SQLObject parent = x.getParent();

                    while (parent instanceof SQLTableSource) {
                        parent = parent.getParent();
                    }

                    if (parent instanceof SQLSelectQueryBlock) {
                        SQLSelectQueryBlock queryBlock = (SQLSelectQueryBlock) parent;
                        if (x == queryBlock.getInto()) {
                            tableStat.incrementSelectIntoCount();
                        } else {
                            tableStat.incrementSelectCount();
                        }
                    } else if (parent instanceof SQLTruncateStatement) {
                        tableStat.incrementTruncateCount();
                    } else if (parent instanceof SQLInsertStatement) {
                        tableStat.incrementInsertCount();
                    } else if (parent instanceof SQLDeleteStatement) {
                        tableStat.incrementDeleteCount();
                    } else if (parent instanceof SQLUpdateStatement) {
                        tableStat.incrementUpdateCount();
                    } else if (parent instanceof MySqlReplaceStatement) {
                        tableStat.incrementReplaceCount();
                    }
                }
            }

            if (visitor.isDenyTable(tableName)) {
                addViolation(visitor, "deny table : " + tableName, x);
                return false;
            }
        }

        return true;
    }

    private static void addViolation(WallVisitor visitor, String message, SQLObject x) {
        visitor.addViolation(new IllegalSQLObjectViolation(message, visitor.toSQL(x)));
    }

    public static void checkUnion(WallVisitor visitor, SQLUnionQuery x) {
        if (x.getOperator() == SQLUnionOperator.MINUS && !visitor.getConfig().isMinusAllow()) {
            addViolation(visitor, "minus not allow", x);
            return;
        }

        if (x.getOperator() == SQLUnionOperator.INTERSECT && !visitor.getConfig().isIntersectAllow()) {
            addViolation(visitor, "intersect not allow", x);
            return;
        }

        if (!visitor.getConfig().isSelectUnionCheck()) {
            return;
        }

        if (WallVisitorUtils.queryBlockFromIsNull(x.getLeft()) || WallVisitorUtils.queryBlockFromIsNull(x.getRight())) {
            addViolation(visitor, "union query not contains 'from clause'", x);
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

    public static void preVisitCheck(WallVisitor visitor, SQLObject x) {
        WallConfig config = visitor.getProvider().getConfig();

        if (!(x instanceof SQLStatement)) {
            return;
        }

        boolean allow = false;
        String denyMessage = null;
        if (x instanceof SQLInsertStatement) {
            allow = true;
            denyMessage = "insert not allow";
        } else if (x instanceof SQLSelectStatement) {
            allow = true;
            denyMessage = "select not allow";
        } else if (x instanceof SQLDeleteStatement) {
            allow = true;
            denyMessage = "delete not allow";
        } else if (x instanceof SQLUpdateStatement) {
            allow = true;
            denyMessage = "update not allow";
        } else if (x instanceof OracleMultiInsertStatement) {
            allow = true;
            denyMessage = "multi-insert not allow";
        } else if (x instanceof OracleMergeStatement) {
            allow = true;
            denyMessage = "merge not allow";
        } else if (x instanceof SQLCallStatement) {
            allow = true;
            denyMessage = "call not allow";
        } else if (x instanceof SQLTruncateStatement) {
            allow = config.isTruncateAllow();
            denyMessage = "truncate not allow";
        } else if (x instanceof SQLCreateTableStatement) {
            allow = config.isCreateTableAllow();
            denyMessage = "create table not allow";
        } else if (x instanceof SQLAlterTableStatement) {
            allow = config.isAlterTableAllow();
            denyMessage = "alter table not allow";
        } else if (x instanceof SQLDropTableStatement) {
            allow = config.isDropTableAllow();
            denyMessage = "drop table not allow";
        } else if (x instanceof MySqlSetCharSetStatement //
                   || x instanceof MySqlSetNamesStatement //
                   || x instanceof SQLSetStatement) {
            allow = config.isSetAllow();
            denyMessage = "set not allow";
        } else if (x instanceof MySqlReplaceStatement) {
            allow = config.isReplaceAllow();
            denyMessage = "replace not allow";
        } else if (x instanceof MySqlDescribeStatement) {
            allow = config.isDescribeAllow();
            denyMessage = "describe not allow";
        } else if (x instanceof MySqlShowStatement) {
            allow = config.isShowAllow();
            denyMessage = "show not allow";
        } else if (x instanceof MySqlCommitStatement) {
            allow = config.isCommitAllow();
            denyMessage = "show not allow";
        } else if (x instanceof SQLUseStatement) {
            allow = config.isUseAllow();
            denyMessage = "show not allow";
        } else {
            allow = config.isNoneBaseStatementAllow();
        }

        if (!allow) {
            if (denyMessage == null) {
                denyMessage = x.getClass() + " not allow";
            }
            addViolation(visitor, denyMessage, x);
        }
    }
}
