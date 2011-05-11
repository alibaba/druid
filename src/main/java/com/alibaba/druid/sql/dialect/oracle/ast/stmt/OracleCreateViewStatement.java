package com.alibaba.druid.sql.dialect.oracle.ast.stmt;

import com.alibaba.druid.sql.ast.statement.SQLCreateViewStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.visitor.OracleASTVisitor;

public class OracleCreateViewStatement extends SQLCreateViewStatement {
    private static final long serialVersionUID = 1L;

    private boolean replace = false;
    private Boolean force;

    public OracleCreateViewStatement() {

    }

    protected void accept0(OracleASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.name);
            acceptChild(visitor, this.subQuery);
        }

        visitor.endVisit(this);
    }

    public Boolean getForce() {
        return this.force;
    }

    public void setForce(Boolean force) {
        this.force = force;
    }

    public boolean isReplace() {
        return this.replace;
    }

    public void setReplace(boolean replace) {
        this.replace = replace;
    }
}
