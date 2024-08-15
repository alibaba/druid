package com.alibaba.druid.sql.dialect.redshift.stmt;

import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.dialect.redshift.visitor.RedshiftASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public interface RedshiftObject extends SQLObject {
    default void accept0(SQLASTVisitor v) {
        if (v instanceof RedshiftASTVisitor) {
            accept0((RedshiftASTVisitor) v);
        }
    }
    void accept0(RedshiftASTVisitor visitor);
}
