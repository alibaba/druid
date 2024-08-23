package com.alibaba.druid.sql.dialect.gaussdb.ast;

import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.dialect.gaussdb.visitor.GaussDbASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public interface GaussDbObject extends SQLObject {
    default void accept0(SQLASTVisitor v) {
        if (v instanceof GaussDbASTVisitor) {
            accept0((GaussDbASTVisitor) v);
        }
    }
    void accept0(GaussDbASTVisitor visitor);
}
