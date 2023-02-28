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
import com.alibaba.druid.sql.dialect.oracle.ast.OracleSQLObjectImpl;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleASTVisitor;

import java.util.ArrayList;
import java.util.List;

public class OracleSelectUnPivot extends OracleSelectPivotBase {
    private NullsIncludeType nullsIncludeType;
    private final List<SQLExpr> items = new ArrayList<SQLExpr>();

    private final List<Item> pivotIn = new ArrayList<Item>();

    public OracleSelectUnPivot() {
    }

    public void accept0(OracleASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.items);
            acceptChild(visitor, this.pivotIn);
        }
        visitor.endVisit(this);
    }

    public List<Item> getPivotIn() {
        return this.pivotIn;
    }

    public List<SQLExpr> getItems() {
        return this.items;
    }

    public void addItem(SQLExpr item) {
        if (item != null) {
            item.setParent(this);
        }
        this.items.add(item);
    }

    public NullsIncludeType getNullsIncludeType() {
        return this.nullsIncludeType;
    }

    public void setNullsIncludeType(NullsIncludeType nullsIncludeType) {
        this.nullsIncludeType = nullsIncludeType;
    }

    public static enum NullsIncludeType {
        INCLUDE_NULLS, EXCLUDE_NULLS;

        public static String toString(NullsIncludeType type, boolean ucase) {
            if (INCLUDE_NULLS.equals(type)) {
                return ucase ? "INCLUDE NULLS" : "include nulls";
            }
            if (EXCLUDE_NULLS.equals(type)) {
                return ucase ? "EXCLUDE NULLS" : "exclude nulls";
            }

            throw new IllegalArgumentException();
        }
    }

    @Override
    public OracleSelectUnPivot clone() {
        OracleSelectUnPivot x = new OracleSelectUnPivot();

        x.setNullsIncludeType(nullsIncludeType);

        for (SQLExpr e : this.items) {
            SQLExpr e2 = e.clone();
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


    public static class Item extends OracleSQLObjectImpl implements Cloneable {
        private List<SQLExpr> columns = new ArrayList<SQLExpr>();
        private List<SQLExpr> literals = new ArrayList<SQLExpr>();

        public Item() {
        }

        public List<SQLExpr> getLiterals() {
            return literals;
        }

        public List<SQLExpr> getColumns() {
            return columns;
        }

        public void setLiterals(List<SQLExpr> literals) {
            this.literals = literals;
        }

        public void addLiteral(SQLExpr literal) {
            literal.setParent(this);
            this.literals.add(literal);
        }

        public void addLiteral(int index, SQLExpr literal) {
            literal.setParent(this);
            this.literals.add(index, literal);
        }

        public void setColumns(List<SQLExpr> columns) {
            this.columns = columns;
        }

        public void addColumn(SQLExpr column) {
            column.setParent(this);
            this.columns.add(column);
        }

        public void addColumn(int index, SQLExpr column) {
            column.setParent(this);
            this.columns.add(index, column);
        }

        public void accept0(OracleASTVisitor visitor) {
            if (visitor.visit(this)) {
                acceptChild(visitor, this.columns);
            }

            visitor.endVisit(this);
        }

        @Override
        public Item clone() {
            Item x = new Item();

            if (this.columns != null) {
                for (SQLExpr e : this.columns) {
                    x.columns.add(e.clone());
                }
            }

            if (this.literals != null) {
                for (SQLExpr e : this.literals) {
                    x.literals.add(e.clone());
                }
            }

            return x;
        }

    }
}
