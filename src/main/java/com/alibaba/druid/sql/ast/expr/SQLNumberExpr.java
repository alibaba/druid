package com.alibaba.druid.sql.ast.expr;

import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class SQLNumberExpr extends SQLNumericLiteralExpr {
    private static final long serialVersionUID = 1L;

    private Number number;

    public SQLNumberExpr() {

    }

    public SQLNumberExpr(Number number) {

        this.number = number;
    }

    public Number getNumber() {
        return this.number;
    }

    public void setNumber(Number number) {
        this.number = number;
    }

    public void output(StringBuffer buf) {
        buf.append(this.number.toString());
    }

    protected void accept0(SQLASTVisitor visitor) {
        visitor.visit(this);
        visitor.endVisit(this);
    }
}
