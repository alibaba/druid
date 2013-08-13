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
import com.alibaba.druid.sql.ast.expr.SQLCaseExpr.Item;
import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLInListExpr;
import com.alibaba.druid.sql.ast.expr.SQLInSubQueryExpr;
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
import com.alibaba.druid.sql.ast.statement.SQLCreateIndexStatement;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLCreateTriggerStatement;
import com.alibaba.druid.sql.ast.statement.SQLCreateViewStatement;
import com.alibaba.druid.sql.ast.statement.SQLDeleteStatement;
import com.alibaba.druid.sql.ast.statement.SQLDropIndexStatement;
import com.alibaba.druid.sql.ast.statement.SQLDropSequenceStatement;
import com.alibaba.druid.sql.ast.statement.SQLDropTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLDropTriggerStatement;
import com.alibaba.druid.sql.ast.statement.SQLDropViewStatement;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLInsertInto;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import com.alibaba.druid.sql.ast.statement.SQLJoinTableSource;
import com.alibaba.druid.sql.ast.statement.SQLRollbackStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLSelectGroupByClause;
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
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlDeleteStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlDescribeStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlReplaceStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSetCharSetStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSetNamesStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowGrantsStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlUpdateStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleCreateSequenceStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleMergeStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleMultiInsertStatement;
import com.alibaba.druid.sql.dialect.sqlserver.ast.stmt.SQLServerExecStatement;
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
import com.alibaba.druid.wall.violation.ErrorCode;
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
                addViolation(visitor, ErrorCode.SELECT_NOT_ALLOW, "'SELECT *' not allow", x);
            }
        }
    }

    public static void check(WallVisitor visitor, SQLPropertyExpr x) {
        checkSchema(visitor, x.getOwner());
    }

    public static void checkInsert(WallVisitor visitor, SQLInsertInto x) {
        checkReadOnly(visitor, x.getTableSource());

        if (!visitor.getConfig().isInsertAllow()) {
            addViolation(visitor, ErrorCode.INSERT_NOT_ALLOW, "insert not allow", x);
        }
    }

    public static void checkSelelct(WallVisitor visitor, SQLSelectQueryBlock x) {

        final WallSelectQueryContext old = wallSelectQueryContextLocal.get();
        try {
            wallSelectQueryContextLocal.set(new WallSelectQueryContext());

            for (SQLSelectItem item : x.getSelectList()) {
                item.setParent(x);
            }

            if (x.getInto() != null) {
                checkReadOnly(visitor, x.getInto());
            }

            if (!visitor.getConfig().isSelectIntoAllow() && x.getInto() != null) {
                addViolation(visitor, ErrorCode.SELECT_INTO_NOT_ALLOW, "select into not allow", x);
                return;
            }

            if (x.getFrom() != null) {
                x.getFrom().setParent(x);
            }

            SQLExpr where = x.getWhere();
            if (where != null) {
                where.setParent(x);
                checkCondition(visitor, x.getWhere());

                if (Boolean.TRUE == getConditionValue(visitor, where, visitor.getConfig().isSelectWhereAlwayTrueCheck())) {
                    boolean isSimpleConstExpr = false;
                    SQLExpr first = getFirst(where);

                    if (first == where) {
                        isSimpleConstExpr = true;
                    } else if (first instanceof SQLBinaryOpExpr) {
                        SQLBinaryOpExpr binaryOpExpr = (SQLBinaryOpExpr) first;

                        if (binaryOpExpr.getOperator() == SQLBinaryOperator.Equality
                            || binaryOpExpr.getOperator() == SQLBinaryOperator.NotEqual) {
                            if (binaryOpExpr.getLeft() instanceof SQLIntegerExpr
                                && binaryOpExpr.getRight() instanceof SQLIntegerExpr) {
                                isSimpleConstExpr = true;
                            }
                        }
                    }

                    final WallSelectQueryContext current = wallSelectQueryContextLocal.get();
                    if (!isSimpleConstExpr // 简单表达式
                        && !(current != null && current.hasTrueLike()) // eg: like '%%'
                    ) {
                        addViolation(visitor, ErrorCode.ALWAY_TRUE, "select alway true condition not allow", x);
                    }
                }

            }
            checkConditionForMultiTenant(visitor, x.getWhere(), x);
        } finally {
            wallSelectQueryContextLocal.set(old);
        }
    }

    public static void checkHaving(WallVisitor visitor, SQLExpr x) {
        if (x == null) {
            return;
        }
        final WallSelectQueryContext old = wallSelectQueryContextLocal.get();
        try {
            wallSelectQueryContextLocal.set(new WallSelectQueryContext());

            if (Boolean.TRUE == getConditionValue(visitor, x, visitor.getConfig().isSelectHavingAlwayTrueCheck())) {
                boolean isSimpleConstExpr = false;
                if (x instanceof SQLBinaryOpExpr) {
                    SQLBinaryOpExpr binaryOpExpr = (SQLBinaryOpExpr) x;
                    if (binaryOpExpr.getOperator() == SQLBinaryOperator.Equality
                        || binaryOpExpr.getOperator() == SQLBinaryOperator.NotEqual) {
                        if (binaryOpExpr.getLeft() instanceof SQLIntegerExpr
                            && binaryOpExpr.getRight() instanceof SQLIntegerExpr) {
                            isSimpleConstExpr = true;
                        }
                    }
                }

                final WallSelectQueryContext current = wallSelectQueryContextLocal.get();
                if (!isSimpleConstExpr && !(current != null && current.hasTrueLike())) {
                    addViolation(visitor, ErrorCode.ALWAY_TRUE, "having alway true condition not allow", x);
                }
            }
        } finally {
            wallSelectQueryContextLocal.set(old);
        }
    }

    public static void checkDelete(WallVisitor visitor, SQLDeleteStatement x) {
        checkReadOnly(visitor, x.getTableSource());

        WallConfig config = visitor.getConfig();
        if (!config.isDeleteAllow()) {
            addViolation(visitor, ErrorCode.INSERT_NOT_ALLOW, "delete not allow", x);
            return;
        }

        boolean hasUsing = false;

        if (x instanceof MySqlDeleteStatement) {
            hasUsing = ((MySqlDeleteStatement) x).getUsing() != null;
        }

        boolean isJoinTableSource = x.getTableSource() instanceof SQLJoinTableSource;
        if (x.getWhere() == null && (!hasUsing) && !isJoinTableSource) {
            WallContext context = WallContext.current();
            if (context != null) {
                context.incrementDeleteNoneConditionWarnnings();
            }

            if (config.isDeleteWhereNoneCheck()) {
                addViolation(visitor, ErrorCode.NONE_CONDITION, "delete none condition not allow", x);
                return;
            }
        }

        SQLExpr where = x.getWhere();
        if (where != null) {
            checkCondition(visitor, where);

            if (Boolean.TRUE == getConditionValue(visitor, where, config.isDeleteWhereAlwayTrueCheck())) {
                boolean isSimpleConstExpr = false;
                SQLExpr first = getFirst(where);

                if (first == where) {
                    isSimpleConstExpr = true;
                } else if (first instanceof SQLBinaryOpExpr) {
                    SQLBinaryOpExpr binaryOpExpr = (SQLBinaryOpExpr) first;

                    if (binaryOpExpr.getOperator() == SQLBinaryOperator.Equality
                        || binaryOpExpr.getOperator() == SQLBinaryOperator.NotEqual) {
                        if (binaryOpExpr.getLeft() instanceof SQLIntegerExpr
                            && binaryOpExpr.getRight() instanceof SQLIntegerExpr) {
                            isSimpleConstExpr = true;
                        }
                    }
                }

                if (!isSimpleConstExpr) {
                    addViolation(visitor, ErrorCode.ALWAY_TRUE, "delete alway true condition not allow", x);
                }
            }
        }

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
                addViolation(visitor, ErrorCode.NOT_PARAMETERIZED, "sql must parameterized", x);
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
            SQLDeleteStatement deleteStmt = (SQLDeleteStatement) parent;
            deleteStmt.setWhere(condition);
            visitor.setSqlModified(true);
        } else if (parent instanceof SQLUpdateStatement) {
            SQLUpdateStatement updateStmt = (SQLUpdateStatement) parent;
            updateStmt.setWhere(condition);
            visitor.setSqlModified(true);
        } else if (parent instanceof SQLSelectQueryBlock) {
            SQLSelectQueryBlock queryBlock = (SQLSelectQueryBlock) parent;
            queryBlock.setWhere(condition);
            visitor.setSqlModified(true);
        }
    }

    public static void checkJoinConditionForMultiTenant(WallVisitor visitor, SQLJoinTableSource join, boolean checkLeft) {
        String tenantTablePattern = visitor.getConfig().getTenantTablePattern();
        if (tenantTablePattern == null || tenantTablePattern.length() == 0) {
            return;
        }

        SQLExpr condition = join.getCondition();

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
            visitor.setSqlModified(true);
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
                addViolation(visitor, ErrorCode.READ_ONLY, "table readonly : " + tableName, tableSource);
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
            addViolation(visitor, ErrorCode.UPDATE_NOT_ALLOW, "update not allow", x);
            return;
        }

        SQLExpr where = x.getWhere();
        if (where == null) {
            WallContext context = WallContext.current();
            if (context != null) {
                context.incrementUpdateNoneConditionWarnnings();
            }

            if (config.isUpdateWhereNoneCheck()) {
                if (x instanceof MySqlUpdateStatement) {
                    MySqlUpdateStatement mysqlUpdate = (MySqlUpdateStatement) x;
                    if (mysqlUpdate.getLimit() == null) {
                        addViolation(visitor, ErrorCode.NONE_CONDITION, "update none condition not allow", x);
                        return;
                    }
                } else {
                    addViolation(visitor, ErrorCode.NONE_CONDITION, "update none condition not allow", x);
                    return;
                }
            }
        } else {
            where.setParent(x);
            checkCondition(visitor, where);

            if (Boolean.TRUE == getConditionValue(visitor, where, config.isUpdateWhereAlayTrueCheck())) {
                boolean isSimpleConstExpr = false;
                SQLExpr first = getFirst(where);

                if (first == where) {
                    isSimpleConstExpr = true;
                } else if (first instanceof SQLBinaryOpExpr) {
                    SQLBinaryOpExpr binaryOpExpr = (SQLBinaryOpExpr) first;

                    if (binaryOpExpr.getOperator() == SQLBinaryOperator.Equality
                        || binaryOpExpr.getOperator() == SQLBinaryOperator.NotEqual) {
                        if (binaryOpExpr.getLeft() instanceof SQLIntegerExpr
                            && binaryOpExpr.getRight() instanceof SQLIntegerExpr) {
                            isSimpleConstExpr = true;
                        }
                    }
                }

                if (!isSimpleConstExpr) {
                    addViolation(visitor, ErrorCode.ALWAY_TRUE, "update alway true condition not allow", x);
                }
            }
        }

        checkConditionForMultiTenant(visitor, where, x);
    }

    public static Object getValue(WallVisitor visitor, SQLBinaryOpExpr x) {
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
                Object result = getValue(visitor, groupList.get(i));
                if (Boolean.TRUE == result) {
                    final WallConditionContext wallContext = WallVisitorUtils.getWallConditionContext();
                    if (wallContext != null && i != 0) {
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

        SQLExpr left = x.getLeft();
        SQLExpr right = x.getRight();
        Object leftResult = getValue(visitor, left);
        Object rightResult = getValue(visitor, right);

        if (x.getOperator() == SQLBinaryOperator.Like || x.getOperator() == SQLBinaryOperator.NotLike) {
            WallContext context = WallContext.current();
            if (context != null) {
                if (rightResult instanceof Number || leftResult instanceof Number) {
                    context.incrementLikeNumberWarnnings();
                }
            }
        }

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

                boolean isLikeAlwayTrue = false;
                if (right instanceof SQLBinaryOpExpr
                    && ((SQLBinaryOpExpr) right).getOperator() == SQLBinaryOperator.Like) {
                    isLikeAlwayTrue = true;
                }

                if (current != null && !isLikeAlwayTrue) {
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

    public static SQLExpr getFirst(SQLExpr x) {
        if (x instanceof SQLBinaryOpExpr) {
            SQLBinaryOpExpr binary = (SQLBinaryOpExpr) x;
            if (binary.getOperator() == SQLBinaryOperator.BooleanAnd
                || binary.getOperator() == SQLBinaryOperator.BooleanOr) {
                return getFirst(((SQLBinaryOpExpr) x).getLeft());
            }
        }

        return x;
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

    private static boolean hasWhere(SQLSelectQuery selectQuery) {

        if (selectQuery instanceof SQLSelectQueryBlock) {
            return ((SQLSelectQueryBlock) selectQuery).getWhere() != null;
        } else if (selectQuery instanceof SQLUnionQuery) {
            SQLUnionQuery union = (SQLUnionQuery) selectQuery;
            return hasWhere(union.getLeft()) || hasWhere(union.getRight());
        }
        return false;
    }

    public static boolean isWhereOrHaving(SQLObject x) {
        if (x == null) {
            return false;
        }

        for (;;) {
            SQLObject parent = x.getParent();

            if (parent == null) {
                return false;
            }

            if (parent instanceof SQLUnionQuery) {
                SQLUnionQuery union = (SQLUnionQuery) parent;
                if (union.getRight() == x && hasWhere(union.getLeft())) {
                    return true;
                }
            }

            if (parent instanceof SQLSelectQueryBlock) {
                SQLSelectQueryBlock query = (SQLSelectQueryBlock) parent;
                if (query.getWhere() == x) {
                    return true;
                }
            }

            if (parent instanceof SQLDeleteStatement) {
                SQLDeleteStatement delete = (SQLDeleteStatement) parent;
                if (delete.getWhere() == x) {
                    return true;
                } else {
                    return false;
                }
            }

            if (parent instanceof SQLUpdateStatement) {
                SQLUpdateStatement update = (SQLUpdateStatement) parent;
                if (update.getWhere() == x) {
                    return true;
                } else {
                    return false;
                }
            }

            if (parent instanceof SQLSelectGroupByClause) {
                SQLSelectGroupByClause groupBy = (SQLSelectGroupByClause) parent;
                if (x == groupBy.getHaving()) {
                    return true;
                } else {
                    return false;
                }
            }

            x = parent;
        }
    }

    public static class WallSelectQueryContext {

        private boolean trueLike = false;

        public boolean hasTrueLike() {
            return trueLike;
        }

        public void setTrueLike(boolean trueLike) {
            this.trueLike = trueLike;
        }
    }

    public static class WallTopStatementContext {

        private boolean fromSysTable    = false;
        private boolean fromSysSchema   = false;

        private boolean fromPermitTable = false;

        public boolean fromSysTable() {
            return fromSysTable;
        }

        public void setFromSysTable(boolean fromSysTable) {
            this.fromSysTable = fromSysTable;
        }

        public boolean fromSysSchema() {
            return fromSysSchema;
        }

        public void setFromSysSchema(boolean fromSysSchema) {
            this.fromSysSchema = fromSysSchema;
        }

        public boolean fromPermitTable() {
            return fromPermitTable;
        }

        public void setFromPermitTable(boolean fromPermitTable) {
            this.fromPermitTable = fromPermitTable;
        }
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

    private static ThreadLocal<WallConditionContext>    wallConditionContextLocal    = new ThreadLocal<WallConditionContext>();
    private static ThreadLocal<WallSelectQueryContext>  wallSelectQueryContextLocal  = new ThreadLocal<WallSelectQueryContext>();
    private static ThreadLocal<WallTopStatementContext> wallTopStatementContextLocal = new ThreadLocal<WallTopStatementContext>();

    public static WallConditionContext getWallConditionContext() {
        return wallConditionContextLocal.get();
    }

    public static WallTopStatementContext getWallTopStatementContext() {
        return wallTopStatementContextLocal.get();
    }

    public static void clearWallTopStatementContext() {
        wallTopStatementContextLocal.set(null);
    }

    public static void initWallTopStatementContext() {
        wallTopStatementContextLocal.set(new WallTopStatementContext());
    }

    public static WallSelectQueryContext getWallSelectQueryContext() {
        return wallSelectQueryContextLocal.get();
    }

    public static Object getConditionValue(WallVisitor visitor, SQLExpr x, boolean alwayTrueCheck) {
        final WallConditionContext old = wallConditionContextLocal.get();
        try {
            wallConditionContextLocal.set(new WallConditionContext());
            final Object value = getValue(visitor, x);

            final WallConditionContext current = wallConditionContextLocal.get();
            WallContext context = WallContext.current();
            if (context != null) {
                if (current.hasPartAlwayTrue() || Boolean.TRUE == value) {
                    if (!isFirst(x)) {
                        context.incrementWarnnings();
                    }
                }
            }

            if (current.hasPartAlwayTrue() && alwayTrueCheck && !visitor.getConfig().isConditionAndAlwayTrueAllow()) {
                addViolation(visitor, ErrorCode.ALWAY_TRUE, "part alway true condition not allow", x);
            }

            if (current.hasConstArithmetic() && !visitor.getConfig().isConstArithmeticAllow()) {
                addViolation(visitor, ErrorCode.CONST_ARITHMETIC, "const arithmetic not allow", x);
            }

            if (current.hasXor() && !visitor.getConfig().isConditionOpXorAllow()) {
                addViolation(visitor, ErrorCode.XOR, "xor not allow", x);
            }

            if (current.hasBitwise() && !visitor.getConfig().isConditionOpBitwseAllow()) {
                addViolation(visitor, ErrorCode.BITWISE, "bitwise operator not allow", x);
            }

            return value;
        } finally {
            wallConditionContextLocal.set(old);
        }
    }

    public static Object getValue(SQLExpr x) {
        return getValue(null, x);
    }

    public static Object getValue(WallVisitor visitor, SQLExpr x) {
        if (x instanceof SQLBinaryOpExpr) {
            return getValue(visitor, (SQLBinaryOpExpr) x);
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
            Object result = getValue(visitor, ((SQLNotExpr) x).getExpr());
            if (result instanceof Boolean) {
                return !((Boolean) result).booleanValue();
            }
        }

        if (x instanceof SQLQueryExpr) {
            if (isSimpleCountTableSource(visitor, ((SQLQueryExpr) x).getSubQuery())) {
                return Integer.valueOf(1);
            }
        }

        if (x instanceof SQLMethodInvokeExpr) {
            return getValue(visitor, (SQLMethodInvokeExpr) x);
        }

        return null;
    }

    public static Object getValue(WallVisitor visitor, SQLMethodInvokeExpr x) {
        String methodName = x.getMethodName();
        if ("len".equalsIgnoreCase(methodName) || "length".equalsIgnoreCase(methodName)) {
            Object firstValue = null;
            if (x.getParameters().size() > 0) {
                firstValue = (getValue(visitor, x.getParameters().get(0)));
            }

            if (firstValue instanceof String) {
                return ((String) firstValue).length();
            }
        }

        if ("if".equalsIgnoreCase(methodName) && x.getParameters().size() == 3) {
            SQLExpr first = x.getParameters().get(0);
            Object firstResult = getValue(visitor, first);

            if (Boolean.TRUE == firstResult) {
                return getValue(visitor, x.getParameters().get(1));
            }

            if (Boolean.FALSE == firstResult) {
                getValue(visitor, x.getParameters().get(2));
            }
        }

        if ("chr".equalsIgnoreCase(methodName) && x.getParameters().size() == 1) {
            SQLExpr first = x.getParameters().get(0);
            Object firstResult = getValue(visitor, first);
            if (firstResult instanceof Number) {
                int intValue = ((Number) firstResult).intValue();
                char ch = (char) intValue;

                return "" + ch;
            }
        }

        if ("concat".equalsIgnoreCase(methodName)) {
            StringBuffer buf = new StringBuffer();
            for (SQLExpr expr : x.getParameters()) {
                Object value = getValue(visitor, expr);
                if (value == null) {
                    return null;
                }

                buf.append(value.toString());
            }
            return buf.toString();
        }

        return null;
    }

    public static boolean isSimpleCountTableSource(WallVisitor visitor, SQLTableSource tableSource) {
        if (!(tableSource instanceof SQLSubqueryTableSource)) {
            return false;
        }

        SQLSubqueryTableSource subQuery = (SQLSubqueryTableSource) tableSource;

        return isSimpleCountTableSource(visitor, subQuery.getSelect());
    }

    public static boolean isSimpleCountTableSource(WallVisitor visitor, SQLSelect select) {
        SQLSelectQuery query = select.getQuery();

        if (query instanceof SQLSelectQueryBlock) {
            SQLSelectQueryBlock queryBlock = (SQLSelectQueryBlock) query;

            boolean allawTrueWhere = false;

            if (queryBlock.getWhere() == null) {
                allawTrueWhere = true;
            } else {
                Object whereValue = getValue(visitor, queryBlock.getWhere());
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

    public static void checkFunctionInTableSource(WallVisitor visitor, SQLMethodInvokeExpr x) {
        final WallTopStatementContext topStatementContext = wallTopStatementContextLocal.get();
        if (topStatementContext != null && (topStatementContext.fromSysSchema || topStatementContext.fromSysTable)) {
            return;
        }

        checkSchema(visitor, x.getOwner());

        String methodName = x.getMethodName();
        if (!visitor.getProvider().checkDenyTable(methodName)) {
            if (isTopUpdateStatement(x) || isFirstSelectTableSource(x)) {
                if (topStatementContext != null) {
                    topStatementContext.setFromSysSchema(Boolean.TRUE);
                    clearViolation(visitor);
                }
            }
        }
    }

    public static void checkFunction(WallVisitor visitor, SQLMethodInvokeExpr x) {

        final WallTopStatementContext topStatementContext = wallTopStatementContextLocal.get();
        if (topStatementContext != null && (topStatementContext.fromSysSchema || topStatementContext.fromSysTable)) {
            return;
        }

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
            boolean isTopNoneFrom = isTopNoneFromSelect(visitor, x);
            if (isTopNoneFrom) {
                return;
            }

            boolean isShow = x.getParent() instanceof MySqlShowGrantsStatement;
            if (isShow) {
                return;
            }

            if (isWhereOrHaving(x)) {
                addViolation(visitor, ErrorCode.FUNCTION_DENY, "deny function : " + methodName, x);
            }
        }
    }

    public static SQLSelectQueryBlock getQueryBlock(SQLObject x) {
        if (x == null) {
            return null;
        }

        if (x instanceof SQLSelectQueryBlock) {
            return (SQLSelectQueryBlock) x;
        }

        SQLObject parent = x.getParent();

        if (parent instanceof SQLExpr) {
            return getQueryBlock(parent);
        }

        if (parent instanceof SQLSelectItem) {
            return getQueryBlock(parent);
        }

        if (parent instanceof SQLSelectQueryBlock) {
            return (SQLSelectQueryBlock) parent;
        }

        return null;
    }

    public static boolean isTopNoneFromSelect(WallVisitor visitor, SQLObject x) {
        for (;;) {
            if ((x.getParent() instanceof SQLExpr) || (x.getParent() instanceof Item)) {
                x = x.getParent();
            } else {
                break;
            }
        }

        if (!(x.getParent() instanceof SQLSelectItem)) {
            return false;
        }
        SQLSelectItem item = (SQLSelectItem) x.getParent();

        if (!(item.getParent() instanceof SQLSelectQueryBlock)) {
            return false;
        }

        SQLSelectQueryBlock queryBlock = (SQLSelectQueryBlock) item.getParent();
        if (!queryBlockFromIsNull(visitor, queryBlock)) {
            return false;
        }

        if (!(queryBlock.getParent() instanceof SQLSelect)) {
            return false;
        }

        SQLSelect select = (SQLSelect) queryBlock.getParent();

        if (!(select.getParent() instanceof SQLSelectStatement)) {
            return false;
        }

        SQLSelectStatement stmt = (SQLSelectStatement) select.getParent();

        return stmt.getParent() == null;
    }

    private static boolean checkSchema(WallVisitor visitor, SQLExpr x) {
        final WallTopStatementContext topStatementContext = wallTopStatementContextLocal.get();
        if (topStatementContext != null && (topStatementContext.fromSysSchema || topStatementContext.fromSysTable)) {
            return true;
        }

        if (x instanceof SQLName) {
            String owner = ((SQLName) x).getSimleName();
            owner = WallVisitorUtils.form(owner);
            if (isInTableSource(x) && !visitor.getProvider().checkDenySchema(owner)) {

                if (!isTopUpdateStatement(x) && !isFirstSelectTableSource(x)) {
                    SQLObject parent = x.getParent();
                    while (parent != null && !(parent instanceof SQLStatement)) {
                        parent = parent.getParent();
                    }

                    boolean sameToTopSelectSchema = false;
                    if (parent instanceof SQLSelectStatement) {
                        SQLSelectStatement selectStmt = (SQLSelectStatement) parent;
                        SQLSelectQuery query = selectStmt.getSelect().getQuery();
                        if (query instanceof SQLSelectQueryBlock) {
                            SQLSelectQueryBlock queryBlock = (SQLSelectQueryBlock) query;
                            SQLTableSource from = queryBlock.getFrom();

                            while (from instanceof SQLJoinTableSource) {
                                from = ((SQLJoinTableSource) from).getLeft();
                            }
                            if (from instanceof SQLExprTableSource) {
                                SQLExpr expr = ((SQLExprTableSource) from).getExpr();
                                if (expr instanceof SQLPropertyExpr) {
                                    SQLExpr schemaExpr = ((SQLPropertyExpr) expr).getOwner();
                                    if (schemaExpr instanceof SQLIdentifierExpr) {
                                        String schema = ((SQLIdentifierExpr) schemaExpr).getName();
                                        schema = form(schema);
                                        if (schema.equalsIgnoreCase(owner)) {
                                            sameToTopSelectSchema = true;
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (!sameToTopSelectSchema) {
                        addViolation(visitor, ErrorCode.SCHEMA_DENY, "deny schema : " + owner, x);
                    }
                } else {
                    if (topStatementContext != null) {
                        topStatementContext.setFromSysSchema(Boolean.TRUE);
                        clearViolation(visitor);
                    }
                }
                return true;
            }

            if (visitor.getConfig().isDenyObjects(owner)) {
                addViolation(visitor, ErrorCode.OBJECT_DENY, "deny object : " + owner, x);
                return true;
            }
        }

        // if (ownerExpr instanceof SQLPropertyExpr) {
        if (x instanceof SQLPropertyExpr) {
            return checkSchema(visitor, ((SQLPropertyExpr) x).getOwner());
        }

        return true;
    }

    private static boolean isInTableSource(SQLObject x) {

        for (;;) {
            if (x instanceof SQLExpr) {
                x = x.getParent();
            } else {
                break;
            }
        }

        if (x instanceof SQLExprTableSource) {
            return true;
        }
        return false;
    }

    private static boolean isFirstSelectTableSource(SQLObject x) {

        for (;;) {
            if (x instanceof SQLExpr) {
                x = x.getParent();
            } else {
                break;
            }
        }

        if (!(x instanceof SQLExprTableSource)) {
            return false;
        }

        SQLSelectQueryBlock queryBlock = null;
        SQLObject parent = x.getParent();
        while (parent != null) {

            // if (parent instanceof SQLJoinTableSource) {
            // SQLJoinTableSource join = (SQLJoinTableSource) parent;
            // if (join.getRight() == x && hasTableSource(join.getLeft())) {
            // return false;
            // }
            // }

            if (parent instanceof SQLSelectQueryBlock) {
                queryBlock = (SQLSelectQueryBlock) parent;
                break;
            }

            x = parent;
            parent = x.getParent();
        }

        if (queryBlock == null) {
            return false;
        }

        boolean isWhereQueryExpr = false;
        do {
            x = parent;
            parent = parent.getParent();
            if (parent instanceof SQLUnionQuery) {
                SQLUnionQuery union = (SQLUnionQuery) parent;
                if (union.getRight() == x && hasTableSource(union.getLeft())) {
                    return false;
                }
            } else if (parent instanceof SQLQueryExpr || parent instanceof SQLInSubQueryExpr) {
                isWhereQueryExpr = isWhereOrHaving(parent);
            } else if (isWhereQueryExpr && parent instanceof SQLSelectQueryBlock) {
                if (hasTableSource((SQLSelectQueryBlock) parent)) {
                    return false;
                }
            }

        } while (parent != null);

        return true;
    }

    private static boolean hasTableSource(SQLSelectQuery x) {

        if (x instanceof SQLUnionQuery) {
            SQLUnionQuery union = (SQLUnionQuery) x;
            return hasTableSource(union.getLeft()) || hasTableSource(union.getRight());
        } else if (x instanceof SQLSelectQueryBlock) {
            return hasTableSource(((SQLSelectQueryBlock) x).getFrom());
        }

        return false;
    }

    private static boolean hasTableSource(SQLTableSource x) {
        if (x == null) {
            return false;
        }

        if (x instanceof SQLExprTableSource) {
            SQLExpr fromExpr = ((SQLExprTableSource) x).getExpr();
            if (fromExpr instanceof SQLName) {
                String name = fromExpr.toString();
                name = form(name);
                if (name.equalsIgnoreCase("DUAL")) {
                    return false;
                }
            }
            return true;
        } else if (x instanceof SQLJoinTableSource) {
            SQLJoinTableSource join = (SQLJoinTableSource) x;
            return hasTableSource(join.getLeft()) || hasTableSource(join.getRight());
        } else if (x instanceof SQLSubqueryTableSource) {
            return hasTableSource(((SQLSubqueryTableSource) x).getSelect().getQuery());
        }

        return false;
    }

    private static boolean isTopUpdateStatement(SQLObject x) {

        for (;;) {
            if (x instanceof SQLExpr) {
                x = x.getParent();
            } else {
                break;
            }
        }

        if (x instanceof SQLExprTableSource) {
            x = x.getParent();

            if (x instanceof SQLUpdateStatement) {
                x = x.getParent();
                if (x == null) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean check(WallVisitor visitor, SQLExprTableSource x) {
        final WallTopStatementContext topStatementContext = wallTopStatementContextLocal.get();

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

            if (topStatementContext != null && (topStatementContext.fromSysSchema || topStatementContext.fromSysTable)) {
                return true;
            }

            if (visitor.isDenyTable(tableName)
                && !(topStatementContext != null && topStatementContext.fromPermitTable())) {

                if (isTopUpdateStatement(x) || isFirstSelectTableSource(x)) {
                    if (topStatementContext != null) {
                        topStatementContext.setFromSysTable(Boolean.TRUE);
                        clearViolation(visitor);
                    }
                    return false;
                }

                boolean isTopNoneFrom = isTopNoneFromSelect(visitor, x);
                if (isTopNoneFrom) {
                    return false;
                }

                addViolation(visitor, ErrorCode.TABLE_DENY, "deny table : " + tableName, x);
                return false;
            }

            if (visitor.getConfig().getPermitTables().contains(tableName)) {
                if (isFirstSelectTableSource(x)) {
                    if (topStatementContext != null) {
                        topStatementContext.setFromPermitTable(Boolean.TRUE);
                    }
                    return false;
                }
            }
        }

        return true;
    }

    private static void addViolation(WallVisitor visitor, int errorCode, String message, SQLObject x) {
        visitor.addViolation(new IllegalSQLObjectViolation(errorCode, message, visitor.toSQL(x)));
    }

    private static void clearViolation(WallVisitor visitor) {
        visitor.getViolations().clear();
    }

    public static void checkUnion(WallVisitor visitor, SQLUnionQuery x) {
        if (x.getOperator() == SQLUnionOperator.MINUS && !visitor.getConfig().isMinusAllow()) {
            addViolation(visitor, ErrorCode.INTERSET_NOT_ALLOW, "minus not allow", x);
            return;
        }

        if (x.getOperator() == SQLUnionOperator.INTERSECT && !visitor.getConfig().isIntersectAllow()) {
            addViolation(visitor, ErrorCode.INTERSET_NOT_ALLOW, "intersect not allow", x);
            return;
        }

        if (WallVisitorUtils.queryBlockFromIsNull(visitor, x.getRight())) {
            boolean isTopUpdateStatement = false;
            SQLObject selectParent = x.getParent();
            while (selectParent instanceof SQLUnionQuery //
                   || selectParent instanceof SQLJoinTableSource //
                   || selectParent instanceof SQLSubqueryTableSource //
                   || selectParent instanceof SQLSelect) {
                selectParent = selectParent.getParent();
            }

            if (selectParent instanceof SQLUpdateStatement) {
                isTopUpdateStatement = true;
            }

            if (isTopUpdateStatement) {
                return;
            }

            WallContext context = WallContext.current();
            if (context != null) {
                context.incrementUnionWarnnings();
            }

            if (visitor.getConfig().isSelectUnionCheck()) {
                addViolation(visitor, ErrorCode.UNION, "union query not contains 'from clause'", x);
            }
        }
    }

    public static boolean queryBlockFromIsNull(WallVisitor visitor, SQLSelectQuery query) {
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
                    if (queryBlockFromIsNull(visitor, subQuery)) {
                        return true;
                    }
                }
            }

            boolean allIsConst = true;
            for (SQLSelectItem item : queryBlock.getSelectList()) {
                if (getValue(visitor, item.getExpr()) == null) {
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

        if (name.startsWith("'") && name.endsWith("'")) {
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
            boolean hasResource = false;
            Enumeration<URL> e = Thread.currentThread().getContextClassLoader().getResources(resource);
            while (e.hasMoreElements()) {
                URL url = e.nextElement();
                InputStream in = null;
                try {
                    in = url.openStream();
                    readFromInputStream(names, in);

                    hasResource = true;
                } finally {
                    JdbcUtils.close(in);
                }
            }

            // for aliyun odps
            if (!hasResource) {
                if (!resource.startsWith("/")) {
                    resource = "/" + resource;
                }

                InputStream in = null;
                try {
                    in = WallVisitorUtils.class.getResourceAsStream(resource);
                    if (in != null) {
                        readFromInputStream(names, in);
                    }
                } finally {
                    JdbcUtils.close(in);
                }
            }
        } catch (IOException e) {
            LOG.error("load oracle deny tables errror", e);
        }
    }

    private static void readFromInputStream(Set<String> names, InputStream in) throws IOException {
        BufferedReader reader = null;
        try {
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
        } finally {
            JdbcUtils.close(reader);
        }
    }

    public static void preVisitCheck(WallVisitor visitor, SQLObject x) {
        WallConfig config = visitor.getProvider().getConfig();

        if (!(x instanceof SQLStatement)) {
            return;
        }

        boolean allow = false;
        int errorCode;
        String denyMessage;
        if (x instanceof SQLInsertStatement) {
            allow = config.isInsertAllow();
            denyMessage = "insert not allow";
            errorCode = ErrorCode.INSERT_NOT_ALLOW;
        } else if (x instanceof SQLSelectStatement) {
            allow = true;
            denyMessage = "select not allow";
            errorCode = ErrorCode.SELECT_NOT_ALLOW;
        } else if (x instanceof SQLDeleteStatement) {
            allow = config.isDeleteAllow();
            denyMessage = "delete not allow";
            errorCode = ErrorCode.DELETE_NOT_ALLOW;
        } else if (x instanceof SQLUpdateStatement) {
            allow = config.isUpdateAllow();
            denyMessage = "update not allow";
            errorCode = ErrorCode.UPDATE_NOT_ALLOW;
        } else if (x instanceof OracleMultiInsertStatement) {
            allow = true;
            denyMessage = "multi-insert not allow";
            errorCode = ErrorCode.INSERT_NOT_ALLOW;
        } else if (x instanceof OracleMergeStatement) {
            allow = config.isMergeAllow();
            denyMessage = "merge not allow";
            errorCode = ErrorCode.MERGE_NOT_ALLOW;
        } else if (x instanceof SQLCallStatement || x instanceof SQLServerExecStatement) {
            allow = config.isCallAllow();
            denyMessage = "call not allow";
            errorCode = ErrorCode.CALL_NOT_ALLOW;
        } else if (x instanceof SQLTruncateStatement) {
            allow = config.isTruncateAllow();
            denyMessage = "truncate not allow";
            errorCode = ErrorCode.TRUNCATE_NOT_ALLOW;
        } else if (x instanceof SQLCreateTableStatement //
                   || x instanceof SQLCreateIndexStatement //
                   || x instanceof SQLCreateViewStatement //
                   || x instanceof SQLCreateTriggerStatement //
                   || x instanceof OracleCreateSequenceStatement //
        ) {
            allow = config.isCreateTableAllow();
            denyMessage = "create table not allow";
            errorCode = ErrorCode.CREATE_TABLE_NOT_ALLOW;
        } else if (x instanceof SQLAlterTableStatement) {
            allow = config.isAlterTableAllow();
            denyMessage = "alter table not allow";
            errorCode = ErrorCode.ALTER_TABLE_NOT_ALLOW;
        } else if (x instanceof SQLDropTableStatement //
                   || x instanceof SQLDropIndexStatement //
                   || x instanceof SQLDropViewStatement //
                   || x instanceof SQLDropTriggerStatement //
                   || x instanceof SQLDropSequenceStatement //
        ) {
            allow = config.isDropTableAllow();
            denyMessage = "drop table not allow";
            errorCode = ErrorCode.DROP_TABLE_NOT_ALLOW;
        } else if (x instanceof MySqlSetCharSetStatement //
                   || x instanceof MySqlSetNamesStatement //
                   || x instanceof SQLSetStatement) {
            allow = config.isSetAllow();
            denyMessage = "set not allow";
            errorCode = ErrorCode.SET_NOT_ALLOW;
        } else if (x instanceof MySqlReplaceStatement) {
            allow = config.isReplaceAllow();
            denyMessage = "replace not allow";
            errorCode = ErrorCode.REPLACE_NOT_ALLOW;
        } else if (x instanceof MySqlDescribeStatement) {
            allow = config.isDescribeAllow();
            denyMessage = "describe not allow";
            errorCode = ErrorCode.DESC_NOT_ALLOW;
        } else if (x instanceof MySqlShowStatement) {
            allow = config.isShowAllow();
            denyMessage = "show not allow";
            errorCode = ErrorCode.SHOW_NOT_ALLOW;
        } else if (x instanceof MySqlCommitStatement) {
            allow = config.isCommitAllow();
            denyMessage = "show not allow";
            errorCode = ErrorCode.COMMIT_NOT_ALLOW;
        } else if (x instanceof SQLRollbackStatement) {
            allow = config.isRollbackAllow();
            denyMessage = "show not allow";
            errorCode = ErrorCode.ROLLBACK_NOT_ALLOW;
        } else if (x instanceof SQLUseStatement) {
            allow = config.isUseAllow();
            denyMessage = "show not allow";
            errorCode = ErrorCode.USE_NOT_ALLOW;
        } else {
            allow = config.isNoneBaseStatementAllow();
            errorCode = ErrorCode.NONE_BASE_STATEMENT_NOT_ALLOW;
            denyMessage = x.getClass() + " not allow";
        }

        if (!allow) {
            addViolation(visitor, errorCode, denyMessage, x);
        }
    }
}
