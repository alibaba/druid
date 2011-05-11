package com.alibaba.druid.sql.dialect.mysql.ast.statement;

import com.alibaba.druid.sql.ast.statement.SQLSelectGroupByClause;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class MySqlSelectGroupBy extends SQLSelectGroupByClause {
    private static final long serialVersionUID = 1L;

    private boolean rollUp = false;

    public boolean isRollUp() {
        return rollUp;
    }

    public void setRollUp(boolean rollUp) {
        this.rollUp = rollUp;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor instanceof MySqlASTVisitor) {
            accept0((MySqlASTVisitor) visitor);
        } else {
            throw new IllegalArgumentException("not support visitor type : " + visitor.getClass().getName());
        }
    }

    protected void accept0(MySqlASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.getItems());
            acceptChild(visitor, this.getHaving());
        }

        visitor.endVisit(this);
    }
}
