package com.alibaba.druid.sql.dialect.mysql.ast.statement;

import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitor;

public class MySqlShowEnginesStatement extends MySqlStatementImpl {

    private static final long serialVersionUID = 1L;
    private boolean           storage          = false;

    public void accept0(MySqlASTVisitor visitor) {
        visitor.visit(this);
        visitor.endVisit(this);
    }

    public boolean isStorage() {
        return storage;
    }

    public void setStorage(boolean storage) {
        this.storage = storage;
    }

}
