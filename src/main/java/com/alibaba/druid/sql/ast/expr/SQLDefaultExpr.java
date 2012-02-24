package com.alibaba.druid.sql.ast.expr;

import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class SQLDefaultExpr extends SQLLiteralExpr {

    private static final long serialVersionUID = 1L;

    @Override
    public boolean equals(Object o) {
        return o instanceof SQLDefaultExpr;
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        visitor.visit(this);
        visitor.endVisit(this);
    }

    public String toString() {
        return "DEFAULT";
    }
}
