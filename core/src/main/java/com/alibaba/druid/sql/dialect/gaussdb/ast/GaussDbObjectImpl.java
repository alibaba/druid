package com.alibaba.druid.sql.dialect.gaussdb.ast;

import com.alibaba.druid.sql.ast.SQLObjectImpl;
import com.alibaba.druid.sql.dialect.gaussdb.visitor.GaussDbASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public abstract class GaussDbObjectImpl extends SQLObjectImpl implements GaussDbObject {
    public void accept0(SQLASTVisitor v) {}
    public void accept0(GaussDbASTVisitor visitor) {
    }
}
