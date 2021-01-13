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
package com.alibaba.druid.wall;

import java.util.List;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.PagerUtils;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.*;
import com.alibaba.druid.sql.ast.expr.*;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.sqlserver.ast.SQLServerSelectQueryBlock;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;
import com.alibaba.druid.wall.spi.WallVisitorUtils;
import com.alibaba.druid.wall.violation.ErrorCode;
import com.alibaba.druid.wall.violation.IllegalSQLObjectViolation;

public interface WallVisitor extends SQLASTVisitor {

    WallConfig getConfig();

    WallProvider getProvider();

    List<Violation> getViolations();

    void addViolation(Violation violation);

    boolean isDenyTable(String name);

    default String toSQL(SQLObject obj) {
        return SQLUtils.toSQLString(obj, getDbType());
    }

    boolean isSqlModified();

    void setSqlModified(boolean sqlModified);

    DbType getDbType();
    
    boolean isSqlEndOfComment();

    void setSqlEndOfComment(boolean sqlEndOfComment);

    void addWallUpdateCheckItem(WallUpdateCheckItem item);

    List<WallUpdateCheckItem> getUpdateCheckItems();

    default boolean visit(SQLPropertyExpr x) {
        WallVisitorUtils.check(this, x);
        return true;
    }

    default boolean visit(SQLInListExpr x) {
        WallVisitorUtils.check(this, x);
        return true;
    }

    default boolean visit(SQLBinaryOpExpr x) {
        return WallVisitorUtils.check(this, x);
    }

    default boolean visit(SQLMethodInvokeExpr x) {
        WallVisitorUtils.checkFunction(this, x);
        return true;
    }

    default boolean visit(SQLSelectQueryBlock x) {
        WallVisitorUtils.checkSelelct(this, x);

        return true;
    }

    default boolean visit(SQLSelectGroupByClause x) {
        WallVisitorUtils.checkHaving(this, x.getHaving());
        return true;
    }

    @Override
    default boolean visit(SQLSelectItem x) {
        WallVisitorUtils.check(this, x);
        return true;
    }

    default boolean visit(SQLJoinTableSource x) {
        WallVisitorUtils.check(this, x);
        return true;
    }

    default boolean visit(SQLCreateTableStatement x) {
        WallVisitorUtils.check(this, x);
        return true;
    }

    default boolean visit(SQLAlterTableStatement x) {
        WallVisitorUtils.check(this, x);
        return true;
    }

    default boolean visit(SQLDropTableStatement x) {
        WallVisitorUtils.check(this, x);
        return true;
    }

    default boolean visit(SQLUpdateStatement x) {
        WallVisitorUtils.initWallTopStatementContext();
        WallVisitorUtils.checkUpdate(this, x);

        return true;
    }

    default void endVisit(SQLUpdateStatement x) {
        WallVisitorUtils.clearWallTopStatementContext();
    }

    default boolean visit(SQLInsertStatement x) {
        WallVisitorUtils.initWallTopStatementContext();
        WallVisitorUtils.checkInsert(this, x);

        return true;
    }

    default void endVisit(SQLInsertStatement x) {
        WallVisitorUtils.clearWallTopStatementContext();
    }

    default boolean visit(SQLDeleteStatement x) {
        WallVisitorUtils.checkDelete(this, x);
        return true;
    }

    default void preVisit(SQLObject x) {
        WallVisitorUtils.preVisitCheck(this, x);
    }

    @Override
    default boolean visit(SQLSelectStatement x) {
        WallConfig config = getConfig();
        if (!config.isSelectAllow()) {
            this.getViolations()
                    .add(new IllegalSQLObjectViolation(ErrorCode.SELECT_NOT_ALLOW, "select not allow",
                    this.toSQL(x)));
            return false;
        }

        WallVisitorUtils.initWallTopStatementContext();

        int selectLimit = config.getSelectLimit();
        if (selectLimit >= 0) {
            SQLSelect select = x.getSelect();
            PagerUtils.limit(select, getDbType(), 0, selectLimit, true);
            setSqlModified(true);
        }
        return true;
    }

    default void endVisit(SQLSelectStatement x) {
        WallVisitorUtils.clearWallTopStatementContext();
    }

    default boolean visit(SQLExprTableSource x) {
        WallVisitorUtils.check(this, x);

        if (x.getExpr() instanceof SQLName) {
            return false;
        }

        return true;
    }

    default boolean visit(SQLIdentifierExpr x) {
        WallConfig config = getConfig();
        String name = x.getName();
        name = WallVisitorUtils.form(name);
        if (config.isVariantCheck() && config.getDenyVariants().contains(name)) {
            getViolations().add(new IllegalSQLObjectViolation(ErrorCode.VARIANT_DENY, "variable not allow : " + name,
                    toSQL(x)));
        }
        return true;
    }

    default boolean visit(SQLUnionQuery x) {
        return WallVisitorUtils.checkUnion(this, x);
    }

    default void endVisit(SQLDeleteStatement x) {
        WallVisitorUtils.clearWallTopStatementContext();
    }

    default boolean visit(SQLLimit x) {
        if (x.getRowCount() instanceof SQLNumericLiteralExpr) {
            WallContext context = WallContext.current();

            int rowCount = ((SQLNumericLiteralExpr) x.getRowCount()).getNumber().intValue();
            if (rowCount == 0) {
                if (context != null) {
                    context.incrementWarnings();
                }

                if (!getProvider().getConfig().isLimitZeroAllow()) {
                    this.getViolations().add(new IllegalSQLObjectViolation(ErrorCode.LIMIT_ZERO, "limit row 0",
                            this.toSQL(x)));
                }
            }
        }
        return true;
    }

    default boolean visit(SQLCreateTriggerStatement x) {
        return false;
    }

    default boolean visit(SQLSetStatement x) {
        return false;
    }

    default boolean visit(SQLCallStatement x) {
        return false;
    }

    default boolean visit(SQLCommentHint x) {
        if (x instanceof TDDLHint) {
            return false;
        }
        WallVisitorUtils.check(this, x);
        return true;
    }

    default boolean visit(SQLShowCreateTableStatement x) {
        String tableName = (x.getName()).getSimpleName();
        WallContext context = WallContext.current();
        if (context != null) {
            WallSqlTableStat tableStat = context.getTableStat(tableName);
            if (tableStat != null) {
                tableStat.incrementShowCount();
            }
        }
        return false;
    }
}
