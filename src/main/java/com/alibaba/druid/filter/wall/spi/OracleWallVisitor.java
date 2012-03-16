package com.alibaba.druid.filter.wall.spi;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.filter.wall.IllegalSQLObjectViolation;
import com.alibaba.druid.filter.wall.Violation;
import com.alibaba.druid.filter.wall.WallConfig;
import com.alibaba.druid.filter.wall.WallVisitor;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLMethodInvokeExpr;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.ast.statement.SQLDeleteStatement;
import com.alibaba.druid.sql.ast.statement.SQLDropTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectGroupByClause;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.ast.statement.SQLUnionQuery;
import com.alibaba.druid.sql.ast.statement.SQLUpdateStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleDeleteStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleInsertStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleMergeStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleMultiInsertStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSelectQueryBlock;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSelectTableReference;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleUpdateStatement;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleASTVIsitorAdapter;

public class OracleWallVisitor extends OracleASTVIsitorAdapter implements WallVisitor {

    private final WallConfig      config;
    private final List<Violation> violations = new ArrayList<Violation>();

    public OracleWallVisitor(WallConfig config){
        this.config = config;
    }

    public WallConfig getConfig() {
        return config;
    }

    public List<Violation> getViolations() {
        return violations;
    }

    public boolean visit(SQLIdentifierExpr x) {

        return true;
    }

    public boolean visit(SQLPropertyExpr x) {
        WallVisitorUtils.check(this, x);
        return true;
    }

    // executeQuery
    public boolean visit(SQLBinaryOpExpr x) {
        return true;
    }

    public boolean visit(SQLDropTableStatement x) {
        violations.add(new IllegalSQLObjectViolation(SQLUtils.toOracleString(x)));
        return false;
    }

    @Override
    public boolean visit(SQLMethodInvokeExpr x) {
        WallVisitorUtils.checkFunction(this, x);

        return true;
    }

    public boolean visit(OracleSelectTableReference x) {
        WallVisitorUtils.check(this, x);
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
    public boolean visit(SQLSelectQueryBlock x) {
        WallVisitorUtils.checkSelelctCondition(this, x.getWhere());

        return true;
    }

    @Override
    public boolean visit(OracleSelectQueryBlock x) {
        WallVisitorUtils.checkSelelctCondition(this, x.getWhere());

        return true;
    }

    @Override
    public boolean visit(SQLUnionQuery x) {
        if (WallVisitorUtils.queryBlockFromIsNull(x.getLeft()) || WallVisitorUtils.queryBlockFromIsNull(x.getRight())) {
            violations.add(new IllegalSQLObjectViolation(SQLUtils.toMySqlString(x)));
        }

        return true;
    }

    @Override
    public String toSQL(SQLObject obj) {
        return SQLUtils.toOracleString(obj);
    }

    @Override
    public boolean containsPermitTable(String name) {
        if (!config.isTableCheck()) {
            return false;
        }

        name = WallVisitorUtils.form(name);
        if (name.startsWith("v$") || name.startsWith("v_$")) {
            return true;
        }
        return config.getPermitTables().contains(name);
    }

    public void preVisit(SQLObject x) {
        if (!(x instanceof SQLStatement)) {
            return;
        }

        if (x instanceof SQLInsertStatement) {

        } else if (x instanceof SQLSelectStatement) {

        } else if (x instanceof SQLDeleteStatement) {

        } else if (x instanceof SQLUpdateStatement) {
        } else if (x instanceof OracleMultiInsertStatement) {

        } else if (x instanceof OracleMergeStatement) {
        } else {
            violations.add(new IllegalSQLObjectViolation(SQLUtils.toMySqlString(x)));
        }
    }

    @Override
    public boolean visit(SQLSelectStatement x) {
        if (!config.isSelelctAllow()) {
            this.getViolations().add(new IllegalSQLObjectViolation(this.toSQL(x)));
            return false;
        }

        return true;
    }

    @Override
    public boolean visit(OracleInsertStatement x) {
        return visit((SQLInsertStatement) x);
    }

    @Override
    public boolean visit(SQLInsertStatement x) {
        if (!config.isInsertAllow()) {
            this.getViolations().add(new IllegalSQLObjectViolation(this.toSQL(x)));
            return false;
        }

        return true;
    }

    @Override
    public boolean visit(OracleMultiInsertStatement x) {
        if (!config.isInsertAllow()) {
            this.getViolations().add(new IllegalSQLObjectViolation(this.toSQL(x)));
            return false;
        }

        return true;
    }

    @Override
    public boolean visit(OracleDeleteStatement x) {
        return visit((SQLDeleteStatement) x);
    }

    @Override
    public boolean visit(SQLDeleteStatement x) {
        if (!config.isDeleteAllow()) {
            this.getViolations().add(new IllegalSQLObjectViolation(this.toSQL(x)));
            return false;
        }

        WallVisitorUtils.checkDeleteCondition(this, x.getWhere());
        return true;
    }

    @Override
    public boolean visit(OracleUpdateStatement x) {
        return visit((SQLUpdateStatement) x);
    }

    @Override
    public boolean visit(SQLUpdateStatement x) {
        if (!config.isUpdateAllow()) {
            this.getViolations().add(new IllegalSQLObjectViolation(this.toSQL(x)));
            return false;
        }

        WallVisitorUtils.checkUpdateCondition(this, x.getWhere());

        return true;
    }
}
