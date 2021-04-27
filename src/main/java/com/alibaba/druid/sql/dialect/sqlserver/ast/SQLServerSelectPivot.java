package com.alibaba.druid.sql.dialect.sqlserver.ast;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.dialect.sqlserver.visitor.SQLServerASTVisitor;

import java.util.ArrayList;
import java.util.List;

public class SQLServerSelectPivot extends SQLServerSelectBasePivot {

    private final List<Item> items = new ArrayList<>();

    private final List<Item> pivotIn = new ArrayList<>();

    @Override
    public void accept0(SQLServerASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.items);
            acceptChild(visitor, this.pivotFor);
            acceptChild(visitor, this.pivotIn);
        }

        visitor.endVisit(this);
    }


    public static class Item extends SQLServerObjectImpl {

        private String alias;
        private SQLExpr expr;

        public Item() {}

        @Override
        public void accept0(SQLServerASTVisitor visitor) {
            if (visitor.visit(this)) {
                acceptChild(visitor, expr);
            }
        }

        public Item(SQLExpr expr, String alias) {
            this.expr = expr;
            this.alias = alias;
        }

        public String getAlias() {
            return this.alias;
        }

        public void setAlias(String alias) {
            this.alias = alias;
        }

        public SQLExpr getExpr() {
            return this.expr;
        }

        public void setExpr(SQLExpr expr) {
            if (expr != null) {
                expr.setParent(this);
            }
            this.expr = expr;
        }
    }

    public List<Item> getItems() {
        return items;
    }

    public List<Item> getPivotIn() {
        return pivotIn;
    }
}
