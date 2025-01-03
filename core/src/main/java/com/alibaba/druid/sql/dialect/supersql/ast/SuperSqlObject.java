package com.alibaba.druid.sql.dialect.supersql.ast;

import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.dialect.supersql.visitor.SuperSqlASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public interface SuperSqlObject extends SQLObject {
    void accept0(SuperSqlASTVisitor visitor);

    @Override
    default void accept(SQLASTVisitor visitor) {
        if (visitor instanceof SuperSqlASTVisitor) {
            accept0((SuperSqlASTVisitor) visitor);
        }
    }
}
