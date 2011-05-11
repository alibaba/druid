package com.alibaba.druid.sql.ast.expr;

import java.io.Serializable;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLExprImpl;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class SQLBinaryOpExpr extends SQLExprImpl implements Serializable {
    private static final long serialVersionUID = 1L;
    public SQLExpr left;
    public SQLExpr right;
    public SQLBinaryOperator operator;

    public SQLBinaryOpExpr() {

    }

    public SQLBinaryOpExpr(SQLExpr left, SQLBinaryOperator operator, SQLExpr right) {

        this.left = left;
        this.right = right;
        this.operator = operator;
    }

    public SQLBinaryOpExpr(SQLExpr left, SQLExpr right, SQLBinaryOperator operator) {

        this.left = left;
        this.right = right;
        this.operator = operator;
    }

    public SQLExpr getLeft() {
        return this.left;
    }

    public void setLeft(SQLExpr left) {
        this.left = left;
    }

    public SQLExpr getRight() {
        return this.right;
    }

    public void setRight(SQLExpr right) {
        this.right = right;
    }

    public SQLBinaryOperator getOperator() {
        return this.operator;
    }

    public void setOperator(SQLBinaryOperator operator) {
        this.operator = operator;
    }

    public void output(StringBuffer buf) {
        this.left.output(buf);
        buf.append(" ");
        buf.append(this.operator.name);
        buf.append(" ");
        this.right.output(buf);
    }

    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.left);
            acceptChild(visitor, this.right);
        }

        visitor.endVisit(this);
    }
}
