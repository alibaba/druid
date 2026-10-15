package com.alibaba.druid.sql.dialect.dm.ast;

import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.dialect.dm.visitor.DmASTVisitor;

public interface DmObject extends SQLObject {
    void accept0(DmASTVisitor visitor);
}
