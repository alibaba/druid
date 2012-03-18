package com.alibaba.druid.sql.dialect.mysql.ast.statement;

import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitor;

public class MySqlDescribeStatement extends MySqlStatementImpl {

    private static final long serialVersionUID = 1L;

    private SQLName           object;

    public void accept0(MySqlASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, object);
        }
        visitor.endVisit(this);
    }

    public SQLName getObject() {
        return object;
    }

    public void setObject(SQLName object) {
        this.object = object;
    }

}
