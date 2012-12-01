package com.alibaba.druid.sql.ast.statement;

import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class SQLColumnUniqueIndex extends SQLConstaintImpl implements SQLColumnConstraint {

    private static final long serialVersionUID = 1L;

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.getName());
        }
        visitor.endVisit(this);
    }

}
