package com.alibaba.druid.filter.wall.spi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.alibaba.druid.filter.wall.IllegalSQLObjectViolation;
import com.alibaba.druid.filter.wall.Violation;
import com.alibaba.druid.filter.wall.WallVisitor;
import com.alibaba.druid.logging.Log;
import com.alibaba.druid.logging.LogFactory;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLMethodInvokeExpr;
import com.alibaba.druid.sql.ast.statement.SQLDeleteStatement;
import com.alibaba.druid.sql.ast.statement.SQLDropTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.ast.statement.SQLUnionQuery;
import com.alibaba.druid.sql.ast.statement.SQLUpdateStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleMergeStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleMultiInsertStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSelectTableReference;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleASTVIsitorAdapter;
import com.alibaba.druid.util.JdbcUtils;

public class OracleWallVisitor extends OracleASTVIsitorAdapter implements WallVisitor {

    private final static Log      LOG             = LogFactory.getLog(OracleWallVisitor.class);

    private final Set<String>     permitFunctions = new HashSet<String>();
    private final Set<String>     permitTables    = new HashSet<String>();
    private final Set<String>     permitSchemas   = new HashSet<String>();
    private final Set<String>     permitNames     = new HashSet<String>();

    private final List<Violation> violations;

    public OracleWallVisitor(){
        this(new ArrayList<Violation>());
    }

    public OracleWallVisitor(List<Violation> violations){
        this.violations = violations;

        addPermitFunction("sys_context");
        addPermitFunction("userenv");
        addPermitFunction("sys_guid");
        addPermitFunction("sys_typeid");

        addPermitName("uid");
        addPermitName("user");

        loadPermitTables();

    }

    private void loadPermitTables() {
        try {
            Enumeration<URL> e = Thread.currentThread().getContextClassLoader().getResources("META-INF/druid-filter-wall-permit-function.txt");
            while (e.hasMoreElements()) {
                URL url = e.nextElement();
                InputStream in = null;
                BufferedReader reader = null;
                try {
                    in = url.openStream();
                    reader = new BufferedReader(new InputStreamReader(in));
                    for (;;) {
                        String line = reader.readLine();
                        if (line == null) {
                            break;
                        }
                        line = line.trim();
                        if (line.length() > 0) {
                            addPermitTable(line);
                        }
                    }

                    url.openStream();
                } finally {
                    JdbcUtils.close(reader);
                    JdbcUtils.close(in);
                }
            }
        } catch (IOException e) {
            LOG.error("load oracle permit tables errror", e);
        }
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

    // executeQuery
    public boolean visit(SQLBinaryOpExpr x) {
        WallVisitorUtils.check(this, x);

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
        } else if (x instanceof OracleMultiInsertStatement) {

        } else if (x instanceof OracleMergeStatement) {
        } else {
            violations.add(new IllegalSQLObjectViolation(SQLUtils.toMySqlString(x)));
        }
    }
}
