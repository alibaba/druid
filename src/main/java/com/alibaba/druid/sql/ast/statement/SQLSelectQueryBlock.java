/*
 * Copyright 1999-2011 Alibaba Group Holding Ltd.
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

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class SQLSelectQueryBlock extends SQLSelectQuery {

    private static final long           serialVersionUID = 1L;

    protected int                       distionOption;
    protected final List<SQLSelectItem> selectList       = new ArrayList<SQLSelectItem>();

    protected SQLTableSource            from;
    protected SQLExprTableSource        into;
    protected SQLExpr                   where;
    protected SQLSelectGroupByClause    groupBy;

    public SQLSelectQueryBlock(){

    }

    public SQLExprTableSource getInto() {
        return into;
    }

    public void setInto(SQLExpr into) {
        this.setInto(new SQLExprTableSource(into));
    }

    public void setInto(SQLExprTableSource into) {
        if (into != null) {
            into.setParent(this);
        }
        this.into = into;
    }

    public SQLSelectGroupByClause getGroupBy() {
        return this.groupBy;
    }

    public void setGroupBy(SQLSelectGroupByClause groupBy) {
        this.groupBy = groupBy;
    }

    public SQLExpr getWhere() {
        return this.where;
    }

    public void setWhere(SQLExpr where) {
        if (where != null) {
            where.setParent(this);
        }
        this.where = where;
    }

    public int getDistionOption() {
        return this.distionOption;
    }

    public void setDistionOption(int distionOption) {
        this.distionOption = distionOption;
    }

    public List<SQLSelectItem> getSelectList() {
        return this.selectList;
    }

    public SQLTableSource getFrom() {
        return this.from;
    }

    public void setFrom(SQLTableSource from) {
        this.from = from;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.selectList);
            acceptChild(visitor, this.from);
            acceptChild(visitor, this.where);
            acceptChild(visitor, this.groupBy);
        }
        visitor.endVisit(this);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + distionOption;
        result = prime * result + ((from == null) ? 0 : from.hashCode());
        result = prime * result + ((groupBy == null) ? 0 : groupBy.hashCode());
        result = prime * result + ((into == null) ? 0 : into.hashCode());
        result = prime * result + ((selectList == null) ? 0 : selectList.hashCode());
        result = prime * result + ((where == null) ? 0 : where.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        SQLSelectQueryBlock other = (SQLSelectQueryBlock) obj;
        if (distionOption != other.distionOption) return false;
        if (from == null) {
            if (other.from != null) return false;
        } else if (!from.equals(other.from)) return false;
        if (groupBy == null) {
            if (other.groupBy != null) return false;
        } else if (!groupBy.equals(other.groupBy)) return false;
        if (into == null) {
            if (other.into != null) return false;
        } else if (!into.equals(other.into)) return false;
        if (selectList == null) {
            if (other.selectList != null) return false;
        } else if (!selectList.equals(other.selectList)) return false;
        if (where == null) {
            if (other.where != null) return false;
        } else if (!where.equals(other.where)) return false;
        return true;
    }

}
