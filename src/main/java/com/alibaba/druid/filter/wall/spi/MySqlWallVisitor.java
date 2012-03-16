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
import com.alibaba.druid.sql.ast.expr.SQLMethodInvokeExpr;
import com.alibaba.druid.sql.ast.expr.SQLNumericLiteralExpr;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.ast.expr.SQLVariantRefExpr;
import com.alibaba.druid.sql.ast.statement.SQLDeleteStatement;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectGroupByClause;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.ast.statement.SQLUnionQuery;
import com.alibaba.druid.sql.ast.statement.SQLUpdateStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.expr.MySqlOutFileExpr;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlDeleteStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSelectGroupBy;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSelectQueryBlock;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSelectQueryBlock.Limit;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitor;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitorAdapter;

public class MySqlWallVisitor extends MySqlASTVisitorAdapter implements WallVisitor, MySqlASTVisitor {

    private final Set<String>     permitFunctions = new HashSet<String>();
    private final Set<String>     permitTables    = new HashSet<String>();
    private final Set<String>     permitSchemas   = new HashSet<String>();
    private final Set<String>     permitNames     = new HashSet<String>();
    private final Set<String>     permitObjects   = new HashSet<String>();

    private final List<Violation> violations;

    public MySqlWallVisitor(){
        this(new ArrayList<Violation>(), true);
    }

    public MySqlWallVisitor(boolean loadDefault){
        this(new ArrayList<Violation>(), loadDefault);
    }

    public MySqlWallVisitor(List<Violation> violations, boolean loadDefault){
        this.violations = violations;

        if (loadDefault) {
            loadDefault();
        }
        
        WallVisitorUtils.loadResource(this.permitNames, "META-INF/druid-filter-wall-permit-name-mysql.txt");
        WallVisitorUtils.loadResource(this.permitSchemas, "META-INF/druid-filter-wall-permit-schema-mysql.txt");
        WallVisitorUtils.loadResource(this.permitFunctions, "META-INF/druid-filter-wall-permit-function-mysql.txt");
        WallVisitorUtils.loadResource(this.permitTables, "META-INF/druid-filter-wall-permit-table-mysql.txt");
        WallVisitorUtils.loadResource(this.permitObjects, "META-INF/druid-filter-wall-permit-object-mysql.txt");
    }

    public void loadDefault() {
        WallVisitorUtils.loadResource(this.permitNames, "META-INF/druid-filter-wall-permit-name-mysql-default.txt");
        WallVisitorUtils.loadResource(this.permitSchemas, "META-INF/druid-filter-wall-permit-schema-mysql-default.txt");
        WallVisitorUtils.loadResource(this.permitFunctions, "META-INF/druid-filter-wall-permit-function-mysql-default.txt");
        WallVisitorUtils.loadResource(this.permitTables, "META-INF/druid-filter-wall-permit-table-mysql-default.txt");
        WallVisitorUtils.loadResource(this.permitObjects, "META-INF/druid-filter-wall-permit-object-mysql-default.txt");
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

    public boolean containsPermitObjects(String name) {
        name = name.toLowerCase();
        return permitObjects.contains(name);
    }

    public Set<String> getPermitObjects() {
        return permitObjects;
    }

    public List<Violation> getViolations() {
        return violations;
    }

    public boolean visit(SQLPropertyExpr x) {
        WallVisitorUtils.check(this, x);
        return true;
    }

    public boolean visit(SQLBinaryOpExpr x) {
        return true;
    }

    @Override
    public boolean visit(SQLSelectQueryBlock x) {
        WallVisitorUtils.checkCondition(this, x.getWhere());

        return true;
    }

    public boolean visit(SQLSelectGroupByClause x) {
        WallVisitorUtils.checkCondition(this, x.getHaving());
        return true;
    }

    public boolean visit(MySqlSelectGroupBy x) {
        WallVisitorUtils.checkCondition(this, x.getHaving());
        return true;
    }

    @Override
    public boolean visit(MySqlSelectQueryBlock x) {
        WallVisitorUtils.checkCondition(this, x.getWhere());
        return true;
    }

    @Override
    public boolean visit(MySqlDeleteStatement x) {
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
    public boolean visit(Limit x) {
        if (x.getRowCount() instanceof SQLNumericLiteralExpr) {
            int rowCount = ((SQLNumericLiteralExpr) x.getRowCount()).getNumber().intValue();
            if (rowCount == 0) {
                this.getViolations().add(new IllegalSQLObjectViolation(this.toSQL(x)));
            }
        }
        return true;
    }

    public boolean visit(SQLVariantRefExpr x) {
        String varName = x.getName();
        if (varName == null) {
            return false;
        }

        if (varName.startsWith("@@")) {
            violations.add(new IllegalSQLObjectViolation(SQLUtils.toMySqlString(x)));
        }

        return false;
    }

    @Override
    public boolean visit(SQLMethodInvokeExpr x) {
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

    @Override
    public boolean visit(MySqlOutFileExpr x) {
        violations.add(new IllegalSQLObjectViolation(SQLUtils.toMySqlString(x)));
        return false;
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
        return SQLUtils.toMySqlString(obj);
    }

    @Override
    public boolean containsPermitTable(String name) {
        name = WallVisitorUtils.form(name);
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

        } else {
            violations.add(new IllegalSQLObjectViolation(SQLUtils.toMySqlString(x)));
        }
    }
}
