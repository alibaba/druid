package com.alibaba.druid.sql.dialect.mysql.ast.statement;

import com.alibaba.druid.sql.ast.statement.SQLCreateIndexStatement;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class MySqlCreateIndexStatement extends SQLCreateIndexStatement implements MySqlStatement {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private String            using;

    public String getUsing() {
        return using;
    }

    public void setUsing(String using) {
        this.using = using;
    }

    protected void accept0(SQLASTVisitor visitor) {
        accept0((MySqlASTVisitor) visitor);
    }

    @Override
    public void accept0(MySqlASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, getName());
            acceptChild(visitor, getTable());
            acceptChild(visitor, getItems());
        }
        visitor.endVisit(this);
    }
}
