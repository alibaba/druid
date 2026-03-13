package com.alibaba.druid.sql.dialect.dm.ast;

import com.alibaba.druid.sql.ast.SQLObjectImpl;
import com.alibaba.druid.sql.dialect.dm.visitor.DmASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public abstract class DmObjectImpl extends SQLObjectImpl implements DmObject {
    public DmObjectImpl() {
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        this.accept0((DmASTVisitor) visitor);
    }

    public abstract void accept0(DmASTVisitor visitor);
}
