package com.alibaba.druid.sql.dialect.oracle.ast.stmt;

import com.alibaba.druid.sql.ast.statement.SQLAlterTableStatement;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class OracleAlterTableStatement extends SQLAlterTableStatement implements OracleDDLStatement {

    private static final long  serialVersionUID        = 1L;

    private boolean            updateGlobalIndexes     = false;
    private boolean            invalidateGlobalIndexes = false;

    protected void accept0(SQLASTVisitor visitor) {
        accept0((OracleASTVisitor) visitor);
    }

    @Override
    public void accept0(OracleASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, getTableSource());
            acceptChild(visitor, getItems());
        }
        visitor.endVisit(this);
    }

    public boolean isUpdateGlobalIndexes() {
        return updateGlobalIndexes;
    }

    public void setUpdateGlobalIndexes(boolean updateGlobalIndexes) {
        this.updateGlobalIndexes = updateGlobalIndexes;
    }

    public boolean isInvalidateGlobalIndexes() {
        return invalidateGlobalIndexes;
    }

    public void setInvalidateGlobalIndexes(boolean invalidateGlobalIndexes) {
        this.invalidateGlobalIndexes = invalidateGlobalIndexes;
    }
}
