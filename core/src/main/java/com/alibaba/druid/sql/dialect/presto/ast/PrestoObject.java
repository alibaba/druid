package com.alibaba.druid.sql.dialect.presto.ast;

import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.dialect.presto.visitor.PrestoASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public interface PrestoObject extends SQLObject {
    void accept0(PrestoASTVisitor visitor);
    default void accept0(SQLASTVisitor visitor) {
        if (visitor instanceof PrestoASTVisitor) {
            accept0((PrestoASTVisitor) visitor);
        }
    }
}
