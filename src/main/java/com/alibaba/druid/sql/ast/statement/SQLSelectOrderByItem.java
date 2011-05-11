package com.alibaba.druid.sql.ast.statement;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLObjectImpl;
import com.alibaba.druid.sql.ast.SQLOrderingSpecification;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class SQLSelectOrderByItem extends SQLObjectImpl {
    private static final long serialVersionUID = 1L;

    protected SQLExpr expr;
    protected String collate;
    protected SQLOrderingSpecification type;

    public SQLSelectOrderByItem() {

    }

    public SQLExpr getExpr() {
        return this.expr;
    }

    public void setExpr(SQLExpr expr) {
        this.expr = expr;
    }

    public String getCollate() {
        return collate;
    }

    public void setCollate(String collate) {
        this.collate = collate;
    }

    public SQLOrderingSpecification getType() {
        return this.type;
    }

    public void setType(SQLOrderingSpecification type) {
        this.type = type;
    }

    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.expr);
        }

        visitor.endVisit(this);
    }
}
