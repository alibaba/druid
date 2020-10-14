package com.alibaba.druid.sql.ast;

import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.ArrayList;
import java.util.List;

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
public class SQLOver extends SQLObjectImpl implements SQLReplaceable {

    protected final List<SQLExpr> partitionBy = new ArrayList<SQLExpr>();
    protected SQLOrderBy          orderBy;
    protected SQLOrderBy          distributeBy;
    protected SQLOrderBy          sortBy;

    // for db2
    protected SQLName             of;

    protected WindowingType       windowingType;

    protected boolean             windowingPreceding;
    protected boolean             windowingFollowing;

    protected SQLExpr             windowingBetweenBegin;
    protected WindowingBound      windowingBetweenBeginBound;

    protected SQLExpr             windowingBetweenEnd;
    protected WindowingBound      windowingBetweenEndBound;

    public SQLOver(){

    }

    public SQLOver(SQLOrderBy orderBy){
        this.setOrderBy(orderBy);
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            if (partitionBy != null) {
                for (SQLExpr item : partitionBy) {
                    if (item != null) {
                        item.accept(visitor);
                    }
                }
            }

            if (orderBy != null) {
                orderBy.accept(visitor);
            }

            if (distributeBy != null) {
                distributeBy.accept(visitor);
            }

            if (sortBy != null) {
                sortBy.accept(visitor);
            }

            if (of != null) {
                of.accept(visitor);
            }
        }
        visitor.endVisit(this);
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

    public SQLOrderBy getDistributeBy() {
        return distributeBy;
    }

    public void setDistributeBy(SQLOrderBy x) {
        if (x != null) {
            x.setParent(this);
        }
        this.distributeBy = x;
    }

    public SQLOrderBy getSortBy() {
        return sortBy;
    }

    public void setSortBy(SQLOrderBy x) {
        if (x != null) {
            x.setParent(this);
        }
        this.sortBy = x;
    }

    public SQLName getOf() {
        return of;
    }

    public void setOf(SQLName of) {
        if (of != null) {
            of.setParent(this);
        }
        this.of = of;
    }

    public List<SQLExpr> getPartitionBy() {
        return partitionBy;
    }

    public WindowingType getWindowingType() {
        return windowingType;
    }

    public void setWindowingType(WindowingType windowingType) {
        this.windowingType = windowingType;
    }

    public boolean isWindowingPreceding() {
        return windowingPreceding;
    }

    public void setWindowingPreceding(boolean windowingPreceding) {
        this.windowingPreceding = windowingPreceding;
    }

    public SQLExpr getWindowingBetweenBegin() {
        return windowingBetweenBegin;
    }

    public void setWindowingBetweenBegin(SQLExpr windowingBetweenBegin) {
        this.windowingBetweenBegin = windowingBetweenBegin;
    }

    public SQLExpr getWindowingBetweenEnd() {
        return windowingBetweenEnd;
    }

    public void setWindowingBetweenEnd(SQLExpr windowingBetweenEnd) {
        this.windowingBetweenEnd = windowingBetweenEnd;
    }

    public boolean isWindowingBetweenEndPreceding() {
        return windowingBetweenEndBound == WindowingBound.PRECEDING;
    }

    public boolean isWindowingBetweenEndFollowing() {
        return windowingBetweenEndBound == WindowingBound.FOLLOWING;
    }

    public WindowingBound getWindowingBetweenBeginBound() {
        return windowingBetweenBeginBound;
    }

    public void setWindowingBetweenBeginBound(WindowingBound windowingBetweenBeginBound) {
        this.windowingBetweenBeginBound = windowingBetweenBeginBound;
    }

    public WindowingBound getWindowingBetweenEndBound() {
        return windowingBetweenEndBound;
    }

