package com.alibaba.druid.sql.dialect.sqlserver.ast;

import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.dialect.sqlserver.visitor.SQLServerASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class SQLServerSelectQueryBlock extends SQLSelectQueryBlock {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private Top               top;

    public Top getTop() {
        return top;
    }

    public void setTop(Top top) {
        this.top = top;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        accept0((SQLServerASTVisitor) visitor);
    }

    protected void accept0(SQLServerASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.top);
            acceptChild(visitor, this.selectList);
            acceptChild(visitor, this.from);
            acceptChild(visitor, this.where);
            acceptChild(visitor, this.groupBy);
        }
        visitor.endVisit(this);
    }
}
