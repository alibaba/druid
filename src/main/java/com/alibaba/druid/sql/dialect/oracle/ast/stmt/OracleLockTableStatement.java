package com.alibaba.druid.sql.dialect.oracle.ast.stmt;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleASTVisitor;

public class OracleLockTableStatement extends OracleStatementImpl {

    private static final long serialVersionUID = 1L;

    private SQLName           table;
    private LockMode          lockMode;
    private boolean           noWait           = false;
    private SQLExpr           wait;

    public boolean isNoWait() {
        return noWait;
    }

    public void setNoWait(boolean noWait) {
        this.noWait = noWait;
    }

    public SQLExpr getWait() {
        return wait;
    }

    public void setWait(SQLExpr wait) {
        this.wait = wait;
    }

    public SQLName getTable() {
        return table;
    }

    public void setTable(SQLName table) {
        this.table = table;
    }

    public LockMode getLockMode() {
        return lockMode;
    }

    public void setLockMode(LockMode lockMode) {
        this.lockMode = lockMode;
    }

    @Override
    public void accept0(OracleASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, table);
            acceptChild(visitor, wait);
        }
        visitor.endVisit(this);
    }

    public static enum LockMode {
        EXCLUSIVE, SHARE
    }
}
