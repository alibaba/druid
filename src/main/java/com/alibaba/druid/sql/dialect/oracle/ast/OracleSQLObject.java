package com.alibaba.druid.sql.dialect.oracle.ast;

import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleASTVisitor;

public interface OracleSQLObject extends SQLObject {

    void accept0(OracleASTVisitor visitor);
}
