package com.alibaba.druid.wall.spi;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLInListExpr;
import com.alibaba.druid.sql.ast.expr.SQLMethodInvokeExpr;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.ast.expr.SQLVariantRefExpr;
import com.alibaba.druid.sql.ast.statement.SQLDeleteStatement;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectGroupByClause;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.ast.statement.SQLTruncateStatement;
import com.alibaba.druid.sql.ast.statement.SQLUnionQuery;
import com.alibaba.druid.sql.ast.statement.SQLUpdateStatement;
import com.alibaba.druid.sql.dialect.sqlserver.ast.expr.SQLServerObjectReferenceExpr;
import com.alibaba.druid.sql.dialect.sqlserver.visitor.SQLServerASTVisitor;
import com.alibaba.druid.sql.dialect.sqlserver.visitor.SQLServerASTVisitorAdapter;
import com.alibaba.druid.wall.Violation;
import com.alibaba.druid.wall.WallConfig;
import com.alibaba.druid.wall.WallVisitor;
import com.alibaba.druid.wall.violation.IllegalSQLObjectViolation;


public class SQLServerWallVisitor extends SQLServerASTVisitorAdapter implements WallVisitor, SQLServerASTVisitor{

    private final WallConfig      config;
    private final List<Violation> violations = new ArrayList<Violation>();
    
    /**
     * @param config
     */
    public SQLServerWallVisitor(WallConfig config) {
        this.config = config;
    }

    @Override
    public WallConfig getConfig() {
        return this.config;
    }

    @Override
    public List<Violation> getViolations() {
        return violations;
    }

    @Override
    public boolean isPermitTable(String name) {
        if(!config.isTableCheck()){
            return false;
        }
        
        name = WallVisitorUtils.form(name);
        return config.getPermitTables().contains(name);
    }

    @Override
    public String toSQL(SQLObject obj) {
        return SQLUtils.toSQLServerString(obj);
    }
    

    public boolean visit(SQLIdentifierExpr x) {
        String name = x.getName();
        name = WallVisitorUtils.form(name);
        if (config.isVariantCheck() && config.getPermitVariants().contains(name)) {
            getViolations().add(new IllegalSQLObjectViolation(toSQL(x)));
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
        WallVisitorUtils.check(this, x);
        return true;
    }

    @Override
    public boolean visit(SQLMethodInvokeExpr x) {
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
        if (!(x instanceof SQLStatement)) {
            return;
        }

        if (config.isNoneBaseStatementAllow()) {
            return;
        }

        if (x instanceof SQLInsertStatement) {

        } else if (x instanceof SQLSelectStatement) {

        } else if (x instanceof SQLDeleteStatement) {

        } else if (x instanceof SQLUpdateStatement) {
        } else if (x instanceof SQLTruncateStatement) {
            if (!config.isTruncateAllow()) {
                violations.add(new IllegalSQLObjectViolation(toSQL(x)));    
            }
        } else {
            violations.add(new IllegalSQLObjectViolation(toSQL(x)));
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
    public boolean visit(SQLInsertStatement x) {
        if (!config.isInsertAllow()) {
            this.getViolations().add(new IllegalSQLObjectViolation(this.toSQL(x)));
            return false;
        }

        return true;
    }

    @Override
    public boolean visit(SQLDeleteStatement x) {
        WallVisitorUtils.checkDelete(this, x);
        return true;
    }

    @Override
    public boolean visit(SQLUpdateStatement x) {
        WallVisitorUtils.checkUpdate(this, x);

        return true;
    }
    
    public boolean visit(SQLVariantRefExpr x) {
        String varName = x.getName();
        if (varName == null) {
            return false;
        }

        if (config.isVariantCheck() && varName.startsWith("@@")) {
            violations.add(new IllegalSQLObjectViolation(toSQL(x)));
        }

        return false;
    }
    
    @Override
    public boolean visit(SQLServerObjectReferenceExpr x) {
        if (x.getSchema() != null && config.isPermitSchema(x.getSchema())) {
            this.getViolations().add(new IllegalSQLObjectViolation(this.toSQL(x)));
        }
        if (x.getDatabase() != null && config.isPermitSchema(x.getDatabase())) {
            this.getViolations().add(new IllegalSQLObjectViolation(this.toSQL(x)));
        }
        return true;
    }
}
