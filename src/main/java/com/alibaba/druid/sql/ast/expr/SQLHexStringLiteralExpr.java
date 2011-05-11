package com.alibaba.druid.sql.ast.expr;

import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class SQLHexStringLiteralExpr extends SQLLiteralExpr {
    private static final long serialVersionUID = 1L;

    private String value;

    public SQLHexStringLiteralExpr() {

    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        visitor.visit(this);

        visitor.endVisit(this);
    }

    public void output(StringBuffer buf) {
        buf.append("x'");
        buf.append(value);
        buf.append("'");
    }
}
