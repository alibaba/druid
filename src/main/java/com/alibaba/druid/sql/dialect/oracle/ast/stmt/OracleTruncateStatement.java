package com.alibaba.druid.sql.dialect.oracle.ast.stmt;

import com.alibaba.druid.sql.ast.statement.SQLTruncateStatement;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class OracleTruncateStatement extends SQLTruncateStatement implements OracleStatement {

    private static final long serialVersionUID = 1L;

    private boolean           purgeSnapshotLog = false;

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        this.accept0((OracleASTVisitor) visitor);
    }

    public void accept0(OracleASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, tableSources);
        }
        visitor.endVisit(this);
    }

    public boolean isPurgeSnapshotLog() {
        return purgeSnapshotLog;
    }

    public void setPurgeSnapshotLog(boolean purgeSnapshotLog) {
        this.purgeSnapshotLog = purgeSnapshotLog;
    }

}
