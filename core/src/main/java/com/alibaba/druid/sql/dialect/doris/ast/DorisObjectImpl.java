package com.alibaba.druid.sql.dialect.doris.ast;

import com.alibaba.druid.sql.ast.SQLObjectImpl;
import com.alibaba.druid.sql.dialect.doris.visitor.DorisASTVisitor;

public abstract class DorisObjectImpl extends SQLObjectImpl implements DorisObject {
    public abstract void accept0(DorisASTVisitor visitor);
}
