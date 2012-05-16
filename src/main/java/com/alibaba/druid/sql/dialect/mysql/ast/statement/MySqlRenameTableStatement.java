package com.alibaba.druid.sql.dialect.mysql.ast.statement;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.dialect.mysql.ast.MySqlObjectImpl;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitor;

public class MySqlRenameTableStatement extends MySqlStatementImpl {

    private static final long serialVersionUID = 1L;

    private List<Item>        items            = new ArrayList<Item>(2);

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }
    
    public void accept0(MySqlASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, items);
        }
        visitor.endVisit(this);
    }

    public static class Item extends MySqlObjectImpl {

        private static final long serialVersionUID = 1L;
        private SQLExpr           name;
        private SQLExpr           to;

        public SQLExpr getName() {
            return name;
        }

        public void setName(SQLExpr name) {
            this.name = name;
        }

        public SQLExpr getTo() {
            return to;
        }

        public void setTo(SQLExpr to) {
            this.to = to;
        }

        @Override
        public void accept0(MySqlASTVisitor visitor) {
            if (visitor.visit(this)) {
                acceptChild(visitor, name);
                acceptChild(visitor, to);
            }
            visitor.endVisit(this);
        }

    }
}
