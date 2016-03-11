package com.alibaba.druid.sql.ast;

import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class SQLKeep extends SQLObjectImpl {

    protected DenseRank  denseRank;

    protected SQLOrderBy orderBy;

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.orderBy);
        }
        visitor.endVisit(this);
    }

    public DenseRank getDenseRank() {
        return denseRank;
    }

    public void setDenseRank(DenseRank denseRank) {
        this.denseRank = denseRank;
    }

    public SQLOrderBy getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(SQLOrderBy orderBy) {
        if (orderBy != null) {
            orderBy.setParent(this);
        }
        this.orderBy = orderBy;
    }

    public static enum DenseRank {
                                  FIRST, //
                                  LAST
    }
}
