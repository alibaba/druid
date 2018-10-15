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
package com.alibaba.druid.sql.ast.statement;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.*;
import com.alibaba.druid.sql.ast.expr.*;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;
import com.alibaba.druid.util.FnvHash;

public class SQLSelectQueryBlock extends SQLObjectImpl implements SQLSelectQuery, SQLReplaceable {
    private boolean                      bracket         = false;
    protected int                        distionOption;
    protected final List<SQLSelectItem>  selectList      = new ArrayList<SQLSelectItem>();

    protected SQLTableSource             from;
    protected SQLExprTableSource         into;
    protected SQLExpr                    where;

    // for oracle & oceanbase
    protected SQLExpr                    startWith;
    protected SQLExpr                    connectBy;
    protected boolean                    prior           = false;
    protected boolean                    noCycle         = false;
    protected SQLOrderBy                 orderBySiblings;

    protected SQLSelectGroupByClause     groupBy;
    protected List<SQLWindow>            windows;
    protected SQLOrderBy                 orderBy;
    protected boolean                    parenthesized   = false;
    protected boolean                    forUpdate       = false;
    protected boolean                    noWait          = false;
    protected SQLExpr                    waitTime;
    protected SQLLimit                   limit;

    // for oracle
    protected List<SQLExpr>              forUpdateOf;
    protected List<SQLExpr>              distributeBy;
    protected List<SQLSelectOrderByItem> sortBy;

    protected String                     cachedSelectList; // optimized for SelectListCache
    protected long                       cachedSelectListHash; // optimized for SelectListCache

    protected List<SQLCommentHint>       hints;
    protected String                     dbType;

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

