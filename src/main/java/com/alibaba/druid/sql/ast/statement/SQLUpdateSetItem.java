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
package com.alibaba.druid.sql.ast.statement;

import com.alibaba.druid.FastsqlException;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLObjectImpl;
import com.alibaba.druid.sql.ast.SQLReplaceable;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.io.IOException;

public class SQLUpdateSetItem extends SQLObjectImpl implements SQLReplaceable {

    private SQLExpr column;
    private SQLExpr value;

    public SQLUpdateSetItem(){

    }

    public SQLExpr getColumn() {
        return column;
    }

    public void cloneTo(SQLUpdateSetItem x) {
        if (column != null) {
            x.column = column.clone();
            x.column.setParent(x);
        }
        if (value != null) {
            x.value = value.clone();
            x.value.setParent(x);
        }
    }

    @Override
    public SQLUpdateSetItem clone() {
        SQLUpdateSetItem x = new SQLUpdateSetItem();
        cloneTo(x);
        return x;
    }


    public void setColumn(SQLExpr x) {
        if (x != null) {
            x.setParent(this);
        }
        this.column = x;
    }

    public SQLExpr getValue() {
        return value;
    }

    public void setValue(SQLExpr value) {
        if (value != null) {
            value.setParent(this);
        }
        this.value = value;
    }

    public void output(Appendable buf) {
        try {
            column.output(buf);
            buf.append(" = ");
            value.output(buf);
        } catch (IOException ex) {
            throw new FastsqlException("output error", ex);
        }
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            if (column != null) {
                column.accept(visitor);
            }

            if (value != null) {
                value.accept(visitor);
            }
        }

        visitor.endVisit(this);
    }

    public boolean columnMatch(String column) {
        if (this.column instanceof SQLIdentifierExpr) {
            return ((SQLIdentifierExpr) this.column).nameEquals(column);
        } else if (this.column instanceof SQLPropertyExpr) {
            ((SQLPropertyExpr) this.column).nameEquals(column);
        }
        return false;
    }

    public boolean columnMatch(long columnHash) {
        if (this.column instanceof SQLName) {
            return ((SQLName) this.column).nameHashCode64() == columnHash;
        }

        return false;
    }

    @Override
    public boolean replace(SQLExpr expr, SQLExpr target) {
        if (expr == this.column) {
            this.setColumn(target);
            return true;
        }

        if (expr == this.value) {
            setValue(target);
            return true;
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SQLUpdateSetItem that = (SQLUpdateSetItem) o;

        if (column != null ? !column.equals(that.column) : that.column != null) return false;
        return value != null ? value.equals(that.value) : that.value == null;
    }

    @Override
    public int hashCode() {
        int result = column != null ? column.hashCode() : 0;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }
}