    public void setWindowingBetweenEndBound(WindowingBound windowingBetweenEndBound) {
        this.windowingBetweenEndBound = windowingBetweenEndBound;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SQLOver sqlOver = (SQLOver) o;

        if (windowingPreceding != sqlOver.windowingPreceding) return false;
        if (windowingFollowing != sqlOver.windowingFollowing) return false;
        if (!partitionBy.equals(sqlOver.partitionBy)) return false;
        if (orderBy != null ? !orderBy.equals(sqlOver.orderBy) : sqlOver.orderBy != null) return false;
        if (of != null ? !of.equals(sqlOver.of) : sqlOver.of != null) return false;
        if (windowingType != sqlOver.windowingType) return false;
        if (windowingBetweenBegin != null ? !windowingBetweenBegin.equals(sqlOver.windowingBetweenBegin) : sqlOver.windowingBetweenBegin != null)
            return false;
        if (windowingBetweenBeginBound != sqlOver.windowingBetweenBeginBound) return false;
        if (windowingBetweenEnd != null ? !windowingBetweenEnd.equals(sqlOver.windowingBetweenEnd) : sqlOver.windowingBetweenEnd != null)
            return false;
        return windowingBetweenEndBound == sqlOver.windowingBetweenEndBound;
    }

    @Override
    public int hashCode() {
        int result = partitionBy != null ? partitionBy.hashCode() : 0;
        result = 31 * result + (orderBy != null ? orderBy.hashCode() : 0);
        result = 31 * result + (of != null ? of.hashCode() : 0);
        result = 31 * result + (windowingType != null ? windowingType.hashCode() : 0);
        result = 31 * result + (windowingPreceding ? 1 : 0);
        result = 31 * result + (windowingFollowing ? 1 : 0);
        result = 31 * result + (windowingBetweenBegin != null ? windowingBetweenBegin.hashCode() : 0);
        result = 31 * result + (windowingBetweenBeginBound != null ? windowingBetweenBeginBound.hashCode() : 0);
        result = 31 * result + (windowingBetweenEnd != null ? windowingBetweenEnd.hashCode() : 0);
        result = 31 * result + (windowingBetweenEndBound != null ? windowingBetweenEndBound.hashCode() : 0);
        return result;
    }

    public void cloneTo(SQLOver x) {
        for (SQLExpr item : partitionBy) {
            SQLExpr item1 = item.clone();
            item1.setParent(x);
            x.partitionBy.add(item1);
        }

        if (orderBy != null) {
            x.setOrderBy(orderBy.clone());
        }

        if (of != null) {
            x.setOf(of.clone());
        }

        x.windowingType = windowingType;
        x.windowingPreceding = windowingPreceding;
        x.windowingFollowing = windowingFollowing;

        if (windowingBetweenBegin != null) {
            x.setWindowingBetweenBegin(windowingBetweenBegin.clone());
        }
        x.windowingBetweenBeginBound = windowingBetweenBeginBound;
        x.windowingBetweenEndBound = windowingBetweenEndBound;

        if (windowingBetweenEnd != null) {
            x.setWindowingBetweenEnd(windowingBetweenEnd.clone());
        }
    }

    public SQLOver clone() {
        SQLOver x = new SQLOver();
        cloneTo(x);
        return x;
    }

    @Override
    public boolean replace(SQLExpr expr, SQLExpr target) {
        if (windowingBetweenBegin == expr) {
            setWindowingBetweenBegin(target);
            return true;
        }

        if (windowingBetweenEnd == expr) {
            setWindowingBetweenEnd(target);
            return true;
        }

        for (int i = 0; i < partitionBy.size(); i++) {
            if (partitionBy.get(i) == expr) {
                partitionBy.set(i, target);
                target.setParent(this);
            }
        }

        return false;
    }

    public static enum WindowingType {
        ROWS("ROWS"), RANGE("RANGE");

        public final String name;
        public final String name_lower;
        private WindowingType(String name) {
            this.name = name;
            this.name_lower = name.toLowerCase();
        }
    }


    public static enum WindowingBound {
        UNBOUNDED_PRECEDING("UNBOUNDED PRECEDING"),
        PRECEDING("PRECEDING"),
        CURRENT_ROW("CURRENT ROW"),
        FOLLOWING("FOLLOWING"),
        UNBOUNDED_FOLLOWING("UNBOUNDED FOLLOWING");

        public final String name;
        public final String name_lower;
        private WindowingBound(String name) {
            this.name = name;
            this.name_lower = name.toLowerCase();
        }
    }
}
