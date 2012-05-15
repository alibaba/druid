package com.alibaba.druid.sql.dialect.mysql.ast.statement;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitor;

public class MySqlShowCreateEventStatement extends MySqlStatementImpl {

    private static final long serialVersionUID = 1L;

    private SQLExpr           eventName;

    public void accept0(MySqlASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, eventName);
        }
        visitor.endVisit(this);
    }

    public SQLExpr getEventName() {
        return eventName;
    }

    public void setEventName(SQLExpr eventName) {
        this.eventName = eventName;
    }

}
