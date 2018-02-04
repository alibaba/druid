/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
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

import com.alibaba.druid.sql.ast.*;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SQLCaseStatement extends SQLStatementImpl implements Serializable {
    private final List<Item>    items            = new ArrayList<Item>();
    private SQLExpr             valueExpr;
    private List<SQLStatement>  elseStatements = new ArrayList<SQLStatement>();

    public SQLCaseStatement(){

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

    public List<SQLStatement> getElseStatements() {
        return elseStatements;
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
            acceptChild(visitor, this.elseStatements);
        }
        visitor.endVisit(this);
    }

    @Override
    public List<SQLObject> getChildren() {
        List<SQLObject> children = new ArrayList<SQLObject>();
        if (valueExpr != null) {
            children.add(valueExpr);
        }
        children.addAll(this.items);
        children.addAll(this.elseStatements);
        return children;
    }

    public static class Item extends SQLObjectImpl implements Serializable {

        private static final long serialVersionUID = 1L;
        private SQLExpr           conditionExpr;
        private SQLStatement      statement;

        public Item(){

        }

        public Item(SQLExpr conditionExpr, SQLStatement statement){

            setConditionExpr(conditionExpr);
            setStatement(statement);
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

        public SQLStatement getStatement() {
            return this.statement;
        }

        public void setStatement(SQLStatement statement) {
            if (statement != null) {
                statement.setParent(this);
            }
            this.statement = statement;
        }

        protected void accept0(SQLASTVisitor visitor) {
            if (visitor.visit(this)) {
                acceptChild(visitor, this.conditionExpr);
                acceptChild(visitor, this.statement);
            }
            visitor.endVisit(this);
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((conditionExpr == null) ? 0 : conditionExpr.hashCode());
            result = prime * result + ((statement == null) ? 0 : statement.hashCode());
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
            if (statement == null) {
                if (other.statement != null) return false;
            } else if (!statement.equals(other.statement)) return false;
            return true;
        }

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SQLCaseStatement that = (SQLCaseStatement) o;

        if (items != null ? !items.equals(that.items) : that.items != null) return false;
        if (valueExpr != null ? !valueExpr.equals(that.valueExpr) : that.valueExpr != null) return false;
        return elseStatements != null ? elseStatements.equals(that.elseStatements) : that.elseStatements == null;
    }

    @Override
    public int hashCode() {
        int result = items != null ? items.hashCode() : 0;
        result = 31 * result + (valueExpr != null ? valueExpr.hashCode() : 0);
        result = 31 * result + (elseStatements != null ? elseStatements.hashCode() : 0);
        return result;
    }
}
