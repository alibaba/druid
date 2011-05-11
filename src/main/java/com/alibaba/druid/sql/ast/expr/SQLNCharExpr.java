package com.alibaba.druid.sql.ast.expr;

import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class SQLNCharExpr extends SQLTextLiteralExpr {
    private static final long serialVersionUID = 1L;

    public SQLNCharExpr() {

    }

    public SQLNCharExpr(String text) {
        super(text);
    }

    public void output(StringBuffer buf) {
        if ((this.text == null) || (this.text.length() == 0)) {
            buf.append("NULL");
            return;
        }

        buf.append("N'");
        buf.append(this.text.replaceAll("'", "''"));
        buf.append("'");
    }

    protected void accept0(SQLASTVisitor visitor) {
        visitor.visit(this);
        visitor.endVisit(this);
    }
}
