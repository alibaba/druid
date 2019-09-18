/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
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

import static com.alibaba.druid.sql.visitor.SQLEvalVisitor.EVAL_VALUE;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import com.alibaba.druid.sql.ast.SQLCommentHint;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLLimit;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.SQLOrderBy;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLAggregateExpr;
import com.alibaba.druid.sql.ast.expr.SQLAllColumnExpr;
import com.alibaba.druid.sql.ast.expr.SQLBetweenExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExprGroup;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOperator;
import com.alibaba.druid.sql.ast.expr.SQLBooleanExpr;
import com.alibaba.druid.sql.ast.expr.SQLCaseExpr;
import com.alibaba.druid.sql.ast.expr.SQLCaseExpr.Item;
import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import com.alibaba.druid.sql.ast.expr.SQLExistsExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLInListExpr;
import com.alibaba.druid.sql.ast.expr.SQLInSubQueryExpr;
import com.alibaba.druid.sql.ast.expr.SQLIntegerExpr;
import com.alibaba.druid.sql.ast.expr.SQLLiteralExpr;
import com.alibaba.druid.sql.ast.expr.SQLMethodInvokeExpr;
import com.alibaba.druid.sql.ast.expr.SQLNCharExpr;
import com.alibaba.druid.sql.ast.expr.SQLNotExpr;
import com.alibaba.druid.sql.ast.expr.SQLNumberExpr;
import com.alibaba.druid.sql.ast.expr.SQLNumericLiteralExpr;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.ast.expr.SQLQueryExpr;
import com.alibaba.druid.sql.ast.expr.SQLUnaryExpr;
import com.alibaba.druid.sql.ast.expr.SQLValuableExpr;
import com.alibaba.druid.sql.ast.expr.SQLVariantRefExpr;
import com.alibaba.druid.sql.ast.statement.SQLAlterStatement;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLBlockStatement;
import com.alibaba.druid.sql.ast.statement.SQLCallStatement;
import com.alibaba.druid.sql.ast.statement.SQLCommentStatement;
import com.alibaba.druid.sql.ast.statement.SQLCommitStatement;
import com.alibaba.druid.sql.ast.statement.SQLCreateStatement;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLDeleteStatement;
import com.alibaba.druid.sql.ast.statement.SQLDescribeStatement;
import com.alibaba.druid.sql.ast.statement.SQLDropStatement;
import com.alibaba.druid.sql.ast.statement.SQLDropTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLExplainStatement;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLInsertInto;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement.ValuesClause;
import com.alibaba.druid.sql.ast.statement.SQLJoinTableSource;
import com.alibaba.druid.sql.ast.statement.SQLMergeStatement;
import com.alibaba.druid.sql.ast.statement.SQLReplaceStatement;
import com.alibaba.druid.sql.ast.statement.SQLRollbackStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLSelectGroupByClause;
import com.alibaba.druid.sql.ast.statement.SQLSelectItem;
import com.alibaba.druid.sql.ast.statement.SQLSelectQuery;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.ast.statement.SQLSetStatement;
import com.alibaba.druid.sql.ast.statement.SQLShowTablesStatement;
import com.alibaba.druid.sql.ast.statement.SQLStartTransactionStatement;
import com.alibaba.druid.sql.ast.statement.SQLSubqueryTableSource;
import com.alibaba.druid.sql.ast.statement.SQLTableSource;
import com.alibaba.druid.sql.ast.statement.SQLTruncateStatement;
import com.alibaba.druid.sql.ast.statement.SQLUnionOperator;
import com.alibaba.druid.sql.ast.statement.SQLUnionQuery;
import com.alibaba.druid.sql.ast.statement.SQLUpdateSetItem;
import com.alibaba.druid.sql.ast.statement.SQLUpdateStatement;
import com.alibaba.druid.sql.ast.statement.SQLUseStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.expr.MySqlOrderingExpr;
import com.alibaba.druid.sql.dialect.mysql.ast.expr.MySqlOutFileExpr;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlDeleteStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlExplainStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlHintStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlInsertStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlLockTableStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlOptimizeStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlRenameTableStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowGrantsStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlUpdateStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleExecuteImmediateStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleMultiInsertStatement;
import com.alibaba.druid.sql.dialect.postgresql.ast.stmt.PGShowStatement;
import com.alibaba.druid.sql.dialect.sqlserver.ast.stmt.SQLServerExecStatement;
import com.alibaba.druid.sql.dialect.sqlserver.ast.stmt.SQLServerInsertStatement;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.visitor.ExportParameterVisitor;
import com.alibaba.druid.sql.visitor.SQLEvalVisitor;
import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import com.alibaba.druid.sql.visitor.functions.Nil;
import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;
import com.alibaba.druid.util.FnvHash;
import com.alibaba.druid.util.JdbcUtils;
import com.alibaba.druid.util.ServletPathMatcher;
import com.alibaba.druid.util.StringUtils;
import com.alibaba.druid.wall.WallConfig;
import com.alibaba.druid.wall.WallConfig.TenantCallBack;
import com.alibaba.druid.wall.WallConfig.TenantCallBack.StatementType;
import com.alibaba.druid.wall.WallContext;
import com.alibaba.druid.wall.WallProvider;
import com.alibaba.druid.wall.WallSqlTableStat;
import com.alibaba.druid.wall.WallUpdateCheckHandler;
import com.alibaba.druid.wall.WallUpdateCheckItem;
import com.alibaba.druid.wall.WallVisitor;
import com.alibaba.druid.wall.violation.ErrorCode;
import com.alibaba.druid.wall.violation.IllegalSQLObjectViolation;

public class WallVisitorUtils {

    private final static Log     LOG           = LogFactory.getLog(WallVisitorUtils.class);

    public final static String   HAS_TRUE_LIKE = "hasTrueLike";

    public final static String[] whiteHints    = { "LOCAL", "TEMPORARY", "SQL_NO_CACHE", "SQL_CACHE", "HIGH_PRIORITY",
            "LOW_PRIORITY", "STRAIGHT_JOIN", "SQL_BUFFER_RESULT", "SQL_BIG_RESULT", "SQL_SMALL_RESULT", "DELAYED" };

    public static void check(WallVisitor visitor, SQLInListExpr x) {

    }

    public static boolean check(WallVisitor visitor, SQLBinaryOpExpr x) {
        if (x.getOperator() == SQLBinaryOperator.BooleanOr
                || x.getOperator() == SQLBinaryOperator.BooleanAnd) {
            List<SQLExpr> groupList = SQLBinaryOpExpr.split(x);
            for (SQLExpr item : groupList) {
                item.accept(visitor);
            }
            return false;
        }

        if (x.getOperator() == SQLBinaryOperator.Add
                || x.getOperator() == SQLBinaryOperator.Concat) {
            List<SQLExpr> groupList = SQLBinaryOpExpr.split(x);
            if (groupList.size() >= 4) {
                int chrCount = 0;
                for (int i = 0; i < groupList.size(); ++i) {
                    SQLExpr item = groupList.get(i);
                    if (item instanceof SQLMethodInvokeExpr) {
                        SQLMethodInvokeExpr methodExpr = (SQLMethodInvokeExpr) item;
                        String methodName = methodExpr.getMethodName().toLowerCase();
                        if ("chr".equals(methodName) || "char".equals(methodName)) {
                            if (methodExpr.getParameters().get(0) instanceof SQLLiteralExpr) {
                                chrCount++;
                            }
                        }
                    } else if (item instanceof SQLCharExpr) {
                        if (((SQLCharExpr) item).getText().length() > 5) {
                            chrCount = 0;
                            continue;
                        }
                    }

                    if (chrCount >= 4) {
                        addViolation(visitor, ErrorCode.EVIL_CONCAT, "evil concat", x);
                        break;
                    }
                }
            }
        }
        
        return true;
    }

