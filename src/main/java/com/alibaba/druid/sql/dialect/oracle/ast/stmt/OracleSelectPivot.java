/*
 * Copyright 1999-2017 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.druid.sql.dialect.oracle.ast.stmt;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLHint;
import com.alibaba.druid.sql.dialect.oracle.ast.OracleSQLObjectImpl;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleASTVisitor;

import java.util.ArrayList;
import java.util.List;

public class OracleSelectPivot extends OracleSelectPivotBase {

    private boolean xml;
    private final List<Item> items = new ArrayList<Item>();
    private final List<SQLExpr> pivotFor = new ArrayList<SQLExpr>();
    private final List<Item> pivotIn = new ArrayList<Item>();

    public OracleSelectPivot() {

    }

    public void accept0(OracleASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.items);
            acceptChild(visitor, this.pivotFor);
            acceptChild(visitor, this.pivotIn);
        }

        visitor.endVisit(this);
    }

    public List<Item> getPivotIn() {
        return this.pivotIn;
    }

    public List<SQLExpr> getPivotFor() {
        return this.pivotFor;
    }

    public boolean isXml() {
        return this.xml;
    }

    public List<Item> getItems() {
        return this.items;
    }

    public void addItem(Item item) {
        if (item != null) {
            item.setParent(this);
        }
        this.items.add(item);
    }

    public void setXml(boolean xml) {
        this.xml = xml;
    }

    public static class Item extends OracleSQLObjectImpl {

        private String alias;
        private SQLExpr expr;

        public Item() {

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

        public void accept0(OracleASTVisitor visitor) {
            if (visitor.visit(this)) {
                acceptChild(visitor, this.expr);
            }

            visitor.endVisit(this);
        }
        @Override
        public Item clone() {
            Item x = new Item();

            x.setAlias(this.alias);

            if (this.alias != null) {
                x.setExpr(this.expr.clone());
            }

            return x;
        }

    }

    @Override
    public OracleSelectPivot clone() {

        OracleSelectPivot x = new OracleSelectPivot();

        x.setXml(this.xml);

        for (Item e : this.items) {
            Item e2 = e.clone();
            e2.setParent(x);
            x.getItems().add(e2);
        }

        for (SQLExpr e : this.pivotFor) {
            SQLExpr e2 = e.clone();
            e2.setParent(x);
            x.getPivotFor().add(e2);
        }

        for (Item e : this.pivotIn) {
            Item e2 = e.clone();
            e2.setParent(x);
            x.getPivotIn().add(e2);
        }

        return x;
    }
}
