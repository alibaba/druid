package com.alibaba.druid.sql.dialect.mysql.ast.statement;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitor;

public class MySqlPrepareStatement extends MySqlStatementImpl {
    private static final long serialVersionUID = 1L;
    private SQLName name;
    private SQLExpr from;

    public MySqlPrepareStatement() {
    }

    public MySqlPrepareStatement(SQLName name, SQLExpr from) {
        this.name = name;
        this.from = from;
    }

    public SQLName getName() {
        return name;
    }

    public void setName(SQLName name) {
        this.name = name;
    }

    public SQLExpr getFrom() {
        return from;
    }

    public void setFrom(SQLExpr from) {
        this.from = from;
    }

    protected void accept0(MySqlASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, name);
            acceptChild(visitor, from);
        }
        visitor.endVisit(this);
    }
}
