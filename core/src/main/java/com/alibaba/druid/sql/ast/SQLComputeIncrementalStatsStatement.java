package com.alibaba.druid.sql.ast;

import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class SQLComputeIncrementalStatsStatement extends SQLStatementImpl {
    private SQLExpr name;
    private SQLExpr partition;

    public SQLExpr getName() {
        return name;
    }

    public void setName(SQLExpr name) {
        if (name != null) {
            name.setParent(this);
        }
        this.name = name;
    }

    public SQLExpr getPartition() {
        return partition;
    }

    public void setPartition(SQLExpr partition) {
        if (partition != null) {
            partition.setParent(this);
        }
        this.partition = partition;
    }

    @Override
    protected void accept0(SQLASTVisitor v) {
        if (v.visit(this)) {
            acceptChild(v, name);
            acceptChild(v, partition);
        }
        v.endVisit(this);
    }
}
