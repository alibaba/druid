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

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLInListExpr;
import com.alibaba.druid.sql.ast.expr.SQLMethodInvokeExpr;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.ast.expr.SQLVariantRefExpr;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLCallStatement;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLDeleteStatement;
import com.alibaba.druid.sql.ast.statement.SQLDropTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectGroupByClause;
import com.alibaba.druid.sql.ast.statement.SQLSelectItem;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.ast.statement.SQLSetStatement;
import com.alibaba.druid.sql.ast.statement.SQLUnionQuery;
import com.alibaba.druid.sql.ast.statement.SQLUpdateStatement;
import com.alibaba.druid.sql.dialect.sqlserver.ast.SQLServerSelectQueryBlock;
import com.alibaba.druid.sql.dialect.sqlserver.ast.expr.SQLServerObjectReferenceExpr;
import com.alibaba.druid.sql.dialect.sqlserver.ast.stmt.SQLServerInsertStatement;
import com.alibaba.druid.sql.dialect.sqlserver.visitor.SQLServerASTVisitor;
import com.alibaba.druid.sql.dialect.sqlserver.visitor.SQLServerASTVisitorAdapter;
import com.alibaba.druid.wall.Violation;
import com.alibaba.druid.wall.WallConfig;
import com.alibaba.druid.wall.WallProvider;
import com.alibaba.druid.wall.WallVisitor;
import com.alibaba.druid.wall.violation.ErrorCode;
import com.alibaba.druid.wall.violation.IllegalSQLObjectViolation;

public class SQLServerWallVisitor extends SQLServerASTVisitorAdapter implements WallVisitor, SQLServerASTVisitor {

    private final WallConfig      config;
    private final WallProvider    provider;
    private final List<Violation> violations = new ArrayList<Violation>();

    public SQLServerWallVisitor(WallProvider provider){
        this.config = provider.getConfig();
        this.provider = provider;
    }

    public WallProvider getProvider() {
        return provider;
    }

    @Override
    public WallConfig getConfig() {
        return this.config;
    }

    public void addViolation(Violation violation) {
        this.violations.add(violation);
    }

    @Override
    public List<Violation> getViolations() {
        return violations;
    }

    @Override
    public boolean isDenyTable(String name) {
        if (!config.isTableCheck()) {
            return false;
        }

        return !this.provider.checkDenyTable(name);
    }

    @Override
    public String toSQL(SQLObject obj) {
        return SQLUtils.toSQLServerString(obj);
    }

    public boolean visit(SQLIdentifierExpr x) {
        // String name = x.getName();
        // name = WallVisitorUtils.form(name);
        // if (config.isVariantCheck() && config.getDenyVariants().contains(name)) {
        // getViolations().add(new IllegalSQLObjectViolation(ErrorCode.VARIANT_DENY, "variable not allow : " + name,
        // toSQL(x)));
        // }
        return true;
    }

    public boolean visit(SQLPropertyExpr x) {
        WallVisitorUtils.check(this, x);
        return true;
    }

    public boolean visit(SQLInListExpr x) {
        WallVisitorUtils.check(this, x);
        return true;
    }

    public boolean visit(SQLBinaryOpExpr x) {
        WallVisitorUtils.check(this, x);
        return true;
    }

    @Override
    public boolean visit(SQLMethodInvokeExpr x) {

        if (x.getParent() instanceof SQLExprTableSource) {
            WallVisitorUtils.checkFunctionInTableSource(this, x);
        }

        WallVisitorUtils.checkFunction(this, x);

        return true;
    }

    public boolean visit(SQLExprTableSource x) {
        WallVisitorUtils.check(this, x);

        if (x.getExpr() instanceof SQLName) {
            return false;
        }

        return true;
    }

    public boolean visit(SQLSelectGroupByClause x) {
        WallVisitorUtils.checkHaving(this, x.getHaving());
        return true;
    }

    @Override
    public boolean visit(SQLServerSelectQueryBlock x) {
        WallVisitorUtils.checkSelelct(this, x);

        return true;
    }

