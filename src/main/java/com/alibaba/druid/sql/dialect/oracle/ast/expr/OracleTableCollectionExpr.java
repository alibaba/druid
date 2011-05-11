package com.alibaba.druid.sql.dialect.oracle.ast.expr;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLExprImpl;
import com.alibaba.druid.sql.dialect.oracle.ast.visitor.OracleASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class OracleTableCollectionExpr extends SQLExprImpl {
    private SQLExpr expr;
    private boolean outer = false;

    public OracleTableCollectionExpr() {

    }

    public boolean isOuter() {
        return this.outer;
    }

    public void setOuter(boolean outer) {
        this.outer = outer;
    }

    public SQLExpr getExpr() {
        return this.expr;
    }

    public void setExpr(SQLExpr expr) {
        this.expr = expr;
    }

    public void output(StringBuffer buf) {
        buf.append("TALBE (");
        this.expr.output(buf);
        buf.append(")");
        if (this.outer) buf.append("(+)");
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        this.accept0((OracleASTVisitor) visitor);
    }

    protected void accept0(OracleASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.expr);
        }

        visitor.endVisit(this);
    }
}
