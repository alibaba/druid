package com.alibaba.druid.sql.ast;

import com.alibaba.druid.sql.ast.expr.SQLIntegerExpr;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class SQLTop extends SQLObjectImpl implements SQLReplaceable {
    private SQLExpr expr;
    private boolean percent;
    private boolean withTies;
    private boolean parentheses;

    public SQLTop() {}
    public SQLTop(SQLExpr expr) {
        this.setExpr(expr);
    }

    public SQLExpr getExpr() {
        return expr;
    }

    public void setExpr(SQLExpr expr) {
        if (expr != null) {
            expr.setParent(this);
        }
        this.expr = expr;
    }

    public void setExpr(int expr) {
        this.setExpr(new SQLIntegerExpr(expr));
    }

    public boolean isPercent() {
        return percent;
    }

    public void setPercent(boolean percent) {
        this.percent = percent;
    }

    public boolean isWithTies() {
        return withTies;
    }

    public void setWithTies(boolean withTies) {
        this.withTies = withTies;
    }

    public boolean isParentheses() {
        return parentheses;
    }

    public void setParentheses(boolean parentheses) {
        this.parentheses = parentheses;
    }

    public SQLTop clone() {
        SQLTop x = new SQLTop();
        if (expr != null) {
            x.setExpr(expr.clone());
        }
        x.withTies = withTies;
        x.percent = percent;
        x.parentheses = parentheses;
        return x;
    }

    @Override
    protected void accept0(SQLASTVisitor v) {
        if (v.visit(this)) {
            acceptChild(v, this.expr);
        }
        v.endVisit(this);
    }

    @Override
    public boolean replace(SQLExpr expr, SQLExpr target) {
        if (expr == this.expr) {
            this.expr = target;
        }
        return false;
    }
}