    @Override
    public boolean visit(SQLSelectQueryBlock x) {
        WallVisitorUtils.checkSelelct(this, x);

        return true;
    }

    @Override
    public boolean visit(SQLUnionQuery x) {
        WallVisitorUtils.checkUnion(this, x);

        return true;
    }

    public void preVisit(SQLObject x) {
        WallVisitorUtils.preVisitCheck(this, x);
    }

    @Override
    public boolean visit(SQLSelectStatement x) {
        if (!config.isSelelctAllow()) {
            this.getViolations().add(new IllegalSQLObjectViolation(ErrorCode.SELECT_NOT_ALLOW, "selelct not allow",
                                                                   this.toSQL(x)));
            return false;
        }
        WallVisitorUtils.initWallTopStatementContext();

        return true;
    }

    @Override
    public void endVisit(SQLSelectStatement x) {
        WallVisitorUtils.clearWallTopStatementContext();
    }

    @Override
    public boolean visit(SQLInsertStatement x) {
        WallVisitorUtils.initWallTopStatementContext();
        WallVisitorUtils.checkInsert(this, x);
        return true;
    }

    @Override
    public void endVisit(SQLInsertStatement x) {
        WallVisitorUtils.clearWallTopStatementContext();
    }

    @Override
    public boolean visit(SQLDeleteStatement x) {
        WallVisitorUtils.checkDelete(this, x);
        return true;
    }

    @Override
    public boolean visit(SQLUpdateStatement x) {
        WallVisitorUtils.initWallTopStatementContext();
        WallVisitorUtils.checkUpdate(this, x);

        return true;
    }

    @Override
    public void endVisit(SQLUpdateStatement x) {
        WallVisitorUtils.clearWallTopStatementContext();
    }

    public boolean visit(SQLVariantRefExpr x) {
        String varName = x.getName();
        if (varName == null) {
            return false;
        }

        if (config.isVariantCheck() && varName.startsWith("@@")) {

            boolean allow = true;
            if (WallVisitorUtils.isWhereOrHaving(x) && isDeny(varName)) {
                allow = false;
            }

            if (!allow) {
                violations.add(new IllegalSQLObjectViolation(ErrorCode.VARIANT_DENY, "variable not allow : "
                                                                                     + x.getName(), toSQL(x)));
            }
        }

        return false;
    }

    public boolean isDeny(String varName) {
        if (varName.startsWith("@@")) {
            varName = varName.substring(2);
        }

        return config.getDenyVariants().contains(varName);
    }

    @Override
    public boolean visit(SQLServerObjectReferenceExpr x) {
        if (x.getSchema() != null && !provider.checkDenySchema(x.getSchema())) {
            this.getViolations().add(new IllegalSQLObjectViolation(ErrorCode.SCHEMA_DENY, "schema not allow : "
                                                                                          + x.getSchema(),
                                                                   this.toSQL(x)));
        }
        if (x.getDatabase() != null && !provider.checkDenySchema(x.getDatabase())) {
            this.getViolations().add(new IllegalSQLObjectViolation(ErrorCode.SCHEMA_DENY, "schema not allow : "
                                                                                          + x.getDatabase(),
                                                                   this.toSQL(x)));
        }
        return true;
    }

    @Override
    public boolean visit(SQLServerInsertStatement x) {
        return this.visit((SQLInsertStatement) x);
    }

    @Override
    public void endVisit(SQLServerInsertStatement x) {
        this.endVisit((SQLInsertStatement) x);
    }

    @Override
    public boolean visit(SQLSelectItem x) {
        WallVisitorUtils.check(this, x);
        return true;
    }

    @Override
    public boolean visit(SQLCreateTableStatement x) {
        WallVisitorUtils.check(this, x);
        return true;
    }

    @Override
    public boolean visit(SQLAlterTableStatement x) {
        WallVisitorUtils.check(this, x);
        return true;
    }

    @Override
    public boolean visit(SQLDropTableStatement x) {
        WallVisitorUtils.check(this, x);
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
}
