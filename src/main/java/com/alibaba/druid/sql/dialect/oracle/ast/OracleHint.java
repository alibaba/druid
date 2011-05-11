package com.alibaba.druid.sql.dialect.oracle.ast;

import com.alibaba.druid.sql.dialect.oracle.ast.visitor.OracleASTVisitor;

public class OracleHint extends OracleSQLObject {
    private static final long serialVersionUID = 1L;

    private String name;

    public OracleHint() {

    }

    public OracleHint(String name) {

        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    protected void accept0(OracleASTVisitor visitor) {
        visitor.visit(this);
        visitor.endVisit(this);
    }
}
