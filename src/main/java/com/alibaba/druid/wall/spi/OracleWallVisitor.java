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

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.PagerUtils;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLInListExpr;
import com.alibaba.druid.sql.ast.expr.SQLMethodInvokeExpr;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleCreateTableStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleDeleteStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleInsertStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleMultiInsertStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleMultiInsertStatement.InsertIntoClause;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSelectQueryBlock;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSelectTableReference;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleUpdateStatement;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleASTVisitorAdapter;
import com.alibaba.druid.util.JdbcConstants;
import com.alibaba.druid.wall.*;
import com.alibaba.druid.wall.violation.ErrorCode;
import com.alibaba.druid.wall.violation.IllegalSQLObjectViolation;

public class OracleWallVisitor extends OracleASTVisitorAdapter implements WallVisitor {

    private final WallConfig      config;
    private final WallProvider    provider;
    private final List<Violation> violations      = new ArrayList<Violation>();
    private boolean               sqlModified     = false;
    private boolean               sqlEndOfComment = false;
    private List<WallUpdateCheckItem> updateCheckItems;

    public OracleWallVisitor(WallProvider provider){
        this.config = provider.getConfig();
        this.provider = provider;
    }

    @Override
    public String getDbType() {
        return JdbcConstants.ORACLE;
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

    public boolean visit(SQLIdentifierExpr x) {
        String name = x.getName();
        name = WallVisitorUtils.form(name);
        if (config.isVariantCheck() && config.getDenyVariants().contains(name)) {
            getViolations().add(new IllegalSQLObjectViolation(ErrorCode.VARIANT_DENY, "variable not allow : " + name,
                                                              toSQL(x)));
        }
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
        return WallVisitorUtils.check(this, x);
    }

    @Override
    public boolean visit(SQLMethodInvokeExpr x) {
        WallVisitorUtils.checkFunction(this, x);

        return true;
    }

    public boolean visit(OracleSelectTableReference x) {
        return WallVisitorUtils.check(this, x);
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
    public boolean visit(SQLSelectQueryBlock x) {
        WallVisitorUtils.checkSelelct(this, x);

        return true;
    }

    @Override
    public boolean visit(OracleSelectQueryBlock x) {
        WallVisitorUtils.checkSelelct(this, x);

        return true;
    }

    @Override
    public boolean visit(SQLUnionQuery x) {
        WallVisitorUtils.checkUnion(this, x);

        return true;
    }

    @Override
    public String toSQL(SQLObject obj) {
        return SQLUtils.toOracleString(obj);
    }

    @Override
    public boolean isDenyTable(String name) {
        if (!config.isTableCheck()) {
            return false;
        }

        name = WallVisitorUtils.form(name);
        if (name.startsWith("v$") || name.startsWith("v_$")) {
            return true;
        }
        return !this.provider.checkDenyTable(name);
    }

    public void preVisit(SQLObject x) {
        WallVisitorUtils.preVisitCheck(this, x);
    }

    @Override
    public boolean visit(SQLSelectStatement x) {
        if (!config.isSelelctAllow()) {
            this.getViolations().add(new IllegalSQLObjectViolation(ErrorCode.SELECT_NOT_ALLOW, "select not allow",
                                                                   this.toSQL(x)));
            return false;
        }
        WallVisitorUtils.initWallTopStatementContext();

        int selectLimit = config.getSelectLimit();
        if (selectLimit >= 0) {
            SQLSelect select = x.getSelect();
            PagerUtils.limit(select, getDbType(), 0, selectLimit, true);
            this.sqlModified = true;
        }

        return true;
    }

    @Override
    public void endVisit(SQLSelectStatement x) {
        WallVisitorUtils.clearWallTopStatementContext();
    }

    @Override
    public boolean visit(OracleInsertStatement x) {
        return visit((SQLInsertStatement) x);
    }

    @Override
    public boolean visit(SQLInsertStatement x) {
        WallVisitorUtils.initWallTopStatementContext();
        WallVisitorUtils.checkInsert(this, x);

        return true;
    }

    @Override
    public void endVisit(OracleInsertStatement x) {
        endVisit((SQLInsertStatement) x);
    }

    @Override
    public void endVisit(SQLInsertStatement x) {
        WallVisitorUtils.clearWallTopStatementContext();
    }

    @Override
    public boolean visit(InsertIntoClause x) {
        WallVisitorUtils.checkInsert(this, x);

        return true;
    }

    @Override
    public boolean visit(OracleMultiInsertStatement x) {
        if (!config.isInsertAllow()) {
            this.getViolations().add(new IllegalSQLObjectViolation(ErrorCode.INSERT_NOT_ALLOW, "insert not allow",
                                                                   this.toSQL(x)));
            return false;
        }
        WallVisitorUtils.initWallTopStatementContext();

        return true;
    }

    @Override
    public void endVisit(OracleMultiInsertStatement x) {
        WallVisitorUtils.clearWallTopStatementContext();
    }

    @Override
    public boolean visit(OracleDeleteStatement x) {
        return visit((SQLDeleteStatement) x);
    }

    @Override
    public boolean visit(SQLDeleteStatement x) {
        WallVisitorUtils.checkDelete(this, x);
        return true;
    }

    @Override
    public void endVisit(OracleDeleteStatement x) {
        endVisit((SQLDeleteStatement) x);
    }

    @Override
    public void endVisit(SQLDeleteStatement x) {
        WallVisitorUtils.clearWallTopStatementContext();
    }

    @Override
    public boolean visit(OracleUpdateStatement x) {
        return visit((SQLUpdateStatement) x);
    }

    @Override
    public boolean visit(SQLUpdateStatement x) {
        WallVisitorUtils.initWallTopStatementContext();
        WallVisitorUtils.checkUpdate(this, x);

        return true;
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
    public boolean visit(OracleCreateTableStatement x) {
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

    @Override
    public boolean visit(SQLCreateTriggerStatement x) {
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

    public boolean visit(SQLJoinTableSource x) {
        WallVisitorUtils.check(this, x);
        return true;
    }
}
