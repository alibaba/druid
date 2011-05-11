package com.alibaba.druid.sql.ast.expr;

import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class SQLNullExpr extends SQLLiteralExpr {
    private static final long serialVersionUID = 1L;

    public SQLNullExpr() {

    }

    public void output(StringBuffer buf) {
        buf.append("NULL");
    }

    protected void accept0(SQLASTVisitor visitor) {
        visitor.visit(this);

        visitor.endVisit(this);
    }
}
