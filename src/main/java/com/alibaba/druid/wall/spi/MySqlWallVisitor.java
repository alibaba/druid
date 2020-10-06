/*
 * Copyright 1999-2017 Alibaba Group Holding Ltd.
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

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.*;
import com.alibaba.druid.sql.ast.expr.*;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.mysql.ast.expr.MySqlOutFileExpr;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.*;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitor;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitorAdapter;
import com.alibaba.druid.wall.*;
import com.alibaba.druid.wall.spi.WallVisitorUtils.WallTopStatementContext;
import com.alibaba.druid.wall.violation.ErrorCode;
import com.alibaba.druid.wall.violation.IllegalSQLObjectViolation;

import java.util.ArrayList;
import java.util.List;

public class MySqlWallVisitor extends MySqlASTVisitorAdapter implements WallVisitor, MySqlASTVisitor {

    private final WallConfig      config;
    private final WallProvider provider;
    private final List<Violation> violations      = new ArrayList<Violation>();
    private boolean               sqlModified     = false;
    private boolean               sqlEndOfComment = false;
    private List<WallUpdateCheckItem> updateCheckItems;

    public MySqlWallVisitor(WallProvider provider){
        this.config = provider.getConfig();
        this.provider = provider;
    }

    @Override
    public DbType getDbType() {
        return DbType.mysql;
    }

    @Override
    public boolean isSqlModified() {
        return sqlModified;
    }

    @Override
    public void setSqlModified(boolean sqlModified) {
        this.sqlModified = sqlModified;
    }

    @Override
    public WallProvider getProvider() {
        return provider;
    }

    @Override
    public WallConfig getConfig() {
        return config;
    }

    @Override
    public void addViolation(Violation violation) {
        this.violations.add(violation);
    }

    @Override
    public List<Violation> getViolations() {
        return violations;
    }

    @Override
    public boolean visit(MySqlSelectQueryBlock x) {
        com.alibaba.druid.wall.spi.WallVisitorUtils.checkSelelct(this, x);
        return true;
    }

    @Override
    public boolean visit(MySqlDeleteStatement x) {
        WallVisitorUtils.checkReadOnly(this, x.getFrom());
        return visit((SQLDeleteStatement) x);
    }

    @Override
    public boolean visit(MySqlUpdateStatement x) {
        return visit((SQLUpdateStatement) x);
    }

    @Override
    public boolean visit(MySqlInsertStatement x) {
        return visit((SQLInsertStatement) x);
    }

    public boolean visit(SQLPropertyExpr x) {
        if (x.getOwner() instanceof SQLVariantRefExpr) {
            SQLVariantRefExpr varExpr = (SQLVariantRefExpr) x.getOwner();
            SQLObject parent = x.getParent();
            String varName = varExpr.getName();
            if (varName.equalsIgnoreCase("@@session") || varName.equalsIgnoreCase("@@global")) {
                if (!(parent instanceof SQLSelectItem) && !(parent instanceof SQLAssignItem)) {
                    violations.add(new IllegalSQLObjectViolation(ErrorCode.VARIANT_DENY,
                                                                 "variable in condition not allow", toSQL(x)));
                    return false;
                }

                if (!checkVar(x.getParent(), x.getName())) {
                    boolean isTop = com.alibaba.druid.wall.spi.WallVisitorUtils.isTopNoneFromSelect(this, x);
                    if (!isTop) {
                        boolean allow = true;
                        if (isDeny(varName)
                            && (com.alibaba.druid.wall.spi.WallVisitorUtils.isWhereOrHaving(x) || com.alibaba.druid.wall.spi.WallVisitorUtils.checkSqlExpr(varExpr))) {
                            allow = false;
                        }

                        if (!allow) {
                            violations.add(new IllegalSQLObjectViolation(ErrorCode.VARIANT_DENY,
                                                                         "variable not allow : " + x.getName(),
                                                                         toSQL(x)));
                        }
                    }
                }
                return false;
            }
        }

        com.alibaba.druid.wall.spi.WallVisitorUtils.check(this, x);
        return true;
    }

    public boolean checkVar(SQLObject parent, String varName) {
        if (varName == null) {
            return false;
        }

        if (varName.equals("?")) {
            return true;
        }

        if (!config.isVariantCheck()) {
            return true;
        }

        if (varName.startsWith("@@")) {
            if (!(parent instanceof SQLSelectItem) && !(parent instanceof SQLAssignItem)) {
                return false;
            }

            varName = varName.substring(2);
        }

        if (config.getPermitVariants().contains(varName)) {
            return true;
        }

        return false;
    }

    public boolean isDeny(String varName) {
        if (varName.startsWith("@@")) {
            varName = varName.substring(2);
        }

        varName = varName.toLowerCase();
        return config.getDenyVariants().contains(varName);
    }

    public boolean visit(SQLVariantRefExpr x) {
        String varName = x.getName();
        if (varName == null) {
            return false;
        }

        if (varName.startsWith("@@") && !checkVar(x.getParent(), x.getName())) {

            final WallTopStatementContext topStatementContext = com.alibaba.druid.wall.spi.WallVisitorUtils.getWallTopStatementContext();
            if (topStatementContext != null
                && (topStatementContext.fromSysSchema() || topStatementContext.fromSysTable())) {
                return false;
            }

            boolean isTop = com.alibaba.druid.wall.spi.WallVisitorUtils.isTopNoneFromSelect(this, x);
            if (!isTop) {
                boolean allow = true;
                if (isDeny(varName) && (com.alibaba.druid.wall.spi.WallVisitorUtils.isWhereOrHaving(x) || com.alibaba.druid.wall.spi.WallVisitorUtils.checkSqlExpr(x))) {
                    allow = false;
                }

                if (!allow) {
                    violations.add(new IllegalSQLObjectViolation(ErrorCode.VARIANT_DENY, "variable not allow : "
                                                                                         + x.getName(), toSQL(x)));
                }
            }
        }

        return false;
    }

    @Override
    public boolean visit(MySqlOutFileExpr x) {
        if (!config.isSelectIntoOutfileAllow() && !com.alibaba.druid.wall.spi.WallVisitorUtils.isTopSelectOutFile(x)) {
            violations.add(new IllegalSQLObjectViolation(ErrorCode.INTO_OUTFILE, "into out file not allow", toSQL(x)));
        }

        return true;
    }

    @Override
    public boolean isDenyTable(String name) {
        if (!config.isTableCheck()) {
            return false;
        }

        return !this.provider.checkDenyTable(name);
    }

    @Override
    public boolean visit(MySqlCreateTableStatement x) {
        com.alibaba.druid.wall.spi.WallVisitorUtils.check(this, x);
        return true;
    }

    @Override
    public boolean visit(SQLSetStatement x) {
        return false;
    }

    @Override
    public boolean visit(SQLCallStatement x) {
        return false;
    }

    @Override
    public boolean visit(SQLCommentHint x) {
        if (x instanceof TDDLHint) {
            return false;
        }
        com.alibaba.druid.wall.spi.WallVisitorUtils.check(this, x);
        return true;
    }

    @Override
    public boolean visit(SQLShowCreateTableStatement x) {
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

    @Override
    public boolean isSqlEndOfComment() {
        return this.sqlEndOfComment;
    }

    @Override
    public void setSqlEndOfComment(boolean sqlEndOfComment) {
        this.sqlEndOfComment = sqlEndOfComment;
    }

    public void addWallUpdateCheckItem(WallUpdateCheckItem item) {
        if (updateCheckItems == null) {
            updateCheckItems = new ArrayList<WallUpdateCheckItem>();
        }
        updateCheckItems.add(item);
    }

    public List<WallUpdateCheckItem> getUpdateCheckItems() {
        return updateCheckItems;
    }
}
