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

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLHint;
import com.alibaba.druid.sql.ast.SQLReplaceable;
import com.alibaba.druid.sql.ast.statement.SQLUpdateStatement;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.ArrayList;
import java.util.List;

public class OracleUpdateStatement extends SQLUpdateStatement implements OracleStatement, SQLReplaceable {

    private List<SQLHint>       hints         = new ArrayList<SQLHint>(1);
    private boolean             only          = false;
    private String              alias;

    private final List<SQLExpr>       returningInto = new ArrayList<SQLExpr>();

    public OracleUpdateStatement() {
        super(DbType.oracle);
    }

    public List<SQLExpr> getReturningInto() {
        return returningInto;
    }

    public void addReturningInto(SQLExpr returningInto) {
        if (returningInto == null) {
            return;
        }
        returningInto.setParent(this);
        this.returningInto.add(returningInto);
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor instanceof OracleASTVisitor) {
            accept0((OracleASTVisitor) visitor);
            return;
        }

        super.accept(visitor);
    }

    @Override
    public void accept0(OracleASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.hints);
            acceptChild(visitor, tableSource);
            acceptChild(visitor, items);
            acceptChild(visitor, where);
            acceptChild(visitor, returning);
            acceptChild(visitor, returningInto);
        }
        visitor.endVisit(this);
    }


    @Override
    public boolean replace(SQLExpr expr, SQLExpr target) {
        boolean replace = super.replace(expr, target);
        if (replace) {
            return true;
        }

        for (int i = returningInto.size() - 1; i >= 0; i--) {
            if (returningInto.get(i) == expr) {
                target.setParent(this);
                returningInto.set(i, target);
                return true;
            }
        }

        return false;
    }


    public String getAlias() {
        return this.alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public boolean isOnly() {
        return this.only;
    }

    public void setOnly(boolean only) {
        this.only = only;
    }

    public int getHintsSize() {
        if (hints == null) {
            return 0;
        }

        return hints.size();
    }

    public List<SQLHint> getHints() {
        if (hints == null) {
            hints = new ArrayList<SQLHint>(2);
        }
        return hints;
    }

    public void setHints(List<SQLHint> hints) {
        this.hints = hints;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OracleUpdateStatement that = (OracleUpdateStatement) o;

        if (with != null ? !with.equals(that.getWith()) : that.getWith() != null) return false;
        if (!items.equals(that.getItems())) return false;
        if (where != null ? !where.equals(that.getWhere()) : that.getWhere() != null) return false;
        if (from != null ? !from.equals(that.getFrom()) : that.getFrom() != null) return false;
        if (hints != null ? !hints.equals(that.hints) : that.hints != null) return false;
        if (tableSource != null ? !tableSource.equals(that.tableSource) : that.tableSource != null) return false;
        if (returning != null ? !returning.equals(that.returning) : that.returning != null) return false;
        return orderBy != null ? orderBy.equals(that.orderBy) : that.orderBy == null;
    }

    @Override
    public int hashCode() {
        int result = with != null ? with.hashCode() : 0;
        result = 31 * result + items.hashCode();
        result = 31 * result + (where != null ? where.hashCode() : 0);
        result = 31 * result + (from != null ? from.hashCode() : 0);
        result = 31 * result + (tableSource != null ? tableSource.hashCode() : 0);
        result = 31 * result + (returning != null ? returning.hashCode() : 0);
        result = 31 * result + (orderBy != null ? orderBy.hashCode() : 0);
        result = 31 * result + (hints != null ? hints.hashCode() : 0);
        return result;
    }

}
