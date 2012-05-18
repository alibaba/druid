package com.alibaba.druid.sql.dialect.mysql.ast.statement;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitor;

public class MySqlHelpStatement extends MySqlStatementImpl {

    private static final long serialVersionUID = 1L;

    private SQLExpr           content;

    public SQLExpr getContent() {
        return content;
    }

    public void setContent(SQLExpr content) {
        this.content = content;
    }

    public void accept0(MySqlASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, content);
        }
        visitor.endVisit(this);
    }
}
