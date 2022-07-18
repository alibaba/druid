/*
 * Copyright 1999-2017 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.alibaba.druid.wall.spi;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.ast.expr.SQLVariantRefExpr;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.saphana.ast.statement.*;
import com.alibaba.druid.sql.dialect.saphana.visitor.SAPHanaASTVisitor;
import com.alibaba.druid.wall.WallProvider;
import com.alibaba.druid.wall.WallVisitor;
import com.alibaba.druid.wall.spi.WallVisitorUtils.WallTopStatementContext;
import com.alibaba.druid.wall.violation.ErrorCode;
import com.alibaba.druid.wall.violation.IllegalSQLObjectViolation;

/**
 * 
 * @author nukiyoam
 */
public class SAPHanaWallVisitor extends WallVisitorBase implements WallVisitor, SAPHanaASTVisitor {

    public SAPHanaWallVisitor(WallProvider provider) {
        super(provider);
    }

    @Override
    public DbType getDbType() {
        return DbType.sap_hana;
    }

    @Override
    public boolean visit(SAPHanaSelectQueryBlock x) {
        WallVisitorUtils.checkSelelct(this, x);
        return true;
    }

    @Override
    public boolean visit(SAPHanaDeleteStatement x) {
        WallVisitorUtils.checkReadOnly(this, x.getFrom());
        return visit((SQLDeleteStatement)x);
    }

    @Override
    public boolean visit(SAPHanaUpdateStatement x) {
        return visit((SQLUpdateStatement)x);
    }

    @Override
    public boolean visit(SAPHanaInsertStatement x) {
        return visit((SQLInsertStatement)x);
    }

    @Override
    public boolean visit(SQLIdentifierExpr x) {
        return true;
    }

    @Override
    public boolean visit(SQLPropertyExpr x) {
        if (x.getOwner() instanceof SQLVariantRefExpr) {
            SQLVariantRefExpr varExpr = (SQLVariantRefExpr)x.getOwner();
            SQLObject parent = x.getParent();
            String varName = varExpr.getName();
            if (varName.equalsIgnoreCase("@@session") || varName.equalsIgnoreCase("@@global")) {
                if (!(parent instanceof SQLSelectItem) && !(parent instanceof SQLAssignItem)) {
                    violations.add(new IllegalSQLObjectViolation(ErrorCode.VARIANT_DENY,
                        "variable in condition not allow", toSQL(x)));
                    return false;
                }

                if (!checkVar(x.getParent(), x.getName())) {
                    boolean isTop = WallVisitorUtils.isTopNoneFromSelect(this, x);
                    if (!isTop) {
                        boolean allow = true;
                        if (isDeny(varName)
                            && (WallVisitorUtils.isWhereOrHaving(x) || WallVisitorUtils.checkSqlExpr(varExpr))) {
                            allow = false;
                        }

                        if (!allow) {
                            violations.add(new IllegalSQLObjectViolation(ErrorCode.VARIANT_DENY,
                                "variable not allow : " + x.getName(), toSQL(x)));
                        }
                    }
                }
                return false;
            }
        }

        WallVisitorUtils.check(this, x);
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
    public boolean visit(SAPHanaCreateTableStatement x) {
        WallVisitorUtils.check(this, x);
        return true;
    }

    @Override
    public boolean visit(SQLVariantRefExpr x) {
        String varName = x.getName();
        if (varName == null) {
            return false;
        }

        if (varName.startsWith("@@") && !checkVar(x.getParent(), x.getName())) {
            final WallTopStatementContext topStatementContext = WallVisitorUtils.getWallTopStatementContext();
            if (topStatementContext != null
                && (topStatementContext.fromSysSchema() || topStatementContext.fromSysTable())) {
                return false;
            }

            boolean isTop = WallVisitorUtils.isTopNoneFromSelect(this, x);
            if (!isTop) {
                boolean allow =
                    !isDeny(varName) || (!WallVisitorUtils.isWhereOrHaving(x) && !WallVisitorUtils.checkSqlExpr(x));

                if (!allow) {
                    violations.add(new IllegalSQLObjectViolation(ErrorCode.VARIANT_DENY,
                        "variable not allow : " + x.getName(), toSQL(x)));
                }
            }
        }

        return false;
    }

    private boolean isDeny(String varName) {
        if (varName.startsWith("@@")) {
            varName = varName.substring(2);
        }

        varName = varName.toLowerCase();
        return config.getDenyVariants().contains(varName);
    }

    private boolean checkVar(SQLObject parent, String varName) {
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

        return config.getPermitVariants().contains(varName);
    }
}
