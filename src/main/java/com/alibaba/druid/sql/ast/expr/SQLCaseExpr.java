/*
 * Copyright 1999-2101 Alibaba Group Holding Ltd.
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
    private final List<Item>  items            = new ArrayList<Item>();
    private SQLExpr           valueExpr;
    private SQLExpr           elseExpr;

    public SQLCaseExpr(){

    }

    public SQLExpr getValueExpr() {
        return this.valueExpr;
    }

    public void setValueExpr(SQLExpr valueExpr) {
        if (valueExpr != null) {
            valueExpr.setParent(this);
        }
        this.valueExpr = valueExpr;
    }

    public SQLExpr getElseExpr() {
        return this.elseExpr;
    }

    public void setElseExpr(SQLExpr elseExpr) {
        if (elseExpr != null) {
            elseExpr.setParent(this);
        }
        this.elseExpr = elseExpr;
    }

    public List<Item> getItems() {
        return this.items;
    }

    public void addItem(Item item) {
        if (item != null) {
            item.setParent(this);
            this.items.add(item);
        }
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
        private SQLExpr           conditionExpr;
        private SQLExpr           valueExpr;

        public Item(){

        }

        public Item(SQLExpr conditionExpr, SQLExpr valueExpr){

            setConditionExpr(conditionExpr);
            setValueExpr(valueExpr);
        }

        public SQLExpr getConditionExpr() {
            return this.conditionExpr;
        }

        public void setConditionExpr(SQLExpr conditionExpr) {
            if (conditionExpr != null) {
                conditionExpr.setParent(this);
            }
            this.conditionExpr = conditionExpr;
        }

        public SQLExpr getValueExpr() {
            return this.valueExpr;
        }

        public void setValueExpr(SQLExpr valueExpr) {
            if (valueExpr != null) {
                valueExpr.setParent(this);
            }
            this.valueExpr = valueExpr;
        }

        protected void accept0(SQLASTVisitor visitor) {
            if (visitor.visit(this)) {
                acceptChild(visitor, this.conditionExpr);
                acceptChild(visitor, this.valueExpr);
            }
            visitor.endVisit(this);
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((conditionExpr == null) ? 0 : conditionExpr.hashCode());
            result = prime * result + ((valueExpr == null) ? 0 : valueExpr.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            Item other = (Item) obj;
            if (conditionExpr == null) {
                if (other.conditionExpr != null) return false;
            } else if (!conditionExpr.equals(other.conditionExpr)) return false;
            if (valueExpr == null) {
                if (other.valueExpr != null) return false;
            } else if (!valueExpr.equals(other.valueExpr)) return false;
            return true;
        }

    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((elseExpr == null) ? 0 : elseExpr.hashCode());
        result = prime * result + ((items == null) ? 0 : items.hashCode());
        result = prime * result + ((valueExpr == null) ? 0 : valueExpr.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        SQLCaseExpr other = (SQLCaseExpr) obj;
        if (elseExpr == null) {
            if (other.elseExpr != null) {
                return false;
            }
        } else if (!elseExpr.equals(other.elseExpr)) {
            return false;
        }
        if (items == null) {
            if (other.items != null) {
                return false;
            }
        } else if (!items.equals(other.items)) {
            return false;
        }
        if (valueExpr == null) {
            if (other.valueExpr != null) {
                return false;
            }
        } else if (!valueExpr.equals(other.valueExpr)) {
            return false;
        }
        return true;
    }

}