    public static boolean check(WallVisitor visitor, SQLBinaryOpExprGroup x) {
        return true;
    }

    public static void check(WallVisitor visitor, SQLCreateTableStatement x) {
        String tableName = ((SQLName) x.getName()).getSimpleName();
        WallContext context = WallContext.current();
        if (context != null) {
            WallSqlTableStat tableStat = context.getTableStat(tableName);
            if (tableStat != null) {
                tableStat.incrementCreateCount();
            }
        }
    }

    public static void check(WallVisitor visitor, SQLAlterTableStatement x) {
        String tableName = ((SQLName) x.getName()).getSimpleName();
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
                String tableName = ((SQLName) expr).getSimpleName();
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
        SQLExpr expr = x.getExpr();

        if (expr instanceof SQLVariantRefExpr) {
            if (!isTopSelectItem(expr) && "@".equals(((SQLVariantRefExpr) expr).getName())) {
                addViolation(visitor, ErrorCode.EVIL_NAME, "@ not allow", x);
            }
        }

        if (visitor.getConfig().isSelectAllColumnAllow()) {
            return;
        }

        if (expr instanceof SQLAllColumnExpr //
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

        checkInsertForMultiTenant(visitor, x);
    }

    public static void checkSelelct(WallVisitor visitor, SQLSelectQueryBlock x) {
        if (x.getInto() != null) {
            checkReadOnly(visitor, x.getInto());
        }

        if (!visitor.getConfig().isSelectIntoAllow() && x.getInto() != null) {
            addViolation(visitor, ErrorCode.SELECT_INTO_NOT_ALLOW, "select into not allow", x);
            return;
        }

        List<SQLCommentHint> hints = x.getHintsDirect();
        if (hints != null
                && x.getParent() instanceof SQLUnionQuery
                && x == ((SQLUnionQuery) x.getParent()).getRight()
        ) {
            for (SQLCommentHint hint : hints) {
                String text = hint.getText();
                if (text.startsWith("!")) {
                    addViolation(visitor, ErrorCode.UNION, "union select hint not allow", x);
                    return;
                }
            }
        }

        SQLExpr where = x.getWhere();
        if (where != null) {
            checkCondition(visitor, x.getWhere());

            Object whereValue = getConditionValue(visitor, where, visitor.getConfig().isSelectWhereAlwayTrueCheck());

            if (Boolean.TRUE == whereValue) {
                if (visitor.getConfig().isSelectWhereAlwayTrueCheck()
                        && visitor.isSqlEndOfComment()
                        && !isSimpleConstExpr(where)) {// 简单表达式
                    addViolation(visitor, ErrorCode.ALWAYS_TRUE, "select alway true condition not allow", x);
                }
            }
        }
        checkSelectForMultiTenant(visitor, x);
        // checkConditionForMultiTenant(visitor, x.getWhere(), x);
    }

    public static void checkHaving(WallVisitor visitor, SQLExpr x) {
        if (x == null) {
            return;
        }

        if (Boolean.TRUE == getConditionValue(visitor, x, visitor.getConfig().isSelectHavingAlwayTrueCheck())) {
            if (visitor.getConfig().isSelectHavingAlwayTrueCheck()
                    && visitor.isSqlEndOfComment()
                    && !isSimpleConstExpr(x)) {
                addViolation(visitor, ErrorCode.ALWAYS_TRUE, "having alway true condition not allow", x);
            }
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
                context.incrementDeleteNoneConditionWarnings();
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
                if (config.isDeleteWhereAlwayTrueCheck() && visitor.isSqlEndOfComment() && !isSimpleConstExpr(where)) {
                    addViolation(visitor, ErrorCode.ALWAYS_TRUE, "delete alway true condition not allow", x);
                }
            }
        }

        // checkConditionForMultiTenant(visitor, x.getWhere(), x);
    }

