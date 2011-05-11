package com.alibaba.druid.sql.ast.expr;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLExprImpl;
import com.alibaba.druid.sql.ast.SQLObjectImpl;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class SQLCaseExpr extends SQLExprImpl implements Serializable {
    private static final long serialVersionUID = 1L;
    private final List<Item> items = new ArrayList<Item>();
    private SQLExpr valueExpr;
    private SQLExpr elseExpr;

    public SQLCaseExpr() {

    }

    public void output(StringBuffer buf) {
        buf.append("CASE ");
        if (this.valueExpr != null) {
            this.valueExpr.output(buf);
            buf.append(" ");
        }

        int i = 0;
        for (int size = this.items.size(); i < size; ++i) {
            if (i != 0) {
                buf.append(" ");
            }
            ((Item) this.items.get(i)).output(buf);
        }

        if (this.elseExpr != null) {
            buf.append(" ELSE ");
            this.elseExpr.output(buf);
        }

        buf.append(" END");
    }

    public SQLExpr getValueExpr() {
        return this.valueExpr;
    }

    public void setValueExpr(SQLExpr valueExpr) {
        this.valueExpr = valueExpr;
    }

    public SQLExpr getElseExpr() {
        return this.elseExpr;
    }

    public void setElseExpr(SQLExpr elseExpr) {
        this.elseExpr = elseExpr;
    }

    public List<Item> getItems() {
        return this.items;
    }

    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.valueExpr);
            acceptChild(visitor, this.items);
            acceptChild(visitor, this.elseExpr);
        }
        visitor.endVisit(this);
    }

    public static class Item extends SQLObjectImpl implements Serializable {
        private static final long serialVersionUID = 1L;
        private SQLExpr conditionExpr;
        private SQLExpr valueExpr;

        public Item() {

        }

        public Item(SQLExpr conditionExpr, SQLExpr valueExpr) {

            this.conditionExpr = conditionExpr;
            this.valueExpr = valueExpr;
        }

        public SQLExpr getConditionExpr() {
            return this.conditionExpr;
        }

        public void setConditionExpr(SQLExpr conditionExpr) {
            this.conditionExpr = conditionExpr;
        }

        public SQLExpr getValueExpr() {
            return this.valueExpr;
        }

        public void setValueExpr(SQLExpr valueExpr) {
            this.valueExpr = valueExpr;
        }

        public void output(StringBuffer buf) {
            buf.append("WHEN ");
            this.conditionExpr.output(buf);
            buf.append(" THEN ");
            this.valueExpr.output(buf);
        }

        protected void accept0(SQLASTVisitor visitor) {
            if (visitor.visit(this)) {
                acceptChild(visitor, this.conditionExpr);
                acceptChild(visitor, this.valueExpr);
            }
            visitor.endVisit(this);
        }
    }
}
