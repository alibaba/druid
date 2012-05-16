package com.alibaba.druid.sql.dialect.mysql.ast.statement;

import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableAddColumn;
import com.alibaba.druid.sql.dialect.mysql.ast.MySqlObject;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class MySqlAlterTableAddColumn extends SQLAlterTableAddColumn implements MySqlObject {

    private static final long serialVersionUID = 1L;

    private SQLName           after;

    public SQLName getAfter() {
        return after;
    }

    public void setAfter(SQLName after) {
        this.after = after;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor instanceof MySqlASTVisitor) {
            accept0((MySqlASTVisitor) visitor);
        } else {
            throw new IllegalArgumentException("not support visitor type : " + visitor.getClass().getName());
        }
    }

    public void accept0(MySqlASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, getColumns());
        }
        visitor.endVisit(this);
    }
}