    public void addWhere(SQLExpr condition) {
        if (condition == null) {
            return;
        }

        if (where == null) {
            where = condition;
        } else {
            where = SQLBinaryOpExpr.and(where, condition);
        }
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

    public void addSelectItem(SQLExpr expr) {
        this.addSelectItem(new SQLSelectItem(expr));
    }

    public void addSelectItem(SQLExpr expr, String alias) {
        this.addSelectItem(new SQLSelectItem(expr, alias));
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

    public void setFrom(SQLSelectQueryBlock queryBlock, String alias) {
        if (queryBlock == null) {
            this.from = null;
            return;
        }

        this.setFrom(new SQLSelect(queryBlock), alias);
    }

    public void setFrom(SQLSelect select, String alias) {
        if (select == null) {
            this.from = null;
            return;
        }

        SQLSubqueryTableSource from = new SQLSubqueryTableSource(select);
        from.setAlias(alias);
        this.setFrom(from);
    }

    public void setFrom(String tableName, String alias) {
        SQLExprTableSource from;
        if (tableName == null || tableName.length() == 0) {
            from = null;
        } else {
            from = new SQLExprTableSource(new SQLIdentifierExpr(tableName), alias);
        }
        this.setFrom(from);
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

    public List<SQLExpr> getDistributeBy() {
        return distributeBy;
    }

    public List<SQLSelectOrderByItem> getSortBy() {
        return sortBy;
    }

    public void addSortBy(SQLSelectOrderByItem item) {
        if (sortBy == null) {
            sortBy = new ArrayList<SQLSelectOrderByItem>();
        }
        if (item != null) {
            item.setParent(this);
        }
        this.sortBy.add(item);
    }

	@Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.selectList);
            acceptChild(visitor, this.from);
            acceptChild(visitor, this.into);
            acceptChild(visitor, this.where);
            acceptChild(visitor, this.startWith);
            acceptChild(visitor, this.connectBy);
            acceptChild(visitor, this.groupBy);
            acceptChild(visitor, this.orderBy);
            acceptChild(visitor, this.distributeBy);
            acceptChild(visitor, this.sortBy);
            acceptChild(visitor, this.waitTime);
            acceptChild(visitor, this.limit);
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

    public SQLSelectQueryBlock clone() {
        SQLSelectQueryBlock x = new SQLSelectQueryBlock();
        cloneTo(x);
        return x;
    }

    public List<SQLExpr> getForUpdateOf() {
        if (forUpdateOf == null) {
            forUpdateOf = new ArrayList<SQLExpr>(1);
        }
        return forUpdateOf;
    }

    public int getForUpdateOfSize() {
        if (forUpdateOf == null) {
            return 0;
        }

        return forUpdateOf.size();
    }

    public void cloneSelectListTo(SQLSelectQueryBlock x) {
        x.distionOption = distionOption;
        for (SQLSelectItem item : this.selectList) {
            SQLSelectItem item2 = item.clone();
            item2.setParent(x);
            x.selectList.add(item2);
        }
    }

    public void cloneTo(SQLSelectQueryBlock x) {

        x.distionOption = distionOption;

        for (SQLSelectItem item : this.selectList) {
            x.addSelectItem(item.clone());
        }

        if (from != null) {
            x.setFrom(from.clone());
        }

        if (into != null) {
            x.setInto(into.clone());
        }

        if (where != null) {
            x.setWhere(where.clone());
        }

        if (startWith != null) {
            x.setStartWith(startWith.clone());
        }

        if (connectBy != null) {
            x.setConnectBy(connectBy.clone());
        }

        x.prior = prior;
        x.noCycle = noCycle;

        if (orderBySiblings != null) {
            x.setOrderBySiblings(orderBySiblings.clone());
        }

        if (groupBy != null) {
            x.setGroupBy(groupBy.clone());
        }

        if (orderBy != null) {
            x.setOrderBy(orderBy.clone());
        }

        x.parenthesized = parenthesized;
        x.forUpdate = forUpdate;
        x.noWait = noWait;
        if (waitTime != null) {
            x.setWaitTime(waitTime.clone());
        }

        if (limit != null) {
            x.setLimit(limit.clone());
        }
    }

    @Override
    public boolean isBracket() {
        return bracket;
    }

    @Override
    public void setBracket(boolean bracket) {
        this.bracket = bracket;
    }

    public SQLTableSource findTableSource(String alias) {
        if (from == null) {
            return null;
        }
        return from.findTableSource(alias);
    }

    public SQLTableSource findTableSourceWithColumn(String column) {
        if (from == null) {
            return null;
        }
        return from.findTableSourceWithColumn(column);
    }

    public SQLTableSource findTableSourceWithColumn(long columnHash) {
        if (from == null) {
            return null;
        }
        return from.findTableSourceWithColumn(columnHash);
    }

    @Override
    public boolean replace(SQLExpr expr, SQLExpr target) {
        if (where == expr) {
            setWhere(target);
            return true;
        }
        return false;
    }

    public SQLSelectItem findSelectItem(String ident) {
        if (ident == null) {
            return null;
        }

        long hash = FnvHash.hashCode64(ident);
        return findSelectItem(hash);
    }

    public SQLSelectItem findSelectItem(long identHash) {
        for (SQLSelectItem item : this.selectList) {
            if (item.match(identHash)) {
                return item;
            }
        }

        return null;
    }

    public boolean selectItemHasAllColumn() {
        return selectItemHasAllColumn(true);
    }

    public boolean selectItemHasAllColumn(boolean recursive) {
        for (SQLSelectItem item : this.selectList) {
            SQLExpr expr = item.getExpr();

            boolean allColumn = expr instanceof SQLAllColumnExpr
                    || (expr instanceof SQLPropertyExpr && ((SQLPropertyExpr) expr).getName().equals("*"));

            if (allColumn) {
                if (recursive && from instanceof SQLSubqueryTableSource) {
                    SQLSelect subSelect = ((SQLSubqueryTableSource) from).select;
                    SQLSelectQueryBlock queryBlock = subSelect.getQueryBlock();
                    if (queryBlock != null) {
                        return queryBlock.selectItemHasAllColumn();
                    }
                }
                return true;
            }
        }

        return false;
    }

    public SQLSelectItem findAllColumnSelectItem() {
        SQLSelectItem allColumnItem = null;
        for (SQLSelectItem item : this.selectList) {
            SQLExpr expr = item.getExpr();

            boolean allColumn = expr instanceof SQLAllColumnExpr
                    || (expr instanceof SQLPropertyExpr && ((SQLPropertyExpr) expr).getName().equals("*"));

            if (allColumnItem != null) {
                return null; // duplicateAllColumn
            }
            allColumnItem = item;
        }

        return allColumnItem;
    }

    public SQLColumnDefinition findColumn(String columnName) {
        if (from == null) {
            return null;
        }

        long hash = FnvHash.hashCode64(columnName);
        return from.findColumn(hash);
    }

    public void addCondition(String conditionSql) {
        if (conditionSql == null || conditionSql.length() == 0) {
            return;
        }

        SQLExpr condition = SQLUtils.toSQLExpr(conditionSql, dbType);
        addCondition(condition);
    }

    public void addCondition(SQLExpr expr) {
        if (expr == null) {
            return;
        }

        this.setWhere(SQLBinaryOpExpr.and(where, expr));
    }

    public boolean removeCondition(String conditionSql) {
        if (conditionSql == null || conditionSql.length() == 0) {
            return false;
        }

        SQLExpr condition = SQLUtils.toSQLExpr(conditionSql, dbType);

        return removeCondition(condition);
    }

    public boolean removeCondition(SQLExpr condition) {
        if (condition == null) {
            return false;
        }

        if (where instanceof SQLBinaryOpExprGroup) {
            SQLBinaryOpExprGroup group = (SQLBinaryOpExprGroup) where;

            int removedCount = 0;
            List<SQLExpr> items = group.getItems();
            for (int i = items.size() - 1; i >= 0; i--) {
                if (items.get(i).equals(condition)) {
                    items.remove(i);
                    removedCount++;
                }
            }
            if (items.size() == 0) {
                where = null;
            }

            return removedCount > 0;
        }

        if (where instanceof SQLBinaryOpExpr) {
            SQLBinaryOpExpr binaryOpWhere = (SQLBinaryOpExpr) where;
            SQLBinaryOperator operator = binaryOpWhere.getOperator();
            if (operator == SQLBinaryOperator.BooleanAnd || operator == SQLBinaryOperator.BooleanOr) {
                List<SQLExpr> items = SQLBinaryOpExpr.split(binaryOpWhere);

                int removedCount = 0;
                for (int i = items.size() - 1; i >= 0; i--) {
                    SQLExpr item = items.get(i);
                    if (item.equals(condition)) {
                        if (SQLUtils.replaceInParent(item, null)) {
                            removedCount++;
                        }
                    }
                }

                return removedCount > 0;
            }
        }

        if (condition.equals(where)) {
            where = null;
            return true;
        }

        return false;
    }

    public void limit(int rowCount, int offset) {
        SQLLimit limit = new SQLLimit();
        limit.setRowCount(new SQLIntegerExpr(rowCount));
        if (offset > 0) {
            limit.setOffset(new SQLIntegerExpr(offset));
        }

        setLimit(limit);
    }

    public String getCachedSelectList() {
        return cachedSelectList;
    }

    public void setCachedSelectList(String cachedSelectList, long cachedSelectListHash) {
        this.cachedSelectList = cachedSelectList;
        this.cachedSelectListHash = cachedSelectListHash;
    }

    public long getCachedSelectListHash() {
        return cachedSelectListHash;
    }

    public List<SQLCommentHint> getHintsDirect() {
        return hints;
    }

    public List<SQLCommentHint> getHints() {
        if (hints == null) {
            hints = new ArrayList<SQLCommentHint>(2);
        }
        return hints;
    }

    public void setHints(List<SQLCommentHint> hints) {
        this.hints = hints;
    }

    public int getHintsSize() {
        if (hints == null) {
            return 0;
        }

        return hints.size();
    }

    public String getDbType() {
        return dbType;
    }

    public void setDbType(String dbType) {
        this.dbType = dbType;
    }

    public List<SQLWindow> getWindows() {
        return windows;
    }

    public void addWindow(SQLWindow x) {
        if (x != null) {
            x.setParent(this);
        }
        if (windows == null) {
            windows = new ArrayList<SQLWindow>(4);
        }
        this.windows.add(x);
    }
}
