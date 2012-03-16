package com.alibaba.druid.filter.wall.spi;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.alibaba.druid.filter.wall.IllegalSQLObjectViolation;
import com.alibaba.druid.filter.wall.Violation;
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
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleMergeStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleMultiInsertStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSelectQueryBlock;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSelectTableReference;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleUpdateStatement;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleASTVIsitorAdapter;

public class OracleWallVisitor extends OracleASTVIsitorAdapter implements WallVisitor {

    private final Set<String>     permitFunctions = new HashSet<String>();
    private final Set<String>     permitTables    = new HashSet<String>();
    private final Set<String>     permitSchemas   = new HashSet<String>();
    private final Set<String>     permitNames     = new HashSet<String>();
    private final Set<String>     permitObjects   = new HashSet<String>();

    private final List<Violation> violations;

    public OracleWallVisitor(){
        this(new ArrayList<Violation>());
    }

    public OracleWallVisitor(List<Violation> violations){
        this.violations = violations;

        WallVisitorUtils.loadResource(this.permitNames, "META-INF/druid-filter-wall-permit-name.txt");
        WallVisitorUtils.loadResource(this.permitSchemas, "META-INF/druid-filter-wall-permit-schema.txt");
        WallVisitorUtils.loadResource(this.permitFunctions, "META-INF/druid-filter-wall-permit-function.txt");
        WallVisitorUtils.loadResource(this.permitTables, "META-INF/druid-filter-wall-permit-table.txt");
        WallVisitorUtils.loadResource(this.permitObjects, "META-INF/druid-filter-wall-permit-object.txt");
    }

    public void addPermitName(String name) {
        name = name.toLowerCase();
        permitNames.add(name);
    }

    public void addPermitFunction(String name) {
        name = name.toLowerCase();
        permitFunctions.add(name);
    }

    public void addPermitTable(String name) {
        name = name.toLowerCase();
        permitTables.add(name);
    }

    public boolean containsPermitObjects(String name) {
        name = name.toLowerCase();
        return permitObjects.contains(name);
    }

    public Set<String> getPermitObjects() {
        return permitObjects;
    }

    public Set<String> getPermitNames() {
        return permitNames;
    }

    public Set<String> getPermitFunctions() {
        return permitFunctions;
    }

    public Set<String> getPermitTables() {
        return permitTables;
    }

    public Set<String> getPermitSchemas() {
        return permitSchemas;
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
        WallVisitorUtils.check(this, x);

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
        WallVisitorUtils.checkCondition(this, x.getHaving());
        return true;
    }

    @Override
    public boolean visit(SQLSelectQueryBlock x) {
        WallVisitorUtils.checkCondition(this, x.getWhere());

        return true;
    }

    @Override
    public boolean visit(OracleSelectQueryBlock x) {
        return visit((SQLSelectQueryBlock) x);
    }

    @Override
    public boolean visit(SQLUnionQuery x) {
        if (WallVisitorUtils.queryBlockFromIsNull(x.getLeft()) || WallVisitorUtils.queryBlockFromIsNull(x.getRight())) {
            violations.add(new IllegalSQLObjectViolation(SQLUtils.toMySqlString(x)));
        }

        return true;
    }

    @Override
    public boolean visit(OracleDeleteStatement x) {
        WallVisitorUtils.checkCondition(this, x.getWhere());
        return true;
    }

    @Override
    public boolean visit(SQLDeleteStatement x) {
        WallVisitorUtils.checkCondition(this, x.getWhere());
        return true;
    }
    
    @Override
    public boolean visit(SQLUpdateStatement x) {
        WallVisitorUtils.checkCondition(this, x.getWhere());
        return true;
    }
    
    @Override
    public boolean visit(OracleUpdateStatement x) {
        WallVisitorUtils.checkCondition(this, x.getWhere());
        return true;
    }

    @Override
    public String toSQL(SQLObject obj) {
        return SQLUtils.toOracleString(obj);
    }

    @Override
    public boolean containsPermitTable(String name) {
        name = WallVisitorUtils.form(name);
        if (name.startsWith("v$") || name.startsWith("v_$")) {
            return true;
        }
        return permitTables.contains(name);
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
}
