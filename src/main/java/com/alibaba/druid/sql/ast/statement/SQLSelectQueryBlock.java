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

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.*;
import com.alibaba.druid.sql.ast.expr.*;
import com.alibaba.druid.sql.dialect.odps.ast.OdpsUDTFSQLSelectItem;
import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitorAdapter;
import com.alibaba.druid.util.FnvHash;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;

public class SQLSelectQueryBlock extends SQLObjectImpl implements SQLSelectQuery, SQLReplaceable, SQLDbTypedObject {
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
    protected List<SQLSelectOrderByItem> distributeBy;
    protected List<SQLSelectOrderByItem> sortBy;
    protected List<SQLSelectOrderByItem> clusterBy;

    protected String                     cachedSelectList; // optimized for SelectListCache
    protected long                       cachedSelectListHash; // optimized for SelectListCache

    protected String                     dbType;
    protected List<SQLCommentHint>       hints;

    public SQLSelectQueryBlock(){

    }

    public SQLSelectQueryBlock(String dbType){
        this.dbType = dbType;
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

    public void setGroupBy(SQLSelectGroupByClause x) {
        if (x != null) {
            x.setParent(this);
        }
        this.groupBy = x;
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

    public SQLExpr getWhere() {
        return this.where;
    }

    public void setWhere(SQLExpr x) {
        if (x != null) {
            x.setParent(this);
        }
        this.where = x;
    }

    public void addWhere(SQLExpr condition) {
        if (condition == null) {
            return;
        }

        if (where == null) {
            condition.setParent(this);
            where = condition;
            return;
        }

        List<SQLExpr> items = SQLBinaryOpExpr.split(where, SQLBinaryOperator.BooleanAnd);
        for (SQLExpr item : items) {
            if (condition.equals(item)) {
                return;
            }

            if (condition instanceof SQLInListExpr) {
                SQLInListExpr inListExpr = (SQLInListExpr) condition;

                if (item instanceof SQLBinaryOpExpr) {
                    SQLBinaryOpExpr binaryOpItem = (SQLBinaryOpExpr) item;
                    SQLExpr left = binaryOpItem.getLeft();
                    SQLExpr right = binaryOpItem.getRight();
                    if (inListExpr.getExpr().equals(left)
                            && binaryOpItem.getOperator() == SQLBinaryOperator.Equality
                            && !(right instanceof SQLNullExpr)
                    ) {
                        if (inListExpr.getTargetList().contains(right)) {
                            return;
                        }

                        SQLUtils.replaceInParent(item, new SQLBooleanExpr(false));
                        return;
                    }
                } else {
                    if (item instanceof SQLInListExpr) {
                        SQLInListExpr inListItem = (SQLInListExpr) item;
                        if (inListExpr.getExpr().equals(inListItem.getExpr())) {
                            TreeSet<SQLExpr> set = new TreeSet<SQLExpr>();
                            set.addAll(inListItem.getTargetList());

                            List<SQLExpr> andList = new ArrayList<SQLExpr>();
                            for (SQLExpr exprItem : inListExpr.getTargetList()) {
                                if (set.contains(exprItem)) {
                                    andList.add(exprItem.clone());
                                }
                            }

                            if (andList.size() == 0) {
                                SQLUtils.replaceInParent(item, new SQLBooleanExpr(false));
                                return;
                            }

                            inListItem.getTargetList().clear();
                            for (SQLExpr val : andList) {
                                inListItem.addTarget(val);
                            }
                            return;
                        }
                    }
                }
            }
        }

        where = SQLBinaryOpExpr.and(this.where, condition);
        where.setParent(this);
    }

    public void whereOr(SQLExpr condition) {
        if (condition == null) {
            return;
        }

        if (where == null) {
            condition.setParent(this);
            where = condition;
        } else if (SQLBinaryOpExpr.isOr(where) || SQLBinaryOpExpr.isOr(condition)) {
            SQLBinaryOpExprGroup group = new SQLBinaryOpExprGroup(SQLBinaryOperator.BooleanOr, dbType);
            group.add(where);
            group.add(condition);
            group.setParent(this);
            where = group;
        } else {
            where = SQLBinaryOpExpr.or(where, condition);
            where.setParent(this);
        }
    }

    public void addHaving(SQLExpr condition) {
        if (condition == null) {
            return;
        }

        if (groupBy == null) {
            groupBy = new SQLSelectGroupByClause();
        }

        groupBy.addHaving(condition);
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

    public void addOrderBy(SQLOrderBy orderBy) {
        if (orderBy == null) {
            return;
        }

        if (this.orderBy == null) {
            setOrderBy(orderBy);
            return;
        }

        for (SQLSelectOrderByItem item : orderBy.getItems()) {
            this.orderBy.addItem(item.clone());
        }
    }

    public void addOrderBy(SQLSelectOrderByItem orderByItem) {
        if (orderByItem == null) {
            return;
        }

        if (this.orderBy == null) {
            orderBy = new SQLOrderBy();
            orderBy.setParent(this);
        }

        orderBy.addItem(orderByItem);
    }

    public boolean containsOrderBy(SQLSelectOrderByItem orderByItem) {
        if (orderByItem == null || this.orderBy == null) {
            return false;
        }

        if (this.orderBy.getItems().contains(orderByItem)) {
            return true;
        }

        SQLExpr expr = orderByItem.getExpr();
        if (expr == null && expr instanceof SQLIntegerExpr) {
            return false;
        }

        int index = 0;
        for (int i = 0; i < selectList.size(); i++) {
            SQLSelectItem selectItem = selectList.get(i);
            if (selectItem.getExpr().equals(expr)) {
                index = i + 1;
                break;
            }
        }

        if (index > 0) {
            for (SQLSelectOrderByItem selectOrderByItem : orderBy.getItems()) {
                final SQLExpr orderByItemExpr = selectOrderByItem.getExpr();
                if (orderByItemExpr instanceof SQLIntegerExpr && ((SQLIntegerExpr) orderByItemExpr).getNumber().intValue() == index) {
                    return true;
                }
            }
        }

        return false;
    }

    public void addOrderBy(SQLExpr orderBy, SQLOrderingSpecification type) {
        if (orderBy == null) {
            return;
        }

        if (this.orderBy == null) {
            setOrderBy(new SQLOrderBy(orderBy, type));
            return;
        }

        this.orderBy.addItem(orderBy, type);
    }

    public void addOrderBy(SQLExpr orderBy) {
        if (orderBy == null) {
            return;
        }

        if (this.orderBy == null) {
            setOrderBy(new SQLOrderBy(orderBy));
            return;
        }

        this.orderBy.addItem(orderBy);
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

    public void setDistinct() {
        this.distionOption = SQLSetQuantifier.DISTINCT;
    }

    public boolean isDistinct() {
        return this.distionOption == SQLSetQuantifier.DISTINCT;
    }

    public List<SQLSelectItem> getSelectList() {
        return this.selectList;
    }

    public SQLSelectItem getSelectItem(int i) {
        return this.selectList.get(i);
    }

    public void addSelectItem(SQLSelectItem item) {
        this.selectList.add(item);
        item.setParent(this);
    }

    public SQLSelectItem addSelectItem(SQLExpr expr) {
        SQLSelectItem item = new SQLSelectItem(expr);
        this.addSelectItem(item);
        return item;
    }

    public void addSelectItem(String selectItemExpr, String alias) {
        SQLExpr expr = SQLUtils.toSQLExpr(selectItemExpr, dbType);
        this.addSelectItem(new SQLSelectItem(expr, alias));
    }

    public void addSelectItem(SQLExpr expr, String alias) {
        this.addSelectItem(new SQLSelectItem(expr, alias));
    }

    private static class AggregationStatVisitor extends SQLASTVisitorAdapter {
        private boolean aggregation = false;
        public boolean visit(SQLAggregateExpr x) {
            aggregation = true;
            return false;
        }
    };

    public boolean hasSelectAggregation() {
        AggregationStatVisitor v = new AggregationStatVisitor();
        for (SQLSelectItem item : selectList) {
            SQLExpr expr = item.getExpr();
            expr.accept(v);
        }
        return v.aggregation;
    }

    public SQLTableSource getFrom() {
        return this.from;
    }

    public void setFrom(SQLExpr from) {
        setFrom(new SQLExprTableSource(from));
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
            SQLExpr expr = SQLUtils.toSQLExpr(tableName);
            from = new SQLExprTableSource(expr, alias);
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

    public List<SQLSelectOrderByItem> getDistributeBy() {
        if (distributeBy == null) {
            distributeBy = new ArrayList<SQLSelectOrderByItem>();
        }

        return distributeBy;
    }

    public List<SQLSelectOrderByItem> getDistributeByDirect() {
        return distributeBy;
    }

    public void addDistributeBy(SQLExpr x) {
        if (x != null) {
            x.setParent(this);
        } else {
            return;
        }

        if (distributeBy == null) {
            distributeBy = new ArrayList<SQLSelectOrderByItem>();
        }
        distributeBy.add(new SQLSelectOrderByItem(x));
    }

    public void addDistributeBy(SQLSelectOrderByItem item) {
        if (distributeBy == null) {
            distributeBy = new ArrayList<SQLSelectOrderByItem>();
        }
        if (item != null) {
            item.setParent(this);
        }
        this.distributeBy.add(item);
    }

    public List<SQLSelectOrderByItem> getSortBy() {
        if (sortBy == null) {
            sortBy = new ArrayList<SQLSelectOrderByItem>();
        }
        return sortBy;
    }

    public List<SQLSelectOrderByItem> getSortByDirect() {
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
            for (int i = 0; i < this.selectList.size(); i++) {
                SQLSelectItem item = this.selectList.get(i);
                if (item != null) {
                    item.accept(visitor);
                }
            }

            if (this.from != null) {
                this.from.accept(visitor);
            }

            if (this.windows != null) {
                for (int i = 0; i < windows.size(); i++) {
                    SQLWindow item = windows.get(i);
                    item.accept(visitor);
                }
            }

            if (this.into != null) {
                this.into.accept(visitor);
            }

            if (this.where != null) {
                this.where.accept(visitor);
            }

            if (this.startWith != null) {
                this.startWith.accept(visitor);
            }

            if (this.connectBy != null) {
                this.connectBy.accept(visitor);
            }

            if (this.groupBy != null) {
                this.groupBy.accept(visitor);
            }

            if (this.orderBy != null) {
                this.orderBy.accept(visitor);
            }

            if (this.distributeBy != null) {
                for (int i = 0; i < distributeBy.size(); i++) {
                    SQLSelectOrderByItem item = distributeBy.get(i);
                    item.accept(visitor);
                }
            }

            if (this.sortBy != null) {
                for (int i = 0; i < sortBy.size(); i++) {
                    SQLSelectOrderByItem item = sortBy.get(i);
                    item.accept(visitor);
                }
            }

            if (this.waitTime != null) {
                this.waitTime.accept(visitor);
            }

            if (this.limit != null) {
                this.limit.accept(visitor);
            }
        }
        visitor.endVisit(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SQLSelectQueryBlock that = (SQLSelectQueryBlock) o;

        if (distionOption != that.distionOption) return false;
        if (prior != that.prior) return false;
        if (noCycle != that.noCycle) return false;
        if (parenthesized != that.parenthesized) return false;
        if (forUpdate != that.forUpdate) return false;
        if (noWait != that.noWait) return false;
        if (cachedSelectListHash != that.cachedSelectListHash) return false;
        if (selectList != null ? !selectList.equals(that.selectList) : that.selectList != null) {
            return false;
        }
        if (from != null ? !from.equals(that.from) : that.from != null) return false;
        if (into != null ? !into.equals(that.into) : that.into != null) return false;
        if (where != null ? !where.equals(that.where) : that.where != null) return false;
        if (startWith != null ? !startWith.equals(that.startWith) : that.startWith != null) return false;
        if (connectBy != null ? !connectBy.equals(that.connectBy) : that.connectBy != null) return false;
        if (orderBySiblings != null ? !orderBySiblings.equals(that.orderBySiblings) : that.orderBySiblings != null)
            return false;
        if (groupBy != null ? !groupBy.equals(that.groupBy) : that.groupBy != null) return false;
        if (orderBy != null ? !orderBy.equals(that.orderBy) : that.orderBy != null) return false;
        if (waitTime != null ? !waitTime.equals(that.waitTime) : that.waitTime != null) return false;
        if (limit != null ? !limit.equals(that.limit) : that.limit != null) return false;
        if (forUpdateOf != null ? !forUpdateOf.equals(that.forUpdateOf) : that.forUpdateOf != null) return false;
        if (distributeBy != null ? !distributeBy.equals(that.distributeBy) : that.distributeBy != null) return false;
        if (sortBy != null ? !sortBy.equals(that.sortBy) : that.sortBy != null) return false;
        if (cachedSelectList != null ? !cachedSelectList.equals(that.cachedSelectList) : that.cachedSelectList != null)
            return false;
        if (dbType != that.dbType) return false;
        return hints != null ? hints.equals(that.hints) : that.hints == null;
    }

    @Override
    public int hashCode() {
        int result = distionOption;
        result = 31 * result + (selectList != null ? selectList.hashCode() : 0);
        result = 31 * result + (from != null ? from.hashCode() : 0);
        result = 31 * result + (into != null ? into.hashCode() : 0);
        result = 31 * result + (where != null ? where.hashCode() : 0);
        result = 31 * result + (startWith != null ? startWith.hashCode() : 0);
        result = 31 * result + (connectBy != null ? connectBy.hashCode() : 0);
        result = 31 * result + (prior ? 1 : 0);
        result = 31 * result + (noCycle ? 1 : 0);
        result = 31 * result + (orderBySiblings != null ? orderBySiblings.hashCode() : 0);
        result = 31 * result + (groupBy != null ? groupBy.hashCode() : 0);
        result = 31 * result + (orderBy != null ? orderBy.hashCode() : 0);
        result = 31 * result + (parenthesized ? 1 : 0);
        result = 31 * result + (forUpdate ? 1 : 0);
        result = 31 * result + (noWait ? 1 : 0);
        result = 31 * result + (waitTime != null ? waitTime.hashCode() : 0);
        result = 31 * result + (limit != null ? limit.hashCode() : 0);
        result = 31 * result + (forUpdateOf != null ? forUpdateOf.hashCode() : 0);
        result = 31 * result + (distributeBy != null ? distributeBy.hashCode() : 0);
        result = 31 * result + (sortBy != null ? sortBy.hashCode() : 0);
        result = 31 * result + (cachedSelectList != null ? cachedSelectList.hashCode() : 0);
        result = 31 * result + (int) (cachedSelectListHash ^ (cachedSelectListHash >>> 32));
        result = 31 * result + (dbType != null ? dbType.hashCode() : 0);
        result = 31 * result + (hints != null ? hints.hashCode() : 0);
        return result;
    }

    public boolean equalsForMergeJoin(SQLSelectQueryBlock that) {
        if (this == that) return true;
        if (that == null) return false;

        if (into != null || limit != null || groupBy != null) {
            return false;
        }

        if (distionOption != that.distionOption) return false;
        if (prior != that.prior) return false;
        if (noCycle != that.noCycle) return false;
        if (parenthesized != that.parenthesized) return false;
        if (forUpdate != that.forUpdate) return false;
        if (noWait != that.noWait) return false;
        if (cachedSelectListHash != that.cachedSelectListHash) return false;
        if (selectList != null ? !selectList.equals(that.selectList) : that.selectList != null) return false;
        if (from != null ? !from.equals(that.from) : that.from != null) return false;
        if (into != null ? !into.equals(that.into) : that.into != null) return false;
//        if (where != null ? !where.equals(that.where) : that.where != null) return false;
        if (startWith != null ? !startWith.equals(that.startWith) : that.startWith != null) return false;
        if (connectBy != null ? !connectBy.equals(that.connectBy) : that.connectBy != null) return false;
        if (orderBySiblings != null ? !orderBySiblings.equals(that.orderBySiblings) : that.orderBySiblings != null)
            return false;
        if (groupBy != null ? !groupBy.equals(that.groupBy) : that.groupBy != null) return false;
        if (orderBy != null ? !orderBy.equals(that.orderBy) : that.orderBy != null) return false;
        if (waitTime != null ? !waitTime.equals(that.waitTime) : that.waitTime != null) return false;
        if (limit != null ? !limit.equals(that.limit) : that.limit != null) return false;
        if (forUpdateOf != null ? !forUpdateOf.equals(that.forUpdateOf) : that.forUpdateOf != null) return false;
        if (distributeBy != null ? !distributeBy.equals(that.distributeBy) : that.distributeBy != null) return false;
        if (sortBy != null ? !sortBy.equals(that.sortBy) : that.sortBy != null) return false;
        if (cachedSelectList != null ? !cachedSelectList.equals(that.cachedSelectList) : that.cachedSelectList != null)
            return false;
        return dbType == that.dbType;
    }

    public SQLSelectQueryBlock clone() {
        SQLSelectQueryBlock x = new SQLSelectQueryBlock(dbType);
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
        x.parenthesized = parenthesized;
        x.distionOption = distionOption;

        if (x.selectList.size() > 0) {
            x.selectList.clear();
        }

        if (hints != null) {
            for (SQLCommentHint hint : hints) {
                SQLCommentHint hint1 = hint.clone();
                hint1.setParent(x);
                x.getHints().add(hint1);
            }
        }

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

        if (distributeBy != null) {
            if (x.distributeBy == null) {
                x.distributeBy = new ArrayList<SQLSelectOrderByItem>();
            }

            for (int i = 0; i < distributeBy.size(); i++) {
                SQLSelectOrderByItem item = distributeBy.get(i).clone();
                item.setParent(x);
                x.distributeBy.add(item);
            }
        }

        if (sortBy != null) {
            if (x.sortBy == null) {
                x.sortBy = new ArrayList<SQLSelectOrderByItem>();
            }

            for (int i = 0; i < sortBy.size(); i++) {
                SQLSelectOrderByItem item = sortBy.get(i).clone();
                item.setParent(x);
                x.sortBy.add(item);
            }
        }

        if (clusterBy != null) {
            if (x.clusterBy == null) {
                x.clusterBy = new ArrayList<SQLSelectOrderByItem>();
            }

            for (int i = 0; i < clusterBy.size(); i++) {
                SQLSelectOrderByItem item = clusterBy.get(i).clone();
                item.setParent(x);
                x.clusterBy.add(item);
            }
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
        return parenthesized;
    }

    @Override
    public void setBracket(boolean bracket) {
        this.parenthesized = bracket;
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

        SQLTableSource tableSource = from.findTableSourceWithColumn(columnHash);

        if (tableSource == null && from instanceof SQLExprTableSource) {
            SQLSelectItem selectItem = this.findSelectItem(columnHash);
            if (selectItem != null
                    && selectItem.getExpr() instanceof SQLName
                    && ((SQLName) selectItem.getExpr()).nameHashCode64() == columnHash) {
                tableSource = from;
            }
        }

        return tableSource;
    }

    @Override
    public boolean replace(SQLExpr expr, SQLExpr target) {
        if (where == expr) {
            setWhere(target);
            return true;
        }

        if (startWith == expr) {
            setStartWith(target);
            return true;
        }

        if (connectBy == expr) {
            setConnectBy(target);
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

        if (from == null) {
            return null;
        }

        if (selectList.size() == 1
                && selectList.get(0).getExpr() instanceof SQLAllColumnExpr) {
            SQLTableSource matchedTableSource = from.findTableSourceWithColumn(identHash);
            if (matchedTableSource != null) {
                return selectList.get(0);
            }
        }

        SQLSelectItem ownerAllItem = null;
        for (SQLSelectItem item : this.selectList) {
            SQLExpr itemExpr = item.getExpr();
//            if (itemExpr instanceof SQLAllColumnExpr && !(from instanceof SQLJoinTableSource)) {
//                if (ownerAllItem != null) {
//                    return null; // dup *
//                }
//                ownerAllItem = item;
//                continue;
//            }

            if (itemExpr instanceof SQLPropertyExpr
                    && ((SQLPropertyExpr) itemExpr).getName().equals("*")
            ) {
                if (ownerAllItem != null) {
                    return null; // dup *
                }
                ownerAllItem = item;
            }
        }

        if (ownerAllItem != null) {
            return ownerAllItem;
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

    public SQLColumnDefinition findColumn(long columnNameHash) {
        SQLObject object = resolveColum(columnNameHash);
        if (object instanceof SQLColumnDefinition) {
            return (SQLColumnDefinition) object;
        }
        return null;
    }

    public SQLObject resolveColum(long columnNameHash) {
        final SQLSelectItem selectItem = findSelectItem(columnNameHash);
        if (selectItem != null) {
            SQLExpr selectItemExpr = selectItem.getExpr();
            if (selectItemExpr instanceof SQLAllColumnExpr) {
                SQLObject resolveColumn = from.resolveColum(columnNameHash);
                if (resolveColumn != null) {
                    return resolveColumn;
                }
            } else if (selectItemExpr instanceof SQLPropertyExpr
                    && ((SQLPropertyExpr) selectItemExpr).getName().equals("*")) {
                SQLTableSource resolvedTableSource = ((SQLPropertyExpr) selectItemExpr).getResolvedTableSource();
                if (resolvedTableSource instanceof SQLSubqueryTableSource) {
                    SQLObject resolveColumn = resolvedTableSource.resolveColum(columnNameHash);
                    if (resolveColumn != null) {
                        return resolveColumn;
                    }
                }
            }

            return selectItem;
        }

        if (from != null) {
            return from.resolveColum(columnNameHash);
        } else {
            return null;
        }
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
                SQLExpr item = items.get(i);
                if (item.equals(condition)) {
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

    public String getDbType() {
        return dbType;
    }

    public void setDbType(String dbType) {
        this.dbType = dbType;
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

    public boolean replaceInParent(SQLSelectQuery x) {
        if (parent instanceof SQLSelect) {
            ((SQLSelect) parent).setQuery(x);
            return true;
        }

        if (parent instanceof SQLUnionQuery) {
            SQLUnionQuery union = (SQLUnionQuery) parent;
            return union.replace(this, x);
        }

        return false;
    }

    public List<SQLSelectOrderByItem> getClusterBy() {
        if (clusterBy == null) {
            clusterBy = new ArrayList<SQLSelectOrderByItem>();
        }

        return clusterBy;
    }

    public List<SQLSelectOrderByItem> getClusterByDirect() {
        return clusterBy;
    }

    public void addClusterBy(SQLSelectOrderByItem item) {
        if (clusterBy == null) {
            clusterBy = new ArrayList<SQLSelectOrderByItem>();
        }
        if (item != null) {
            item.setParent(this);
        }
        this.clusterBy.add(item);
    }

    public List<String> computeSelecteListAlias() {
        List<String> aliasList = new ArrayList<String>();

        for (SQLSelectItem item : this.selectList) {
            if (item instanceof OdpsUDTFSQLSelectItem) {
                aliasList.addAll(((OdpsUDTFSQLSelectItem) item).getAliasList());
            } else {
                SQLExpr expr = item.getExpr();
                if (expr instanceof SQLAllColumnExpr) {
                    // TODO
                } else if (expr instanceof SQLPropertyExpr && ((SQLPropertyExpr) expr).getName().equals("*")) {
                    // TODO
                } else {
                    aliasList.add(item.computeAlias());
                }
            }
        }

        return aliasList;
    }

    public List<SQLTableSource> getMappJoinTableSources() {
        if (hints == null) {
            return Collections.emptyList();
        }

        List<SQLTableSource> tableSources = null;
        for (SQLCommentHint hint : hints) {
            String text = hint.getText();
            if (text.startsWith("+")) {
                SQLExpr hintExpr = SQLUtils.toSQLExpr(text.substring(1), dbType);
                if (hintExpr instanceof SQLMethodInvokeExpr) {
                    SQLMethodInvokeExpr func = (SQLMethodInvokeExpr) hintExpr;
                    if (func.methodNameHashCode64() == FnvHash.Constants.MAPJOIN) {
                        for (SQLExpr arg : func.getArguments()) {
                            SQLIdentifierExpr tablename = (SQLIdentifierExpr) arg;
                            SQLTableSource tableSource = findTableSource(tablename.getName());
                            if (tableSources == null) {
                                tableSources = new ArrayList<SQLTableSource>(2);
                            }
                            tableSources.add(tableSource);
                        }
                    }
                }
            }
        }

        if (tableSources == null) {
            return Collections.emptyList();
        }

        return tableSources;
    }

    public boolean clearMapJoinHint() {
        if (hints == null) {
            return false;
        }

        int removeCount = 0;
        for (int i = hints.size() - 1; i >= 0; i--) {
            SQLCommentHint hint = hints.get(i);
            String text = hint.getText();
            if (text.startsWith("+")) {
                SQLExpr hintExpr = SQLUtils.toSQLExpr(text.substring(1), dbType);
                if (hintExpr instanceof SQLMethodInvokeExpr) {
                    SQLMethodInvokeExpr func = (SQLMethodInvokeExpr) hintExpr;
                    if (func.methodNameHashCode64() == FnvHash.Constants.MAPJOIN) {
                        hints.remove(i);
                        removeCount++;
                    }
                }
            }
        }

        return removeCount > 0;
    }
}
