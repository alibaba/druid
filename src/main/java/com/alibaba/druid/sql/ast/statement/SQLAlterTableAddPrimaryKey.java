package com.alibaba.druid.sql.ast.statement;

import com.alibaba.druid.sql.ast.SQLObjectImpl;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class SQLAlterTableAddPrimaryKey extends SQLObjectImpl implements SQLAlterTableItem {

    private static final long serialVersionUID = 1L;
    private SQLPrimaryKey     primaryKey;

    public SQLPrimaryKey getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(SQLPrimaryKey primaryKey) {
        this.primaryKey = primaryKey;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, primaryKey);
        }
        visitor.endVisit(this);
    }

}
