package com.alibaba.druid.sql.ast.expr;

public abstract class SQLTextLiteralExpr extends SQLLiteralExpr {
    private static final long serialVersionUID = 1L;

    protected String text;

    public SQLTextLiteralExpr() {

    }

    public SQLTextLiteralExpr(String text) {

        this.text = text;
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
