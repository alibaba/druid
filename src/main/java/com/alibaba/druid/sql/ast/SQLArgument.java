package com.alibaba.druid.sql.ast;

import com.alibaba.druid.sql.visitor.SQLASTVisitor;

/**
 * Created by wenshao on 29/05/2017.
 */
public class SQLArgument extends SQLObjectImpl {
    private SQLParameter.ParameterType type;
    private SQLExpr expr;

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, expr);
        }

        visitor.endVisit(this);
    }

    public SQLParameter.ParameterType getType() {
        return type;
    }

    public SQLExpr getExpr() {
        return expr;
    }

    public void setType(SQLParameter.ParameterType type) {
        this.type = type;
    }

    public void setExpr(SQLExpr expr) {
        if (expr != null) {
            expr.setParent(this);
        }
        this.expr = expr;
    }
}
