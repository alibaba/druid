package com.alibaba.druid.sql.ast.expr;

import com.alibaba.druid.sql.ast.SQLExprImpl;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class SQLVariantRefExpr extends SQLExprImpl {
    private static final long serialVersionUID = 1L;

    private String name;

    public SQLVariantRefExpr(String name) {

        this.name = name;
    }

    public SQLVariantRefExpr() {

    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void output(StringBuffer buf) {
        buf.append(this.name);
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        visitor.visit(this);

        visitor.endVisit(this);
    }
}
