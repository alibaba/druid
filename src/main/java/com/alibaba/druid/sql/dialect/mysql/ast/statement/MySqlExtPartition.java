package com.alibaba.druid.sql.dialect.mysql.ast.statement;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.dialect.mysql.ast.MySqlObjectImpl;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitor;

import java.util.ArrayList;
import java.util.List;

public class MySqlExtPartition extends MySqlObjectImpl implements Cloneable {
    private final List<Item> items = new ArrayList<Item>();

    public List<Item> getItems() {
        return items;
    }

    @Override
    public void accept0(MySqlASTVisitor visitor) {
        if (visitor.visit(this)) {
            for (int i = 0; i < items.size(); i++) {
                items.get(i).accept(visitor);
            }
        }
        visitor.endVisit(this);
    }

    public MySqlExtPartition clone() {
        MySqlExtPartition x = new MySqlExtPartition();
        for (Item item : items) {
            Item item1 = item.clone();
            item1.setParent(x);
            x.items.add(item1);
        }
        return x;
    }

    public static class Item extends MySqlObjectImpl implements Cloneable {
        private SQLName dbPartition;
        private SQLExpr dbPartitionBy;
        private SQLName tbPartition;
        private SQLExpr tbPartitionBy;

        @Override
        public void accept0(MySqlASTVisitor visitor) {
            if (visitor.visit(this)) {
                acceptChild(visitor, dbPartition);
                acceptChild(visitor, dbPartitionBy);
                acceptChild(visitor, tbPartition);
                acceptChild(visitor, tbPartitionBy);
            }
            visitor.endVisit(this);
        }

        public Item clone() {
            Item x = new Item();

            if (dbPartition != null) {
                x.setDbPartition(dbPartition.clone());
            }

            if (dbPartitionBy != null) {
                x.setDbPartitionBy(dbPartitionBy.clone());
            }

            if (tbPartition != null) {
                x.setTbPartition(tbPartition.clone());
            }

            if (tbPartitionBy != null) {
                x.setTbPartitionBy(tbPartitionBy.clone());
            }

            return x;
        }

        public SQLName getDbPartition() {
            return dbPartition;
        }

        public void setDbPartition(SQLName x) {
            if (x != null) {
                x.setParent(this);
            }
            this.dbPartition = x;
        }

        public SQLName getTbPartition() {
            return tbPartition;
        }

        public void setTbPartition(SQLName x) {
            if (x != null) {
                x.setParent(this);
            }
            this.tbPartition = x;
        }

        public SQLExpr getDbPartitionBy() {
            return dbPartitionBy;
        }

        public void setDbPartitionBy(SQLExpr x) {
            if (x != null) {
                x.setParent(this);
            }
            this.dbPartitionBy = x;
        }

        public SQLExpr getTbPartitionBy() {
            return tbPartitionBy;
        }

        public void setTbPartitionBy(SQLExpr x) {
            if (x != null) {
                x.setParent(this);
            }
            this.tbPartitionBy = x;
        }
    }
}
