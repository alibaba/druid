package com.alibaba.druid.sql.ast;

import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class SQLPartitionBatch extends SQLPartition {
    private SQLExpr start;
    private SQLExpr end;
    private SQLExpr every;

    public SQLExpr getStart() {
        return start;
    }

    public void setStart(SQLExpr start) {
        this.start = start;
    }

    public SQLExpr getEnd() {
        return end;
    }

    public void setEnd(SQLExpr end) {
        this.end = end;
    }

    public SQLExpr getEvery() {
        return every;
    }

    public void setEvery(SQLExpr every) {
        this.every = every;
    }

    @Override
    protected void accept0(SQLASTVisitor v) {
        if (v.visit(this)) {
            acceptChild(v, start);
            acceptChild(v, end);
            acceptChild(v, every);
        }
        v.endVisit(this);
    }
    @Override
    public SQLPartitionBatch clone() {
        SQLPartitionBatch sqlPartitionBatch = new SQLPartitionBatch();
        sqlPartitionBatch.setStart(start);
        sqlPartitionBatch.setEnd(end);
        sqlPartitionBatch.setEvery(every);
        sqlPartitionBatch.setParent(parent);
        return sqlPartitionBatch;
    }
}
