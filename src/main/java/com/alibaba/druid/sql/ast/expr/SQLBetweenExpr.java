package com.alibaba.druid.sql.ast.expr;

import java.io.Serializable;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLExprImpl;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class SQLBetweenExpr extends SQLExprImpl implements Serializable {
    private static final long serialVersionUID = 1L;
    public SQLExpr testExpr;
    private boolean not;
    public SQLExpr beginExpr;
    public SQLExpr endExpr;

    public SQLBetweenExpr() {

    }

    public SQLBetweenExpr(SQLExpr testExpr, SQLExpr beginExpr, SQLExpr endExpr) {

        this.testExpr = testExpr;
        this.beginExpr = beginExpr;
        this.endExpr = endExpr;
    }

    public SQLBetweenExpr(SQLExpr testExpr, boolean not, SQLExpr beginExpr, SQLExpr endExpr) {

        this.testExpr = testExpr;
        this.not = not;
        this.beginExpr = beginExpr;
        this.endExpr = endExpr;
    }

    public void output(StringBuffer buf) {
        this.testExpr.output(buf);
        if (this.not) buf.append(" NOT BETWEEN ");
        else {
            buf.append(" BETWEEN ");
        }
        this.beginExpr.output(buf);
        buf.append(" AND ");
        this.endExpr.output(buf);
    }

    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.testExpr);
            acceptChild(visitor, this.beginExpr);
            acceptChild(visitor, this.endExpr);
        }
        visitor.endVisit(this);
    }

    public SQLExpr getTestExpr() {
        return this.testExpr;
    }

    public void setTestExpr(SQLExpr testExpr) {
        this.testExpr = testExpr;
    }

    public boolean isNot() {
        return this.not;
    }

    public void setNot(boolean not) {
        this.not = not;
    }

    public SQLExpr getBeginExpr() {
        return this.beginExpr;
    }

    public void setBeginExpr(SQLExpr beginExpr) {
        this.beginExpr = beginExpr;
    }

    public SQLExpr getEndExpr() {
        return this.endExpr;
    }

    public void setEndExpr(SQLExpr endExpr) {
        this.endExpr = endExpr;
    }
}
