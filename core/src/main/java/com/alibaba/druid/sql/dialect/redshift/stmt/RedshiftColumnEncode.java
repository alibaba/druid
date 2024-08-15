package com.alibaba.druid.sql.dialect.redshift.stmt;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.dialect.redshift.visitor.RedshiftASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class RedshiftColumnEncode extends RedshiftColumnConstraint {
    private SQLExpr expr;
    public RedshiftColumnEncode() {
        super();
    }
    public SQLExpr getExpr() {
        return expr;
    }

    public void setExpr(SQLExpr expr) {
        this.expr = expr;
    }
    @Override
    protected void accept0(SQLASTVisitor v) {
        if (v instanceof RedshiftASTVisitor) {
            RedshiftASTVisitor vv = (RedshiftASTVisitor) v;
            if (vv.visit(this)) {
                acceptChild(vv, expr);
            }
            vv.endVisit(this);
        }
    }

    @Override
    public RedshiftColumnEncode clone() {
        RedshiftColumnEncode redshiftColumnEncode = new RedshiftColumnEncode();
        super.cloneTo(redshiftColumnEncode);
        redshiftColumnEncode.setExpr(expr.clone());
        return redshiftColumnEncode;
    }
}
