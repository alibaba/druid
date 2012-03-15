package com.alibaba.druid.filter.wall.spi;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.alibaba.druid.filter.wall.IllegalSQLObjectViolation;
import com.alibaba.druid.filter.wall.Violation;
import com.alibaba.druid.filter.wall.WallVisitor;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLMethodInvokeExpr;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.ast.expr.SQLVariantRefExpr;
import com.alibaba.druid.sql.ast.statement.SQLDropTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLSelectQuery;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLUnionQuery;
import com.alibaba.druid.sql.dialect.mysql.ast.expr.MySqlOutFileExpr;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlExecuteStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSelectQueryBlock;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowColumnsStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowDatabasesStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowTablesStatement;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitorAdapter;

public class MySqlWallVisitor extends MySqlASTVisitorAdapter implements WallVisitor {

    private Set<String>           permitFunctions   = new HashSet<String>();
    private Set<String>           permitTablesoures = new HashSet<String>();
    private Set<String>           permitSchemas     = new HashSet<String>();

    private final List<Violation> violations;

    public MySqlWallVisitor(){
        this(new ArrayList<Violation>());
    }

    public MySqlWallVisitor(List<Violation> violations){
        this.violations = violations;

        permitFunctions.add("version");
        permitFunctions.add("load_file");
        permitFunctions.add("databse");
        permitFunctions.add("schema");
        permitFunctions.add("user");
        permitFunctions.add("system_user");
        permitFunctions.add("session_user");
        permitFunctions.add("benchmark");
        permitFunctions.add("connection_id");
        permitFunctions.add("current_user");

        permitSchemas.add("information_schema");
        permitSchemas.add("mysql");

        permitTablesoures.add("outfile");
    }

    public List<Violation> getViolations() {
        return violations;
    }

    public boolean visit(MySqlExecuteStatement x) {
        violations.add(new IllegalSQLObjectViolation(SQLUtils.toMySqlString(x)));
        return false;
    }

    @Override
    public boolean visit(MySqlShowTablesStatement x) {
        violations.add(new IllegalSQLObjectViolation(SQLUtils.toMySqlString(x)));
        return false;
    }

    @Override
    public boolean visit(MySqlShowDatabasesStatement x) {
        violations.add(new IllegalSQLObjectViolation(SQLUtils.toMySqlString(x)));
        return false;
    }

    @Override
    public boolean visit(MySqlShowColumnsStatement x) {
        violations.add(new IllegalSQLObjectViolation(SQLUtils.toMySqlString(x)));
        return false;
    }

    public boolean visit(SQLBinaryOpExpr x) {
        WallVisitorUtils.check(this, x);

        return true;
    }

    @Override
    public boolean visit(SQLSelectQueryBlock x) {
        if (x.getWhere() != null) {
            x.getWhere().setParent(x);
        }

        return true;
    }

    @Override
    public boolean visit(MySqlSelectQueryBlock x) {
        return visit((SQLSelectQueryBlock) x);
    }

    public boolean visit(SQLDropTableStatement x) {
        violations.add(new IllegalSQLObjectViolation(SQLUtils.toMySqlString(x)));
        return false;
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
        String methodName = x.getMethodName();

        if (permitFunctions.contains(methodName.toLowerCase())) {
            violations.add(new IllegalSQLObjectViolation(SQLUtils.toMySqlString(x)));
        }

        return true;
    }

    public boolean visit(SQLExprTableSource x) {
        SQLExpr expr = x.getExpr();

        if (expr instanceof SQLPropertyExpr) {
            SQLPropertyExpr propExpr = (SQLPropertyExpr) expr;

            if (propExpr.getOwner() instanceof SQLIdentifierExpr) {
                String ownerName = ((SQLIdentifierExpr) propExpr.getOwner()).getName();

                if (permitSchemas.contains(ownerName.toLowerCase())) {
                    violations.add(new IllegalSQLObjectViolation(SQLUtils.toMySqlString(x)));
                }
            }
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
        if (queryBlockFromIsNull(x.getLeft()) || queryBlockFromIsNull(x.getRight())) {
            violations.add(new IllegalSQLObjectViolation(SQLUtils.toMySqlString(x)));
        }

        return true;
    }

    private static boolean queryBlockFromIsNull(SQLSelectQuery query) {
        if (query instanceof SQLSelectQueryBlock) {
            return ((SQLSelectQueryBlock) query).getFrom() == null;
        }

        return false;
    }
}
