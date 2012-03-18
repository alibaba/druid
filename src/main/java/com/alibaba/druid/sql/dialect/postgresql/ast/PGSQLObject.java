package com.alibaba.druid.sql.dialect.postgresql.ast;

import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.dialect.postgresql.visitor.PGASTVisitor;

public interface PGSQLObject extends SQLObject {

    void accept0(PGASTVisitor visitor);
}
