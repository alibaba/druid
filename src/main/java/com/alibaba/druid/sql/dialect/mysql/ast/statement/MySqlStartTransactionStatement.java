package com.alibaba.druid.sql.dialect.mysql.ast.statement;

import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitor;

public class MySqlStartTransactionStatement extends MySqlStatementImpl {
    private static final long serialVersionUID = 1L;

    private boolean consistentSnapshot = false;

    private boolean begin = false;
    private boolean work = false;

    public boolean isConsistentSnapshot() {
        return consistentSnapshot;
    }

    public void setConsistentSnapshot(boolean consistentSnapshot) {
        this.consistentSnapshot = consistentSnapshot;
    }

    public boolean isBegin() {
        return begin;
    }

    public void setBegin(boolean begin) {
        this.begin = begin;
    }

    public boolean isWork() {
        return work;
    }

    public void setWork(boolean work) {
        this.work = work;
    }

    protected void accept0(MySqlASTVisitor visitor) {
        if (visitor.visit(this)) {

        }
        visitor.endVisit(this);
    }
}
