package com.alibaba.druid.sql.ast;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class SQLPartitionSpec extends SQLObjectImpl implements Cloneable {
    private List<Item> items = new ArrayList<Item>();

    @Override
    protected void accept0(SQLASTVisitor v) {
        if (v.visit(this)) {
            acceptChild(v, items);
        }
        v.endVisit(this);
    }

    public void addItem(Item item) {
        item.setParent(this);
        items.add(item);
    }

    public List<Item> getItems() {
        return items;
    }

    public SQLPartitionSpec clone() {
        SQLPartitionSpec x = new SQLPartitionSpec();

        for (Item item : items) {
            x.addItem(item.clone());
        }

        return x;
    }

    public static class Item extends SQLObjectImpl implements Cloneable {
        private SQLName column;
        private SQLExpr value;

        @Override
        protected void accept0(SQLASTVisitor v) {
            if (v.visit(this)) {
                acceptChild(v, column);
                acceptChild(v, value);
            }
            v.endVisit(this);
        }

        public Item clone() {
            Item x = new Item();

            if (column != null) {
                x.setColumn(column.clone());
            }

            if (value != null) {
                x.setValue(value.clone());
            }

            return x;
        }

        public SQLName getColumn() {
            return column;
        }

        public void setColumn(SQLName column) {
            this.column = column;
        }

        public SQLExpr getValue() {
            return value;
        }

        public void setValue(SQLExpr value) {
            this.value = value;
        }
    }
}
