package com.alibaba.druid.sql.ast.expr;

import com.alibaba.druid.sql.ast.SQLExprImpl;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class SQLAllColumnExpr extends SQLExprImpl {
    private static final long serialVersionUID = 1L;

    public SQLAllColumnExpr() {

    }

    public void output(StringBuffer buf) {
        buf.append("*");
    }

    protected void accept0(SQLASTVisitor visitor) {
        visitor.visit(this);
        visitor.endVisit(this);
    }
}
