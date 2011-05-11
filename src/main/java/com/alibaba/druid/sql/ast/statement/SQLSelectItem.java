package com.alibaba.druid.sql.ast.statement;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLObjectImpl;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class SQLSelectItem extends SQLObjectImpl {
    private static final long serialVersionUID = 1L;

    private SQLExpr expr;
    private String alias;

    public SQLSelectItem() {

    }

    public SQLSelectItem(SQLExpr expr, String alias) {

        this.expr = expr;
        this.alias = alias;
    }

    public SQLExpr getExpr() {
        return this.expr;
    }

    public void setExpr(SQLExpr expr) {
        this.expr = expr;
    }

    public String getAlias() {
        return this.alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public void output(StringBuffer buf) {
        this.expr.output(buf);
        if ((this.alias != null) && (this.alias.length() != 0)) {
            buf.append(" AS ");
            buf.append(this.alias);
        }
    }

    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.expr);
        }
        visitor.endVisit(this);
    }
}
