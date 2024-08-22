package com.alibaba.druid.sql.dialect.doris.ast;

import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.dialect.doris.visitor.DorisASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public interface DorisObject extends SQLObject {
    default void accept0(SQLASTVisitor v) {
        if (v instanceof DorisASTVisitor) {
            accept0((DorisASTVisitor) v);
        }
    }
    void accept0(DorisASTVisitor visitor);
}
