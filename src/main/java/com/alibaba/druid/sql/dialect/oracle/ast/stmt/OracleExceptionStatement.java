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
package com.alibaba.druid.sql.dialect.oracle.ast.stmt;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.OracleSQLObjectImpl;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleASTVisitor;

public class OracleExceptionStatement extends OracleStatementImpl implements OracleStatement {

    private List<Item> items = new ArrayList<Item>();

    public List<Item> getItems() {
        return items;
    }

    public void addItem(Item item) {
        if (item != null) {
            item.setParent(this);
        }
        
        this.items.add(item);
    }

    public static class Item extends OracleSQLObjectImpl {

        private SQLExpr            when;
        private List<SQLStatement> statements = new ArrayList<SQLStatement>();

        public SQLExpr getWhen() {
            return when;
        }

        public void setWhen(SQLExpr when) {
            this.when = when;
        }

        public List<SQLStatement> getStatements() {
            return statements;
        }

        public void setStatement(SQLStatement statement) {
            if (statement != null) {
                statement.setParent(this);
                this.statements.add(statement);
            }
        }

        @Override
        public void accept0(OracleASTVisitor visitor) {
            if (visitor.visit(this)) {
                acceptChild(visitor, when);
                acceptChild(visitor, statements);
            }
            visitor.endVisit(this);
        }

        public Item clone() {
            Item x = new Item();
            if (when != null) {
                x.setWhen(when.clone());
            }
            for (SQLStatement stmt : statements) {
                SQLStatement stmt2 = stmt.clone();
                stmt2.setParent(x);
                x.statements.add(stmt2);
            }
            return x;
        }
    }

    @Override
    public void accept0(OracleASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, items);
        }
        visitor.endVisit(this);
    }

    public OracleExceptionStatement clone() {
        OracleExceptionStatement x = new OracleExceptionStatement();
        for (Item item : items) {
            Item item2 = item.clone();
            item2.setParent(x);
            x.items.add(item2);
        }
        return x;
    }
}
