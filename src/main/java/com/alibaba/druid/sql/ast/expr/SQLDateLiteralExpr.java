package com.alibaba.druid.sql.ast.expr;

import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class SQLDateLiteralExpr extends SQLLiteralExpr {
    private static final long serialVersionUID = 1L;

    private SQLDateLiteralValue value = new SQLDateLiteralValue();

    public SQLDateLiteralExpr() {

    }

    public SQLDateLiteralValue getValue() {
        return value;
    }

    public void setValue(SQLDateLiteralValue value) {
        this.value = value;
    }

    public void output(StringBuffer buf) {
        buf.append("DATE'");
        this.value.output(buf);
        buf.append("'");
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        visitor.visit(this);

        visitor.endVisit(this);
    }

}
