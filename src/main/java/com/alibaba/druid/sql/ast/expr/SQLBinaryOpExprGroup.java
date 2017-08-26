package com.alibaba.druid.sql.ast.expr;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLExprImpl;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.ArrayList;
import java.util.List;

public class SQLBinaryOpExprGroup extends SQLExprImpl {
    private final SQLBinaryOperator operator;
    private final List<SQLExpr> items = new ArrayList<SQLExpr>();
    private String dbType;

    public SQLBinaryOpExprGroup(SQLBinaryOperator operator) {
        this.operator = operator;
    }

    public SQLBinaryOpExprGroup(SQLBinaryOperator operator, String dbType) {
        this.operator = operator;
        this.dbType = dbType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SQLBinaryOpExprGroup that = (SQLBinaryOpExprGroup) o;

        if (operator != that.operator) return false;
        return items.equals(that.items);
    }

    @Override
    public int hashCode() {
        int result = operator != null ? operator.hashCode() : 0;
        result = 31 * result + items.hashCode();
        return result;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.items);
        }

        visitor.endVisit(this);
    }

    @Override
    public SQLExpr clone() {
        SQLBinaryOpExprGroup x = new SQLBinaryOpExprGroup(operator);

        for (SQLExpr item : items) {
            SQLExpr item2 = item.clone();
            item2.setParent(this);
            x.items.add(item2);
        }

        return x;
    }

    public void add(SQLExpr item) {
        if (item != null) {
            item.setParent(this);
        }
        this.items.add(item);
    }

    public List<SQLExpr> getItems() {
        return this.items;
    }

    public SQLBinaryOperator getOperator() {
        return operator;
    }
}
