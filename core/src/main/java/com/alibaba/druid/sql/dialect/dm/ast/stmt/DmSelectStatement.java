package com.alibaba.druid.sql.dialect.dm.ast.stmt;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.dm.visitor.DmASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class DmSelectStatement extends SQLSelectStatement {
    public DmSelectStatement() {
        super(DbType.dm);
    }

    public DmSelectStatement(SQLSelect select) {
        super(select, DbType.dm);
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
            acceptChild(visitor, this.select);
        }
        visitor.endVisit(this);
    }
}
