package com.alibaba.druid.sql.dialect.mysql.ast.statement;

import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitor;

public class MySqlShowProcessListStatement extends MySqlStatementImpl {

    private static final long serialVersionUID = 1L;

    private boolean           full             = false;

    public void accept0(MySqlASTVisitor visitor) {
        visitor.visit(this);
        visitor.endVisit(this);
    }

    public boolean isFull() {
        return full;
    }

    public void setFull(boolean full) {
        this.full = full;
    }

}
