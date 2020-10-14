package com.alibaba.druid.sql.ast.statement;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLStatementImpl;
import com.alibaba.druid.sql.ast.expr.SQLIntegerExpr;
import com.alibaba.druid.sql.ast.expr.SQLIntervalExpr;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.ArrayList;
import java.util.List;

public class SQLImportTableStatement extends SQLStatementImpl {
    private boolean             extenal;
    private SQLExprTableSource  table;
    private List<SQLAssignItem> partition = new ArrayList<SQLAssignItem>();
    private SQLExpr             from;
    private SQLExpr             location;
    private SQLIntegerExpr version;// for ads
    private boolean usingBuild = false;

    public SQLImportTableStatement() {
        dbType = DbType.hive;
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

    public List<SQLAssignItem> getPartition() {
        return partition;
    }

    public SQLExpr getFrom() {
        return from;
    }

    public void setFrom(SQLExpr x) {
        if (x != null) {
            x.setParent(this);
        }
        this.from = x;
    }

    public SQLExpr getLocation() {
        return location;
    }

    public void setLocation(SQLExpr x) {
        if (x != null) {
            x.setParent(this);
        }
        this.location = x;
    }

    protected void accept0(SQLASTVisitor v) {
        if (v.visit(this)) {
            acceptChild(v, table);
            acceptChild(v, partition);
            acceptChild(v, from);
            acceptChild(v, location);
            acceptChild(v, version);
        }
        v.endVisit(this);
    }

    public SQLIntegerExpr getVersion() {
        return version;
    }

    public void setVersion(SQLIntegerExpr version) {
        this.version = version;
    }

    public boolean isUsingBuild() {
        return usingBuild;
    }

    public void setUsingBuild(boolean usingBuild) {
        this.usingBuild = usingBuild;
    }

    public boolean isExtenal() {
        return extenal;
    }

    public void setExtenal(boolean extenal) {
        this.extenal = extenal;
    }
}
