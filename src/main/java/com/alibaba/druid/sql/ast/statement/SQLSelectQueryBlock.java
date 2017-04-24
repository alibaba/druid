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

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLLimit;
import com.alibaba.druid.sql.ast.SQLObjectImpl;
import com.alibaba.druid.sql.ast.SQLOrderBy;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class SQLSelectQueryBlock extends SQLObjectImpl implements SQLSelectQuery {

    protected int                       distionOption;
    protected final List<SQLSelectItem> selectList      = new ArrayList<SQLSelectItem>();

    protected SQLTableSource            from;
    protected SQLExprTableSource        into;
    protected SQLExpr                   where;

    // for oracle & oceanbase
    protected SQLExpr                   startWith;
    protected SQLExpr                   connectBy;
    protected boolean                   prior           = false;
    protected boolean                   noCycle         = false;
    protected SQLOrderBy                orderBySiblings;

    protected SQLSelectGroupByClause    groupBy;
    protected SQLOrderBy                orderBy;
    protected boolean                   parenthesized   = false;
    protected boolean                   forUpdate       = false;
    protected boolean                   noWait          = false;
    protected SQLExpr                   waitTime;

    protected SQLLimit                  limit;

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
        if (groupBy != null) {
            groupBy.setParent(this);
        }
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
    
    public SQLOrderBy getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(SQLOrderBy orderBy) {
        if (orderBy != null) {
            orderBy.setParent(this);
        }
        
        this.orderBy = orderBy;
    }

    public SQLOrderBy getOrderBySiblings() {
        return orderBySiblings;
    }

    public void setOrderBySiblings(SQLOrderBy orderBySiblings) {
        if (orderBySiblings != null) {
            orderBySiblings.setParent(this);
        }
        this.orderBySiblings = orderBySiblings;
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
    
    public void addSelectItem(SQLSelectItem item) {
        this.selectList.add(item);
        item.setParent(this);
    }

    public SQLTableSource getFrom() {
        return this.from;
    }

    public void setFrom(SQLTableSource from) {
        if (from != null) {
            from.setParent(this);
        }
        this.from = from;
    }

    public boolean isParenthesized() {
		return parenthesized;
	}

	public void setParenthesized(boolean parenthesized) {
		this.parenthesized = parenthesized;
	}
	
    public boolean isForUpdate() {
        return forUpdate;
    }

    public void setForUpdate(boolean forUpdate) {
        this.forUpdate = forUpdate;
    }
    
    public boolean isNoWait() {
        return noWait;
    }

    public void setNoWait(boolean noWait) {
        this.noWait = noWait;
    }
    
    public SQLExpr getWaitTime() {
        return waitTime;
    }
    
    public void setWaitTime(SQLExpr waitTime) {
        if (waitTime != null) {
            waitTime.setParent(this);
        }
        this.waitTime = waitTime;
    }

    public SQLLimit getLimit() {
        return limit;
    }

    public void setLimit(SQLLimit limit) {
        if (limit != null) {
            limit.setParent(this);
        }
        this.limit = limit;
    }

    public SQLExpr getFirst() {
        if (limit == null) {
            return null;
        }

        return limit.getRowCount();
    }

    public void setFirst(SQLExpr first) {
        if (limit == null) {
            limit = new SQLLimit();
        }
        this.limit.setRowCount(first);
    }

    public SQLExpr getOffset() {
        if (limit == null) {
            return null;
        }

        return limit.getOffset();
    }

    public void setOffset(SQLExpr offset) {
        if (limit == null) {
            limit = new SQLLimit();
        }
        this.limit.setOffset(offset);
    }

    public boolean isPrior() {
        return prior;
    }

    public void setPrior(boolean prior) {
        this.prior = prior;
    }

    public SQLExpr getStartWith() {
        return this.startWith;
    }

    public void setStartWith(SQLExpr startWith) {
        if (startWith != null) {
            startWith.setParent(this);
        }
        this.startWith = startWith;
    }

    public SQLExpr getConnectBy() {
        return this.connectBy;
    }

    public void setConnectBy(SQLExpr connectBy) {
        if (connectBy != null) {
            connectBy.setParent(this);
        }
        this.connectBy = connectBy;
    }

    public boolean isNoCycle() {
        return this.noCycle;
    }

    public void setNoCycle(boolean noCycle) {
        this.noCycle = noCycle;
    }

	@Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.selectList);
            acceptChild(visitor, this.from);
            acceptChild(visitor, this.where);
            acceptChild(visitor, this.startWith);
            acceptChild(visitor, this.connectBy);
            acceptChild(visitor, this.groupBy);
        }
        visitor.endVisit(this);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (Boolean.valueOf(parenthesized).hashCode());
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
        if (parenthesized ^ other.parenthesized) return false;
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
