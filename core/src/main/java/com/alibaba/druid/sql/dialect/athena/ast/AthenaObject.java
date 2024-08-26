package com.alibaba.druid.sql.dialect.athena.ast;

import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.dialect.athena.visitor.AthenaASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public interface AthenaObject extends SQLObject {
    void accept0(AthenaASTVisitor visitor);

    @Override
    default void accept(SQLASTVisitor visitor) {
        if (visitor instanceof AthenaASTVisitor) {
            accept0((AthenaASTVisitor) visitor);
        }
    }
}
