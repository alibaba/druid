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
        if (start != null) {
            start.setParent(this);
        }
        this.start = start;
    }

    public SQLExpr getEnd() {
        return end;
    }

    public void setEnd(SQLExpr end) {
        if (end != null) {
            end.setParent(this);
        }
        this.end = end;
    }

    public SQLExpr getEvery() {
        return every;
    }

    public void setEvery(SQLExpr every) {
        if (every != null) {
            every.setParent(this);
        }
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
        if (start != null) {
            sqlPartitionBatch.setStart(start.clone());
        }
        if (end != null) {
            sqlPartitionBatch.setEnd(end.clone());
        }
        if (every != null) {
            sqlPartitionBatch.setEvery(every.clone());
        }
        sqlPartitionBatch.setParent(parent);
        return sqlPartitionBatch;
    }
}