    private static boolean isSimpleConstExpr(SQLExpr sqlExpr) {
        List<SQLExpr> parts = getParts(sqlExpr);
        if (parts.isEmpty()) {
            return false;
        }

        for (SQLExpr part : parts) {
            if(isFirst(part)) {
                Object evalValue = part.getAttribute(EVAL_VALUE);
                if (evalValue == null) {
                    if (part instanceof SQLBooleanExpr) {
                        evalValue = ((SQLBooleanExpr) part).getBooleanValue();
                    } else if (part instanceof SQLNumericLiteralExpr) {
                        evalValue = ((SQLNumericLiteralExpr) part).getNumber();
                    } else if (part instanceof SQLCharExpr) {
                        evalValue = ((SQLCharExpr) part).getText();
                    } else if (part instanceof SQLNCharExpr) {
                        evalValue = ((SQLNCharExpr) part).getText();
                    }
                }
                Boolean result = SQLEvalVisitorUtils.castToBoolean(evalValue);
                if (result != null && result) {
                    return true;
                }
            }
            boolean isSimpleConstExpr = false;
            if (part == sqlExpr || part instanceof SQLLiteralExpr) {
                isSimpleConstExpr = true;
            } else if (part instanceof SQLBinaryOpExpr) {
                SQLBinaryOpExpr binaryOpExpr = (SQLBinaryOpExpr) part;

                if (binaryOpExpr.getOperator() == SQLBinaryOperator.Equality
                    || binaryOpExpr.getOperator() == SQLBinaryOperator.NotEqual
                    || binaryOpExpr.getOperator() == SQLBinaryOperator.GreaterThan) {
                    if (binaryOpExpr.getLeft() instanceof SQLIntegerExpr
                        && binaryOpExpr.getRight() instanceof SQLIntegerExpr) {
                        isSimpleConstExpr = true;
                    }
                }
            }

            if (!isSimpleConstExpr) {
                return false;
            }
        }
        return true;
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
            }
        }

    }

    private static void checkJoinSelectForMultiTenant(WallVisitor visitor, SQLJoinTableSource join,
                                                      SQLSelectQueryBlock x) {
        TenantCallBack tenantCallBack = visitor.getConfig().getTenantCallBack();
        String tenantTablePattern = visitor.getConfig().getTenantTablePattern();
        if (tenantCallBack == null && (tenantTablePattern == null || tenantTablePattern.length() == 0)) {
            return;
        }

        SQLTableSource right = join.getRight();
        if (right instanceof SQLExprTableSource) {
            SQLExpr tableExpr = ((SQLExprTableSource) right).getExpr();

            if (tableExpr instanceof SQLIdentifierExpr) {
                String tableName = ((SQLIdentifierExpr) tableExpr).getName();

                String alias = null;
                String tenantColumn = null;
                if (tenantCallBack != null) {
                    tenantColumn = tenantCallBack.getTenantColumn(StatementType.SELECT, tableName);
                }

                if (StringUtils.isEmpty(tenantColumn)
                    && ServletPathMatcher.getInstance().matches(tenantTablePattern, tableName)) {
                    tenantColumn = visitor.getConfig().getTenantColumn();
                }

                if (!StringUtils.isEmpty(tenantColumn)) {
                    alias = right.getAlias();
                    if (alias == null) {
                        alias = tableName;
                    }

                    SQLExpr item = null;
                    if (alias != null) {
                        item = new SQLPropertyExpr(new SQLIdentifierExpr(alias), tenantColumn);
                    } else {
                        item = new SQLIdentifierExpr(tenantColumn);
                    }
                    SQLSelectItem selectItem = new SQLSelectItem(item);
                    x.getSelectList().add(selectItem);
                    visitor.setSqlModified(true);
                }
            }
        }
    }

    private static boolean isSelectStatmentForMultiTenant(SQLSelectQueryBlock queryBlock) {

        SQLObject parent = queryBlock.getParent();
        while (parent != null) {

            if (parent instanceof SQLUnionQuery) {
                SQLObject x = parent;
                parent = x.getParent();
            } else {
                break;
            }
        }

        if (!(parent instanceof SQLSelect)) {
            return false;
        }

        parent = ((SQLSelect) parent).getParent();
        if (parent instanceof SQLSelectStatement) {
            return true;
        }

        return false;
    }

    private static void checkSelectForMultiTenant(WallVisitor visitor, SQLSelectQueryBlock x) {
        TenantCallBack tenantCallBack = visitor.getConfig().getTenantCallBack();
        String tenantTablePattern = visitor.getConfig().getTenantTablePattern();
        if (tenantCallBack == null && (tenantTablePattern == null || tenantTablePattern.length() == 0)) {
            return;
        }

        if (x == null) {
            throw new IllegalStateException("x is null");
        }

        if (!isSelectStatmentForMultiTenant(x)) {
            return;
        }

        SQLTableSource tableSource = x.getFrom();
        String alias = null;
        String matchTableName = null;
        String tenantColumn = null;
        if (tableSource instanceof SQLExprTableSource) {
            SQLExpr tableExpr = ((SQLExprTableSource) tableSource).getExpr();

            if (tableExpr instanceof SQLIdentifierExpr) {
                String tableName = ((SQLIdentifierExpr) tableExpr).getName();

                if (tenantCallBack != null) {
                    tenantColumn = tenantCallBack.getTenantColumn(StatementType.SELECT, tableName);
                }

                if (StringUtils.isEmpty(tenantColumn)
                    && ServletPathMatcher.getInstance().matches(tenantTablePattern, tableName)) {
                    tenantColumn = visitor.getConfig().getTenantColumn();
                }

                if (!StringUtils.isEmpty(tenantColumn)) {
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

                    if (tenantCallBack != null) {
                        tenantColumn = tenantCallBack.getTenantColumn(StatementType.SELECT, tableName);
                    }

                    if (StringUtils.isEmpty(tenantColumn)
                        && ServletPathMatcher.getInstance().matches(tenantTablePattern, tableName)) {
                        tenantColumn = visitor.getConfig().getTenantColumn();
                    }

                    if (!StringUtils.isEmpty(tenantColumn)) {
                        matchTableName = tableName;
                        alias = join.getLeft().getAlias();

                        if (alias == null) {
                            alias = tableName;
                        }
                    }
                }
                checkJoinSelectForMultiTenant(visitor, join, x);
            } else {
                checkJoinSelectForMultiTenant(visitor, join, x);
            }
        }

        if (matchTableName == null) {
            return;
        }

        SQLExpr item = null;
        if (alias != null) {
            item = new SQLPropertyExpr(new SQLIdentifierExpr(alias), tenantColumn);
        } else {
            item = new SQLIdentifierExpr(tenantColumn);
        }
        SQLSelectItem selectItem = new SQLSelectItem(item);
        x.getSelectList().add(selectItem);
        visitor.setSqlModified(true);
    }

    private static void checkUpdateForMultiTenant(WallVisitor visitor, SQLUpdateStatement x) {
        TenantCallBack tenantCallBack = visitor.getConfig().getTenantCallBack();
        String tenantTablePattern = visitor.getConfig().getTenantTablePattern();
        if (tenantCallBack == null && (tenantTablePattern == null || tenantTablePattern.length() == 0)) {
            return;
        }

        if (x == null) {
            throw new IllegalStateException("x is null");
        }

        SQLTableSource tableSource = x.getTableSource();
        String alias = null;
        String matchTableName = null;
        String tenantColumn = null;
        if (tableSource instanceof SQLExprTableSource) {
            SQLExpr tableExpr = ((SQLExprTableSource) tableSource).getExpr();
            if (tableExpr instanceof SQLIdentifierExpr) {
                String tableName = ((SQLIdentifierExpr) tableExpr).getName();

                if (tenantCallBack != null) {
                    tenantColumn = tenantCallBack.getTenantColumn(StatementType.UPDATE, tableName);
                }
                if (StringUtils.isEmpty(tenantColumn)
                    && ServletPathMatcher.getInstance().matches(tenantTablePattern, tableName)) {
                    tenantColumn = visitor.getConfig().getTenantColumn();
                }

                if (!StringUtils.isEmpty(tenantColumn)) {
                    matchTableName = tableName;
                    alias = tableSource.getAlias();
                }
            }
        }

        if (matchTableName == null) {
            return;
        }

        SQLExpr item = null;
        if (alias != null) {
            item = new SQLPropertyExpr(new SQLIdentifierExpr(alias), tenantColumn);
        } else {
            item = new SQLIdentifierExpr(tenantColumn);
        }
        SQLExpr value = generateTenantValue(visitor, alias, StatementType.UPDATE, matchTableName);

        SQLUpdateSetItem updateSetItem = new SQLUpdateSetItem();
        updateSetItem.setColumn(item);
        updateSetItem.setValue(value);

        x.addItem(updateSetItem);
        visitor.setSqlModified(true);
    }

    private static void checkInsertForMultiTenant(WallVisitor visitor, SQLInsertInto x) {
        TenantCallBack tenantCallBack = visitor.getConfig().getTenantCallBack();
        String tenantTablePattern = visitor.getConfig().getTenantTablePattern();
        if (tenantCallBack == null && (tenantTablePattern == null || tenantTablePattern.length() == 0)) {
            return;
        }

        if (x == null) {
            throw new IllegalStateException("x is null");
        }

        SQLExprTableSource tableSource = x.getTableSource();
        String alias = null;
        String matchTableName = null;
        String tenantColumn = null;
        SQLExpr tableExpr = tableSource.getExpr();
        if (tableExpr instanceof SQLIdentifierExpr) {
            String tableName = ((SQLIdentifierExpr) tableExpr).getName();

            if (tenantCallBack != null) {
                tenantColumn = tenantCallBack.getTenantColumn(StatementType.INSERT, tableName);
            }
            if (StringUtils.isEmpty(tenantColumn)
                && ServletPathMatcher.getInstance().matches(tenantTablePattern, tableName)) {
                tenantColumn = visitor.getConfig().getTenantColumn();
            }

            if (!StringUtils.isEmpty(tenantColumn)) {
                matchTableName = tableName;
                alias = tableSource.getAlias();
            }
        }

        if (matchTableName == null) {
            return;
        }

        SQLExpr item = null;
        if (alias != null) {
            item = new SQLPropertyExpr(new SQLIdentifierExpr(alias), tenantColumn);
        } else {
            item = new SQLIdentifierExpr(tenantColumn);
        }
        SQLExpr value = generateTenantValue(visitor, alias, StatementType.INSERT, matchTableName);

        // add insert item and value
        x.getColumns().add(item);

        List<ValuesClause> valuesClauses = null;
        ValuesClause valuesClause = null;
        if (x instanceof MySqlInsertStatement) {
            valuesClauses = ((MySqlInsertStatement) x).getValuesList();
        } else if (x instanceof SQLServerInsertStatement) {
            valuesClauses = ((MySqlInsertStatement) x).getValuesList();
        } else {
            valuesClause = x.getValues();
        }

        if (valuesClauses != null && valuesClauses.size() > 0) {
            for (ValuesClause clause : valuesClauses) {
                clause.addValue(value);
            }
        }
        if (valuesClause != null) {
            valuesClause.addValue(value);
        }

        // insert .. select
        SQLSelect select = x.getQuery();
        if (select != null) {
            List<SQLSelectQueryBlock> queryBlocks = splitSQLSelectQuery(select.getQuery());
            for (SQLSelectQueryBlock queryBlock : queryBlocks) {
                queryBlock.getSelectList().add(new SQLSelectItem(value));
            }
        }

        visitor.setSqlModified(true);
    }

    private static List<SQLSelectQueryBlock> splitSQLSelectQuery(SQLSelectQuery x) {
        List<SQLSelectQueryBlock> groupList = new ArrayList<SQLSelectQueryBlock>();
        Stack<SQLSelectQuery> stack = new Stack<SQLSelectQuery>();

        stack.push(x);
        do {
            SQLSelectQuery query = stack.pop();
            if (query instanceof SQLSelectQueryBlock) {
                groupList.add((SQLSelectQueryBlock) query);
            } else if (query instanceof SQLUnionQuery) {
                SQLUnionQuery unionQuery = (SQLUnionQuery) query;
                stack.push(unionQuery.getLeft());
                stack.push(unionQuery.getRight());
            }
        } while (!stack.empty());
        return groupList;
    }

    @Deprecated
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
        StatementType statementType = null;
        if (parent instanceof SQLDeleteStatement) {
            tableSource = ((SQLDeleteStatement) parent).getTableSource();
            statementType = StatementType.DELETE;
        } else if (parent instanceof SQLUpdateStatement) {
            tableSource = ((SQLUpdateStatement) parent).getTableSource();
            statementType = StatementType.UPDATE;
        } else if (parent instanceof SQLSelectQueryBlock) {
            tableSource = ((SQLSelectQueryBlock) parent).getFrom();
            statementType = StatementType.SELECT;
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

                checkJoinConditionForMultiTenant(visitor, join, false, statementType);
            } else {
                checkJoinConditionForMultiTenant(visitor, join, true, statementType);
            }
        }

        if (matchTableName == null) {
            return;
        }

        SQLBinaryOpExpr tenantCondition = createTenantCondition(visitor, alias, statementType, matchTableName);

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

    @Deprecated
    public static void checkJoinConditionForMultiTenant(WallVisitor visitor, SQLJoinTableSource join,
                                                        boolean checkLeft, StatementType statementType) {
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
                    SQLBinaryOpExpr tenantCondition = createTenantCondition(visitor, alias, statementType, tableName);

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

    @Deprecated
    private static SQLBinaryOpExpr createTenantCondition(WallVisitor visitor, String alias,
                                                         StatementType statementType, String tableName) {
        SQLExpr left, right;
        if (alias != null) {
            left = new SQLPropertyExpr(new SQLIdentifierExpr(alias), visitor.getConfig().getTenantColumn());
        } else {
            left = new SQLIdentifierExpr(visitor.getConfig().getTenantColumn());
        }
        right = generateTenantValue(visitor, alias, statementType, tableName);

        SQLBinaryOpExpr tenantCondition = new SQLBinaryOpExpr(left, SQLBinaryOperator.Equality, right);
        return tenantCondition;
    }

    private static SQLExpr generateTenantValue(WallVisitor visitor, String alias, StatementType statementType,
                                               String tableName) {
        SQLExpr value;
        TenantCallBack callBack = visitor.getConfig().getTenantCallBack();
        if (callBack != null) {
            WallProvider.setTenantValue(callBack.getTenantValue(statementType, tableName));
        }

        Object tenantValue = WallProvider.getTenantValue();
        if (tenantValue instanceof Number) {
            value = new SQLNumberExpr((Number) tenantValue);
        } else if (tenantValue instanceof String) {
            value = new SQLCharExpr((String) tenantValue);
        } else {
            throw new IllegalStateException("tenant value not support type " + tenantValue);
        }

        return value;
    }

    public static void checkReadOnly(WallVisitor visitor, SQLTableSource tableSource) {
        if (tableSource instanceof SQLExprTableSource) {
            String tableName = null;
            SQLExpr tableNameExpr = ((SQLExprTableSource) tableSource).getExpr();
            if (tableNameExpr instanceof SQLName) {
                tableName = ((SQLName) tableNameExpr).getSimpleName();
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
                context.incrementUpdateNoneConditionWarnings();
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
            checkCondition(visitor, where);

            if (Boolean.TRUE == getConditionValue(visitor, where, config.isUpdateWhereAlayTrueCheck())) {
                if (config.isUpdateWhereAlayTrueCheck() && visitor.isSqlEndOfComment()&& !isSimpleConstExpr(where)) {
                    addViolation(visitor, ErrorCode.ALWAYS_TRUE, "update alway true condition not allow", x);
                }
            }

            SQLName table = x.getTableName();
            if (table == null) {
                return;
            }

            String tableName = table.getSimpleName();
            Set<String> updateCheckColumns = config.getUpdateCheckTable(tableName);
            boolean isUpdateCheckTable = updateCheckColumns != null && !updateCheckColumns.isEmpty();

            WallUpdateCheckHandler updateCheckHandler = config.getUpdateCheckHandler();
            if (isUpdateCheckTable && updateCheckHandler != null) {
                String checkColumn = updateCheckColumns.iterator().next();

                SQLExpr valueExpr = null;
                for (SQLUpdateSetItem item : x.getItems()) {
                    if (item.columnMatch(checkColumn)) {
                        valueExpr = item.getValue();
                        break;
                    }
                }

                if (valueExpr != null) {
                    List<SQLExpr> conditions;
                    if (where instanceof SQLBinaryOpExpr) {
                        conditions = SQLBinaryOpExpr.split((SQLBinaryOpExpr) where, SQLBinaryOperator.BooleanAnd);
                    } else if (where instanceof SQLBinaryOpExprGroup) {
                        conditions = new ArrayList<SQLExpr>();
                        for (SQLExpr each : ((SQLBinaryOpExprGroup) where).getItems()) {
                            if (each instanceof SQLBinaryOpExpr) {
                                conditions.addAll(SQLBinaryOpExpr.split((SQLBinaryOpExpr) each, SQLBinaryOperator.BooleanAnd));
                            } else if (each instanceof SQLInListExpr) {
                                conditions.add(each);
                            }
                        }
                    } else {
                        conditions = new ArrayList<SQLExpr>();
                        conditions.add(where);
                    }

                    List<SQLExpr> filterValueExprList = new ArrayList<SQLExpr>();
                    for (SQLExpr condition : conditions) {
                        if (condition instanceof SQLBinaryOpExpr) {
                            SQLBinaryOpExpr binaryCondition = (SQLBinaryOpExpr) condition;
                            if (binaryCondition.getOperator() == SQLBinaryOperator.Equality
                                    && binaryCondition.conditionContainsColumn(checkColumn)) {
                                SQLExpr left = binaryCondition.getLeft();
                                SQLExpr right = binaryCondition.getRight();

                                if (left instanceof SQLValuableExpr || left instanceof SQLVariantRefExpr) {
                                    filterValueExprList.add(left);
                                } else if (right instanceof SQLValuableExpr || right instanceof SQLVariantRefExpr) {
                                    filterValueExprList.add(right);
                                }
                            }
                        } else if (condition instanceof SQLInListExpr) {
                            SQLInListExpr listExpr = (SQLInListExpr) condition;
                            if (listExpr.getExpr() instanceof SQLIdentifierExpr) {
                                SQLIdentifierExpr nameExpr = (SQLIdentifierExpr) listExpr.getExpr();
                                if (nameExpr.getName().equals(checkColumn)) {
                                    filterValueExprList.addAll(((SQLInListExpr) condition).getTargetList());
                                }
                            }
                        }
                    }

                    boolean allValue = valueExpr instanceof SQLValuableExpr;
                    if (allValue) {
                        for (SQLExpr filterValue : filterValueExprList) {
                            if (!(filterValue instanceof SQLValuableExpr)) {
                                allValue = false;
                                break;
                            }
                        }
                    }

                    if (allValue) {
                        Object setValue = ((SQLValuableExpr) valueExpr).getValue();
                        List<Object> filterValues = new ArrayList<Object>(filterValueExprList.size());
                        for (SQLExpr expr : filterValueExprList) {
                            filterValues.add(((SQLValuableExpr) expr).getValue());
                        }
                        filterValues = new ArrayList(new HashSet(filterValues));
                        boolean validate = updateCheckHandler.check(tableName, checkColumn, setValue, filterValues);
                        if (!validate) {
                            visitor.addViolation(new IllegalSQLObjectViolation(ErrorCode.UPDATE_CHECK_FAIL, "update check failed.", visitor.toSQL(x)));
                        }
                    } else {
                        visitor.addWallUpdateCheckItem(new WallUpdateCheckItem(tableName, checkColumn, valueExpr, filterValueExprList));
                    }
                    //updateCheckHandler.check(tableName, checkColumn)
                }
            }
        }

        checkUpdateForMultiTenant(visitor, x);
    }

    public static Object getValue(WallVisitor visitor, SQLBinaryOpExprGroup x) {
        List<SQLExpr> groupList = x.getItems();

        if (x.getOperator() == SQLBinaryOperator.BooleanOr) {
            return getValue_or(visitor, groupList);
        }

        if (x.getOperator() == SQLBinaryOperator.BooleanAnd) {
            return getValue_and(visitor, groupList);
        }

        return null;
    }

    public static Object getValue(WallVisitor visitor, SQLBinaryOpExpr x) {
        if (x.getOperator() == SQLBinaryOperator.BooleanOr) {
            List<SQLExpr> groupList = SQLBinaryOpExpr.split(x);

            return getValue_or(visitor, groupList);
        }

        if (x.getOperator() == SQLBinaryOperator.BooleanAnd) {
            List<SQLExpr> groupList = SQLBinaryOpExpr.split(x);
            return getValue_and(visitor, groupList);
        }
        
        boolean checkCondition = visitor != null
                                 && (!visitor.getConfig().isConstArithmeticAllow()
                                     || !visitor.getConfig().isConditionOpBitwseAllow() || !visitor.getConfig().isConditionOpXorAllow());

        if (x.getLeft() instanceof SQLName) {
            if (x.getRight() instanceof SQLName) {
                if (x.getLeft().toString().equalsIgnoreCase(x.getRight().toString())) {
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
            } else if (!checkCondition) {
                switch (x.getOperator()) {
                    case Equality:
                    case NotEqual:
                    case GreaterThan:
                    case GreaterThanOrEqual:
                    case LessThan:
                    case LessThanOrEqual:
                    case LessThanOrGreater:
                        return null;
                    default:
                        break;
                }
            }
        }

        if (x.getLeft() instanceof SQLValuableExpr && x.getRight() instanceof SQLValuableExpr) {
            Object leftValue = ((SQLValuableExpr) x.getLeft()).getValue();
            Object rightValue = ((SQLValuableExpr) x.getRight()).getValue();
            if (x.getOperator() == SQLBinaryOperator.Equality) {
                boolean evalValue = SQLEvalVisitorUtils.eq(leftValue, rightValue);
                x.putAttribute(EVAL_VALUE, evalValue);
                return evalValue;
            } else if (x.getOperator() == SQLBinaryOperator.NotEqual) {
                boolean evalValue = SQLEvalVisitorUtils.eq(leftValue, rightValue);
                x.putAttribute(EVAL_VALUE, !evalValue);
                return !evalValue;
            }
        }

        Object leftResult = getValue(visitor, x.getLeft());
        Object rightResult = getValue(visitor, x.getRight());

        if (x.getOperator() == SQLBinaryOperator.Like && leftResult instanceof String && leftResult.equals(rightResult)) {
            addViolation(visitor, ErrorCode.SAME_CONST_LIKE, "same const like", x);
        }

        if (x.getOperator() == SQLBinaryOperator.Like || x.getOperator() == SQLBinaryOperator.NotLike) {
            WallContext context = WallContext.current();
            if (context != null) {
                if (rightResult instanceof Number || leftResult instanceof Number) {
                    context.incrementLikeNumberWarnings();
                }
            }
        }

        String dbType = null;
        WallContext wallContext = WallContext.current();
        if (wallContext != null) {
            dbType = wallContext.getDbType();
        }

        return eval(visitor, dbType, x, Collections.emptyList());
    }

    private static Object getValue_or(WallVisitor visitor, List<SQLExpr> groupList) {
        boolean allFalse = true;
        for (int i = groupList.size() - 1; i >= 0; --i) {
            SQLExpr item = groupList.get(i);
            Object result = getValue(visitor, item);
            Boolean booleanVal = SQLEvalVisitorUtils.castToBoolean(result);
            if (Boolean.TRUE == booleanVal) {
                final WallConditionContext wallContext = WallVisitorUtils.getWallConditionContext();
                if (wallContext != null && !isFirst(item)) {
                    wallContext.setPartAlwayTrue(true);
                }
                return true;
            }

            if (Boolean.FALSE != booleanVal) {
                allFalse = false;
            }
        }

        if (allFalse) {
            return false;
        }

        return null;
    }

    private static Object getValue_and(WallVisitor visitor, List<SQLExpr> groupList) {
        int dalConst = 0;
        Boolean allTrue = Boolean.TRUE;
        for (int i = groupList.size() - 1; i >= 0; --i) {

            SQLExpr item = groupList.get(i);
            Object result = getValue(visitor, item);
            Boolean booleanVal = SQLEvalVisitorUtils.castToBoolean(result);

            if (Boolean.TRUE == booleanVal) {
                final WallConditionContext wallContext = WallVisitorUtils.getWallConditionContext();
                if (wallContext != null && !isFirst(item)) {
                    wallContext.setPartAlwayTrue(true);
                }
                dalConst++;
            } else if (Boolean.FALSE == booleanVal) {
                final WallConditionContext wallContext = WallVisitorUtils.getWallConditionContext();
                if (wallContext != null && !isFirst(item)) {
                    wallContext.setPartAlwayFalse(true);
                }
                allTrue = Boolean.FALSE;
                dalConst++;
            } else {
                if (allTrue != Boolean.FALSE) {
                    allTrue = null;
                }
                dalConst = 0;
            }

            if (dalConst == 2 && visitor != null && !visitor.getConfig().isConditionDoubleConstAllow()) {
                addViolation(visitor, ErrorCode.DOUBLE_CONST_CONDITION, "double const condition", item);
            }
        }

        if (Boolean.TRUE == allTrue) {
            return true;
        } else if (Boolean.FALSE == allTrue) {
            return false;
        }
        return null;
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

    public static List<SQLExpr> getParts(SQLExpr x) {
        List<SQLExpr> exprs = new ArrayList<SQLExpr>();
        exprs.add(x);

        while (true) {
            List<SQLExpr> tmp = partExpr(exprs);

            if (tmp.size() == exprs.size()) {
                break;
            }
            exprs = tmp;
        }

        return exprs;
    }

    public static List<SQLExpr> partExpr(List<SQLExpr> exprs) {
        List<SQLExpr> partList = new ArrayList<SQLExpr>();

        for (SQLExpr x : exprs) {
            if (x instanceof SQLBinaryOpExpr) {
                SQLBinaryOpExpr binary = (SQLBinaryOpExpr) x;
                if (binary.getOperator() == SQLBinaryOperator.BooleanAnd
                    || binary.getOperator() == SQLBinaryOperator.BooleanOr) {
                    partList.add(((SQLBinaryOpExpr) x).getLeft());
                    partList.add(((SQLBinaryOpExpr) x).getRight());

                    continue;
                }
            }
            partList.add(x);
        }
        return partList;
    }

    public static boolean isFirst(SQLObject x) {
        if (x == null) {
            return true;
        }

        for (;;) {
            SQLObject parent = x.getParent();
            if (!(parent instanceof SQLExpr)) {
                return true;
            }

            if (parent instanceof SQLBinaryOpExprGroup) {
                if (x != ((SQLBinaryOpExprGroup) parent).getItems().get(0)) {
                    return false;
                }
            }

            if (parent instanceof SQLBinaryOpExpr) {
                SQLBinaryOpExpr binaryExpr = (SQLBinaryOpExpr) parent;
                if (x == binaryExpr.getRight()) {
                    return false;
                }
            }
            x = parent;
        }
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

    public static boolean checkSqlExpr(SQLExpr x) { // check groupby, orderby, limit
        if (x == null) {
            return false;
        }

        SQLObject obj = x;
        for (;;) {
            SQLObject parent = obj.getParent();

            if (parent == null) {
                return false;
            }

            if (parent instanceof SQLSelectGroupByClause) {
                return true;
            } else if (parent instanceof SQLOrderBy) {
                return true;
            } else if (parent instanceof SQLLimit) {
                return true;
            } else if (parent instanceof MySqlOrderingExpr) {
                return true;
            }

            obj = parent;
        }
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

            if (parent instanceof SQLJoinTableSource) {
                SQLJoinTableSource joinTableSource = (SQLJoinTableSource) parent;
                if (joinTableSource.getCondition() == x) {
                    return true;
                }

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

        private boolean partAlwayTrue   = false;
        private boolean partAlwayFalse  = false;
        private boolean constArithmetic = false;
        private boolean xor             = false;
        private boolean bitwise         = false;

        public boolean hasPartAlwayTrue() {
            return partAlwayTrue;
        }

        public void setPartAlwayTrue(boolean partAllowTrue) {
            this.partAlwayTrue = partAllowTrue;
        }

        public boolean hasPartAlwayFalse() {
            return partAlwayFalse;
        }

        public void setPartAlwayFalse(boolean partAlwayFalse) {
            this.partAlwayFalse = partAlwayFalse;
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
                        context.incrementWarnings();
                    }
                }
            }

            if (current.hasPartAlwayTrue()
                    && !visitor.getConfig().isConditionAndAlwayTrueAllow()) {
                addViolation(visitor, ErrorCode.ALWAYS_TRUE, "part alway true condition not allow", x);
            }

            if (current.hasPartAlwayFalse()
                    && !visitor.getConfig().isConditionAndAlwayFalseAllow()) {
                addViolation(visitor, ErrorCode.ALWAYS_FALSE, "part alway false condition not allow", x);
            }

            if (current.hasConstArithmetic()
                    && !visitor.getConfig().isConstArithmeticAllow()) {
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

    public static Object getValueFromAttributes(WallVisitor visitor, SQLObject sqlObject) {
        if (sqlObject == null) {
            return null;
        }

        if (visitor != null && visitor.getConfig().isConditionLikeTrueAllow()
            && sqlObject.getAttributes().containsKey(HAS_TRUE_LIKE)) {
            return null;
        }
        return sqlObject.getAttribute(EVAL_VALUE);
    }

    public static Object getValue(SQLExpr x) {
        return getValue(null, x);
    }

    public static Object getValue(WallVisitor visitor, SQLExpr x) {
        if (x != null && x.containsAttribute(EVAL_VALUE)) {
            return getValueFromAttributes(visitor, x);
        }

        if (x instanceof SQLBinaryOpExpr) {
            return getValue(visitor, (SQLBinaryOpExpr) x);
        }

        if (x instanceof SQLBinaryOpExprGroup) {
            return getValue(visitor, (SQLBinaryOpExprGroup) x);
        }

        if (x instanceof SQLBooleanExpr) {
            return ((SQLBooleanExpr) x).getBooleanValue();
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

            if (isSimpleCaseTableSource(visitor, ((SQLQueryExpr) x).getSubQuery())) {
                SQLSelectQueryBlock queryBlock = (SQLSelectQueryBlock) ((SQLQueryExpr) x).getSubQuery().getQuery();
                SQLCaseExpr caseExpr = (SQLCaseExpr) queryBlock.getSelectList().get(0).getExpr();

                Object result = getValue(caseExpr);

                if (visitor != null && !visitor.getConfig().isCaseConditionConstAllow()) {
                    boolean leftIsName = false;
                    if (x.getParent() instanceof SQLBinaryOpExpr) {
                        SQLExpr left = ((SQLBinaryOpExpr) x.getParent()).getLeft();
                        if (left instanceof SQLName) {
                            leftIsName = true;
                        }
                    }

                    if (!leftIsName && result != null) {
                        addViolation(visitor, ErrorCode.CONST_CASE_CONDITION, "const case condition", caseExpr);
                    }
                }

                return result;
            }
        }

        String dbType = null;
        if (visitor != null) {
            dbType = visitor.getDbType();
        }

        if (x instanceof SQLMethodInvokeExpr //
            || x instanceof SQLBetweenExpr //
            || x instanceof SQLInListExpr //
            || x instanceof SQLUnaryExpr //
        ) {
            return eval(visitor, dbType, x, Collections.emptyList());
        }

        if (x instanceof SQLCaseExpr) {

            if (visitor != null && !visitor.getConfig().isCaseConditionConstAllow()) {
                SQLCaseExpr caseExpr = (SQLCaseExpr) x;

                boolean leftIsName = false;
                if (caseExpr.getParent() instanceof SQLBinaryOpExpr) {
                    SQLExpr left = ((SQLBinaryOpExpr) caseExpr.getParent()).getLeft();
                    if (left instanceof SQLName) {
                        leftIsName = true;
                    }
                }

                if (!leftIsName && caseExpr.getValueExpr() == null && caseExpr.getItems().size() > 0) {
                    SQLCaseExpr.Item item = caseExpr.getItems().get(0);
                    Object conditionVal = getValue(visitor, item.getConditionExpr());
                    Object itemVal = getValue(visitor, item.getValueExpr());
                    if (conditionVal instanceof Boolean && itemVal != null) {
                        addViolation(visitor, ErrorCode.CONST_CASE_CONDITION, "const case condition", caseExpr);
                    }
                }
            }

            return eval(visitor, dbType, x, Collections.emptyList());
        }

        return null;
    }

    public static Object eval(WallVisitor wallVisitor, String dbType, SQLObject sqlObject, List<Object> parameters) {
        SQLEvalVisitor visitor = SQLEvalVisitorUtils.createEvalVisitor(dbType);
        visitor.setParameters(parameters);
        visitor.registerFunction("rand", Nil.instance);
        visitor.registerFunction("sin", Nil.instance);
        visitor.registerFunction("cos", Nil.instance);
        visitor.registerFunction("asin", Nil.instance);
        visitor.registerFunction("acos", Nil.instance);
        sqlObject.accept(visitor);

        if (sqlObject instanceof SQLNumericLiteralExpr) {
            return ((SQLNumericLiteralExpr) sqlObject).getNumber();
        }
        return getValueFromAttributes(wallVisitor, sqlObject);
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
                    if (((SQLAggregateExpr) selectItemExpr)
                            .methodNameHashCod64() == FnvHash.Constants.COUNT) {
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

    public static boolean isSimpleCaseTableSource(WallVisitor visitor, SQLSelect select) {
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
            boolean simpleCase = false;
            if (queryBlock.getSelectList().size() == 1) {
                SQLExpr selectItemExpr = queryBlock.getSelectList().get(0).getExpr();
                if (selectItemExpr instanceof SQLCaseExpr) {
                    simpleCase = true;
                }
            }

            if (allawTrueWhere && simpleCase) {
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

        String methodName = x.getMethodName().toLowerCase();
        if (!visitor.getProvider().checkDenyTable(methodName)) {
            if (isTopStatementWithTableSource(x) || isFirstSelectTableSource(x)) {
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

        String methodName = x.getMethodName().toLowerCase();

        WallContext context = WallContext.current();
        if (context != null) {
            context.incrementFunctionInvoke(methodName);
        }

        if (!visitor.getProvider().checkDenyFunction(methodName)) {
            boolean isTopNoneFrom = isTopNoneFromSelect(visitor, x);
            if (isTopNoneFrom) {
                return;
            }

            if (isTopFromDenySchema(visitor, x)) {
                return;
            }

            boolean isShow = x.getParent() instanceof MySqlShowGrantsStatement;
            if (isShow) {
                return;
            }

            if (isWhereOrHaving(x) || checkSqlExpr(x)) {
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

    private static boolean isTopFromDenySchema(WallVisitor visitor, SQLMethodInvokeExpr x) {
        SQLObject parent = x.getParent();
        for (;; ) {
            if (parent instanceof SQLExpr || parent instanceof Item || parent instanceof SQLSelectItem) {
                parent = parent.getParent();
            } else {
                break;
            }
        }

        if (parent instanceof SQLSelectQueryBlock) {
            SQLSelectQueryBlock queryBlock = (SQLSelectQueryBlock) parent;
            if (!(queryBlock.getParent() instanceof SQLSelect)) {
                return false;
            }

            SQLSelect select = (SQLSelect) queryBlock.getParent();

            if (!(select.getParent() instanceof SQLSelectStatement)) {
                return false;
            }

            SQLSelectStatement stmt = (SQLSelectStatement) select.getParent();

            if (stmt.getParent() != null) {
                return false;
            }

            SQLTableSource from = queryBlock.getFrom();
            if (from instanceof SQLExprTableSource) {
                SQLExpr fromExpr = ((SQLExprTableSource) from).getExpr();
                if (fromExpr instanceof SQLName) {
                    String fromTableName = fromExpr.toString();
                    return visitor.isDenyTable(fromTableName);
                }
            }

            return false;
        }



        return false;
    }

    private static boolean checkSchema(WallVisitor visitor, SQLExpr x) {
        final WallTopStatementContext topStatementContext = wallTopStatementContextLocal.get();
        if (topStatementContext != null && (topStatementContext.fromSysSchema || topStatementContext.fromSysTable)) {
            return true;
        }

        if (x instanceof SQLName) {
            String owner = ((SQLName) x).getSimpleName();
            owner = WallVisitorUtils.form(owner);
            if (isInTableSource(x) && !visitor.getProvider().checkDenySchema(owner)) {

                if (!isTopStatementWithTableSource(x) && !isFirstSelectTableSource(x) && !isFirstInSubQuery(x)) {
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

    private static boolean isFirstInSubQuery(SQLObject x) {
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

        SQLSelect sqlSelect = null;
        SQLObject parent = x.getParent();
        while (parent != null) {

            if (parent instanceof SQLSelect) {
                sqlSelect = (SQLSelect) parent;
                break;
            }

            x = parent;
            parent = x.getParent();
        }

        if (sqlSelect == null) {
            return false;
        }

        parent = sqlSelect.getParent();
        if (!(parent instanceof SQLInSubQueryExpr && isFirst(parent))) {
            return false;
        }

        SQLInSubQueryExpr sqlInSubQueryExpr = (SQLInSubQueryExpr) parent;
        if (!(sqlInSubQueryExpr.getParent() instanceof SQLSelectQueryBlock)) {
            return false;
        }

        SQLSelectQueryBlock queryBlock = (SQLSelectQueryBlock) sqlInSubQueryExpr.getParent();
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
        boolean isSelectItem = false;
        do {
            x = parent;
            parent = parent.getParent();
            if (parent instanceof SQLUnionQuery) {
                SQLUnionQuery union = (SQLUnionQuery) parent;
                if (union.getRight() == x && hasTableSource(union.getLeft())) {
                    return false;
                }
            } else if (parent instanceof SQLQueryExpr || parent instanceof SQLInSubQueryExpr
                       || parent instanceof SQLExistsExpr) {
                isWhereQueryExpr = isWhereOrHaving(parent);
            } else if (parent instanceof SQLSelectItem) {
                isSelectItem = true;
            } else if ((isWhereQueryExpr || isSelectItem) && parent instanceof SQLSelectQueryBlock) {
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

    private static boolean isTopStatementWithTableSource(SQLObject x) {

        for (;;) {
            if (x instanceof SQLExpr) {
                x = x.getParent();
            } else {
                break;
            }
        }

        if (x instanceof SQLExprTableSource) {
            x = x.getParent();

            if (x instanceof SQLStatement) {
                x = x.getParent();
                if (x == null) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean isTopSelectItem(SQLObject x) {
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
        return isTopSelectStatement(item.getParent());
    }

    private static boolean isTopSelectStatement(SQLObject x) {

        if (!(x instanceof SQLSelectQueryBlock)) {
            return false;
        }

        SQLSelectQueryBlock queryBlock = (SQLSelectQueryBlock) x;
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

    public static boolean isTopSelectOutFile(MySqlOutFileExpr x) {
        if (!(x.getParent() instanceof SQLExprTableSource)) {
            return false;
        }
        SQLExprTableSource tableSource = (SQLExprTableSource) x.getParent();
        return isTopSelectStatement(tableSource.getParent());
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
            String tableName = ((SQLName) expr).getSimpleName();

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
                    } else if (parent instanceof SQLReplaceStatement) {
                        tableStat.incrementReplaceCount();
                    }
                }
            }

            if (topStatementContext != null && (topStatementContext.fromSysSchema || topStatementContext.fromSysTable)) {
                return true;
            }

            if (visitor.isDenyTable(tableName)
                && !(topStatementContext != null && topStatementContext.fromPermitTable())) {

                if (isTopStatementWithTableSource(x) || isFirstSelectTableSource(x)) {
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

        if (!WallVisitorUtils.queryBlockFromIsNull(visitor, x.getLeft())
            && WallVisitorUtils.queryBlockFromIsNull(visitor, x.getRight())) {
            boolean isTopUpdateStatement = false;
            boolean isTopInsertStatement = false;
            SQLObject selectParent = x.getParent();
            while (selectParent instanceof SQLSelectQuery //
                   || selectParent instanceof SQLJoinTableSource //
                   || selectParent instanceof SQLSubqueryTableSource //
                   || selectParent instanceof SQLSelect) {
                selectParent = selectParent.getParent();
            }

            if (selectParent instanceof SQLUpdateStatement) {
                isTopUpdateStatement = true;
            }

            if (selectParent instanceof SQLInsertStatement) {
                isTopInsertStatement = true;
            }

            if (isTopUpdateStatement || isTopInsertStatement) {
                return;
            }
            
            if (x.getLeft() instanceof SQLSelectQueryBlock) {
                SQLSelectQueryBlock left = (SQLSelectQueryBlock) x.getLeft();
                SQLTableSource tableSource = left.getFrom();
                if (left.getWhere() == null && tableSource != null && tableSource instanceof SQLExprTableSource) {
                    return;
                }
            }

            WallContext context = WallContext.current();
            if (context != null) {
                context.incrementUnionWarnings();
            }

            if (((x.getOperator() == SQLUnionOperator.UNION || x.getOperator() == SQLUnionOperator.UNION_ALL || x.getOperator() == SQLUnionOperator.DISTINCT)
                 && visitor.getConfig().isSelectUnionCheck() && visitor.isSqlEndOfComment())
                || (x.getOperator() == SQLUnionOperator.MINUS && visitor.getConfig().isSelectMinusCheck())
                || (x.getOperator() == SQLUnionOperator.INTERSECT && visitor.getConfig().isSelectIntersectCheck())
                || (x.getOperator() == SQLUnionOperator.EXCEPT && visitor.getConfig().isSelectExceptCheck())) {
                addViolation(visitor, ErrorCode.UNION,
                             x.getOperator().toString() + " query not contains 'from clause'", x);
            }
        }
    }

    public static boolean queryBlockFromIsNull(WallVisitor visitor, SQLSelectQuery query) {
        return queryBlockFromIsNull(visitor, query, true);
    }

    public static boolean queryBlockFromIsNull(WallVisitor visitor, SQLSelectQuery query, boolean checkSelectConst) {
        if (query instanceof SQLSelectQueryBlock) {
            SQLSelectQueryBlock queryBlock = (SQLSelectQueryBlock) query;
            SQLTableSource from = queryBlock.getFrom();
            
            if (queryBlock.getSelectList().size() < 1) {
                return false;
            }

            if (from == null) {
                boolean itemIsConst = true;
                boolean itemHasAlias = false;
                for (SQLSelectItem item : queryBlock.getSelectList()) {
                    if (item.getExpr() instanceof SQLIdentifierExpr || item.getExpr() instanceof SQLPropertyExpr) {
                        itemIsConst = false;
                        break;
                    }
                    if(item.getAlias() != null ) {
                        itemHasAlias = true;
                        break;
                    }
                }
                if (itemIsConst && !itemHasAlias) {
                    return true;
                } else {
                    return false;
                }
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

            if (checkSelectConst) {
                SQLExpr where = queryBlock.getWhere();
                if (where != null) {
                    Object whereValue = getValue(visitor, where);
                    if (Boolean.TRUE == whereValue) {
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
                }
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
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            if (classLoader != null) {
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
        if (x instanceof SQLCommentStatement) {
            return;
        } else if (x instanceof SQLInsertStatement) {
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
        } else if (x instanceof SQLMergeStatement) {
            allow = config.isMergeAllow();
            denyMessage = "merge not allow";
            errorCode = ErrorCode.MERGE_NOT_ALLOW;
        } else if (x instanceof SQLCallStatement
                || x instanceof SQLServerExecStatement
                || x instanceof OracleExecuteImmediateStatement) {
            allow = config.isCallAllow();
            denyMessage = "call not allow";
            errorCode = ErrorCode.CALL_NOT_ALLOW;
        } else if (x instanceof SQLTruncateStatement) {
            allow = config.isTruncateAllow();
            denyMessage = "truncate not allow";
            errorCode = ErrorCode.TRUNCATE_NOT_ALLOW;
        } else if (x instanceof SQLCreateStatement) {
            allow = config.isCreateTableAllow();
            denyMessage = "create table not allow";
            errorCode = ErrorCode.CREATE_TABLE_NOT_ALLOW;
        } else if (x instanceof SQLAlterStatement) {
            allow = config.isAlterTableAllow();
            denyMessage = "alter table not allow";
            errorCode = ErrorCode.ALTER_TABLE_NOT_ALLOW;
        } else if (x instanceof SQLDropStatement) {
            allow = config.isDropTableAllow();
            denyMessage = "drop table not allow";
            errorCode = ErrorCode.DROP_TABLE_NOT_ALLOW;
        } else if (x instanceof SQLSetStatement) {
            allow = config.isSetAllow();
            denyMessage = "set not allow";
            errorCode = ErrorCode.SET_NOT_ALLOW;
        } else if (x instanceof SQLReplaceStatement) {
            allow = config.isReplaceAllow();
            denyMessage = "replace not allow";
            errorCode = ErrorCode.REPLACE_NOT_ALLOW;
        } else if (x instanceof SQLDescribeStatement
            || (x instanceof MySqlExplainStatement && ((MySqlExplainStatement)x).isDescribe())) {
            allow = config.isDescribeAllow();
            denyMessage = "describe not allow";
            errorCode = ErrorCode.DESC_NOT_ALLOW;
        } else if (x instanceof MySqlShowStatement
                || x instanceof PGShowStatement
                || x instanceof SQLShowTablesStatement) {
            allow = config.isShowAllow();
            denyMessage = "show not allow";
            errorCode = ErrorCode.SHOW_NOT_ALLOW;
        } else if (x instanceof SQLCommitStatement) {
            allow = config.isCommitAllow();
            denyMessage = "commit not allow";
            errorCode = ErrorCode.COMMIT_NOT_ALLOW;
        } else if (x instanceof SQLRollbackStatement) {
            allow = config.isRollbackAllow();
            denyMessage = "rollback not allow";
            errorCode = ErrorCode.ROLLBACK_NOT_ALLOW;
        } else if (x instanceof SQLUseStatement) {
            allow = config.isUseAllow();
            denyMessage = "use not allow";
            errorCode = ErrorCode.USE_NOT_ALLOW;
        } else if (x instanceof MySqlRenameTableStatement) {
            allow = config.isRenameTableAllow();
            denyMessage = "rename table not allow";
            errorCode = ErrorCode.RENAME_TABLE_NOT_ALLOW;
        } else if (x instanceof MySqlHintStatement) {
            allow = config.isHintAllow();
            denyMessage = "hint not allow";
            errorCode = ErrorCode.HINT_NOT_ALLOW;
        } else if (x instanceof MySqlLockTableStatement) {
            allow = config.isLockTableAllow();
            denyMessage = "lock table not allow";
            errorCode = ErrorCode.LOCK_TABLE_NOT_ALLOW;
        } else if (x instanceof SQLStartTransactionStatement) {
            allow = config.isStartTransactionAllow();
            denyMessage = "start transaction not allow";
            errorCode = ErrorCode.START_TRANSACTION_NOT_ALLOW;
        } else if (x instanceof SQLBlockStatement) {
            allow = config.isBlockAllow();
            denyMessage = "block statement not allow";
            errorCode = ErrorCode.BLOCK_NOT_ALLOW;
        } else if (x instanceof SQLExplainStatement
                || x instanceof MySqlOptimizeStatement) {
            allow = true;
            errorCode = 0;
            denyMessage = null;
        } else {
            allow = config.isNoneBaseStatementAllow();
            errorCode = ErrorCode.NONE_BASE_STATEMENT_NOT_ALLOW;
            denyMessage = x.getClass() + " not allow";
        } 

        if (!allow) {
            addViolation(visitor, errorCode, denyMessage, x);
        }
    }
    
    public static void check(WallVisitor visitor, SQLCommentHint x) {
        if (!visitor.getConfig().isHintAllow()) {
            addViolation(visitor, ErrorCode.EVIL_HINTS, "hint not allow", x);
            return;
        }

        String text = x.getText();
        text = text.trim();
        if (text.startsWith("!")) {
            text = text.substring(1);
        }

        if (text.length() == 0) {
            return;
        }

        int pos = 0;
        for (; pos < text.length(); pos++) {
            char ch = text.charAt(pos);
            if (ch >= '0' && ch <= '9') {
                continue;
            } else {
                break;
            }
        }

        if (pos == 5) {
            text = text.substring(5);
            text = text.trim();
        }

        text = text.toUpperCase();
        
        boolean isWhite = false;
        for (String hint : whiteHints) {
            if (text.equals(hint)) {
                isWhite = true;
                break;
            }
        }

        if (!isWhite) {
            if (text.startsWith("FORCE INDEX") || text.startsWith("IGNORE INDEX")) {
                isWhite = true;
            }
        }
        
        if(!isWhite) {
            if (text.startsWith("SET")) {
                SQLStatementParser parser = new MySqlStatementParser(text);
                List<SQLStatement> statementList = parser.parseStatementList();
                if (statementList != null && statementList.size() > 0)  {
                    SQLStatement statement = statementList.get(0);
                    if (statement instanceof SQLSetStatement) {
                        isWhite = true;
                    }
                }
            }
        }

        if (!isWhite) {
            addViolation(visitor, ErrorCode.EVIL_HINTS, "hint not allow", x);
        }
    }

    public static void check(WallVisitor visitor, SQLJoinTableSource x) {
        SQLExpr condition = x.getCondition();
        if (condition instanceof SQLName) {
            addViolation(visitor, ErrorCode.INVALID_JOIN_CONDITION, "invalid join condition", x);
        }
    }
}
