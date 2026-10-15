package com.alibaba.druid.sql.dialect.dm.ast.stmt;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import com.alibaba.druid.sql.dialect.dm.visitor.DmASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class DmInsertStatement extends SQLInsertStatement {
    public DmInsertStatement() {
        dbType = DbType.dm;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor instanceof DmASTVisitor) {
            accept0((DmASTVisitor) visitor);
        } else {
            super.accept0(visitor);
        }
    }

    public void accept0(DmASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.tableSource);
            acceptChild(visitor, this.columns);
            acceptChild(visitor, this.valuesList);
            acceptChild(visitor, this.query);
        }
        visitor.endVisit(this);
    }
}
