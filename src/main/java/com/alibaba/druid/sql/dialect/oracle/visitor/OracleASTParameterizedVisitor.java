package com.alibaba.druid.sql.dialect.oracle.visitor;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.visitor.SQLASTParameterizedVisitor;

import java.util.List;

public class OracleASTParameterizedVisitor  extends SQLASTParameterizedVisitor implements OracleASTVisitor {
    public OracleASTParameterizedVisitor() {
        super(DbType.oracle);
    }

    public OracleASTParameterizedVisitor(List<Object> parameters) {
        super(DbType.oracle, parameters);
    }
}
