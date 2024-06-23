package com.alibaba.druid.sql.template;

import com.alibaba.druid.sql.ast.SQLObjectImpl;
import com.alibaba.druid.sql.ast.statement.SQLSelectQuery;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class SQLSelectQueryTemplate extends SQLObjectImpl implements SQLSelectQuery {
    private String text;

    public SQLSelectQueryTemplate() {
    }

    public SQLSelectQueryTemplate(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    @Override
    protected void accept0(SQLASTVisitor v) {
        v.visit(this);
        v.endVisit(this);
    }

    @Override
    public boolean isParenthesized() {
        return false;
    }

    @Override
    public void setParenthesized(boolean parenthesized) {
    }

    @Override
    public SQLSelectQueryTemplate clone() {
        return new SQLSelectQueryTemplate(text);
    }
}
