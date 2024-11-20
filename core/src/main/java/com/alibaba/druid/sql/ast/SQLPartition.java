package com.alibaba.druid.sql.ast;

import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public abstract class SQLPartition extends SQLObjectImpl {
    @Override
    protected void accept0(SQLASTVisitor v) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SQLPartition clone() {
        throw new UnsupportedOperationException();
    }
}
