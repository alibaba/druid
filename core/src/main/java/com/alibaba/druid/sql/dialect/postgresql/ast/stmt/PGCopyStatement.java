package com.alibaba.druid.sql.dialect.postgresql.ast.stmt;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLStatementImpl;
import com.alibaba.druid.sql.ast.statement.SQLAssignItem;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.dialect.postgresql.visitor.PGASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.ArrayList;
import java.util.List;

public class PGCopyStatement extends SQLStatementImpl implements PGSQLStatement {
    private SQLExprTableSource table;
    private final List<SQLName> columns = new ArrayList<SQLName>();
    private SQLSelect query;
    private boolean directionTo;
    private SQLExpr target; // file path, STDIN, STDOUT
    private boolean program;
    private final List<SQLAssignItem> options = new ArrayList<SQLAssignItem>();
    private SQLExpr where;

    public PGCopyStatement() {
        super(DbType.postgresql);
    }

    public SQLExprTableSource getTable() {
        return table;
    }

    public void setTable(SQLExprTableSource x) {
        if (x != null) {
            x.setParent(this);
        }
        this.table = x;
    }

    public List<SQLName> getColumns() {
        return columns;
    }

    public SQLSelect getQuery() {
        return query;
    }

    public void setQuery(SQLSelect x) {
        if (x != null) {
            x.setParent(this);
        }
        this.query = x;
    }

    public boolean isDirectionTo() {
        return directionTo;
    }

    public void setDirectionTo(boolean directionTo) {
        this.directionTo = directionTo;
    }

    public SQLExpr getTarget() {
        return target;
    }

    public void setTarget(SQLExpr x) {
        if (x != null) {
            x.setParent(this);
        }
        this.target = x;
    }

    public boolean isProgram() {
        return program;
    }

    public void setProgram(boolean program) {
        this.program = program;
    }

    public List<SQLAssignItem> getOptions() {
        return options;
    }

    public SQLExpr getWhere() {
        return where;
    }

    public void setWhere(SQLExpr x) {
        if (x != null) {
            x.setParent(this);
        }
        this.where = x;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor instanceof PGASTVisitor) {
            accept0((PGASTVisitor) visitor);
        }
    }

    @Override
    public void accept0(PGASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, table);
            acceptChild(visitor, columns);
            acceptChild(visitor, query);
            acceptChild(visitor, target);
            acceptChild(visitor, options);
            acceptChild(visitor, where);
        }
        visitor.endVisit(this);
    }
}
