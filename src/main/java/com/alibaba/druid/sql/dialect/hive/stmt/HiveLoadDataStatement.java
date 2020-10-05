package com.alibaba.druid.sql.dialect.hive.stmt;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.SQLStatementImpl;
import com.alibaba.druid.sql.ast.statement.SQLAssignItem;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.dialect.hive.visitor.HiveASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.ArrayList;
import java.util.List;

public class HiveLoadDataStatement extends SQLStatementImpl {
    private boolean local;
    private SQLExpr inpath;
    private boolean overwrite;
    private SQLExprTableSource into;

    private final List<SQLExpr> partition  = new ArrayList<SQLExpr>(4);

    public HiveLoadDataStatement() {
        super(DbType.hive);
    }

    protected void accept0(SQLASTVisitor visitor) {
        if (visitor instanceof HiveASTVisitor) {
            accept0((HiveASTVisitor) visitor);
        } else {
            super.accept0(visitor);
        }
    }

    protected void accept0(HiveASTVisitor visitor) {
        if (visitor.visit(this)) {
            this.acceptChild(visitor, inpath);
            this.acceptChild(visitor, into);
            this.acceptChild(visitor, partition);
        }
        visitor.endVisit(this);
    }

    public boolean isLocal() {
        return local;
    }

    public void setLocal(boolean local) {
        this.local = local;
    }

    public SQLExpr getInpath() {
        return inpath;
    }

    public void setInpath(SQLExpr x) {
        if (x != null) {
            x.setParent(this);
        }
        this.inpath = x;
    }

    public boolean isOverwrite() {
        return overwrite;
    }

    public void setOverwrite(boolean overwrite) {
        this.overwrite = overwrite;
    }

    public SQLExprTableSource getInto() {
        return into;
    }

    public void setInto(SQLExprTableSource x) {
        if (x != null) {
            x.setParent(this);
        }
        this.into = x;
    }

    public void setInto(SQLExpr x) {
        if (x == null) {
            this.into = null;
            return;
        }

        setInto(new SQLExprTableSource(x));
    }

    public List<SQLExpr> getPartition() {
        return partition;
    }

    public void addPartion(SQLAssignItem item) {
        if (item != null) {
            item.setParent(this);
        }
        this.partition.add(item);
    }
}
