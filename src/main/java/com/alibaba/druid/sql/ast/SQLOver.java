package com.alibaba.druid.sql.ast;

import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.ArrayList;
import java.util.List;

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
public class SQLOver extends SQLObjectImpl {

    protected final List<SQLExpr> partitionBy = new ArrayList<SQLExpr>();
    protected SQLOrderBy          orderBy;

    // for db2
    protected SQLName             of;

    protected SQLExpr             windowing;
    protected WindowingType       windowingType = WindowingType.ROWS;

    protected boolean             windowingPreceding;
    protected boolean             windowingFollowing;

    protected SQLExpr             windowingBetweenBegin;
    protected boolean             windowingBetweenBeginPreceding;
    protected boolean             windowingBetweenBeginFollowing;

    protected SQLExpr             windowingBetweenEnd;
    protected boolean             windowingBetweenEndPreceding;
    protected boolean             windowingBetweenEndFollowing;

    public SQLOver(){

    }

    public SQLOver(SQLOrderBy orderBy){
        this.setOrderBy(orderBy);
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.partitionBy);
            acceptChild(visitor, this.orderBy);
            acceptChild(visitor, this.of);
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

    public SQLExpr getWindowing() {
        return windowing;
    }

    public void setWindowing(SQLExpr windowing) {
        this.windowing = windowing;
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

    public boolean isWindowingFollowing() {
        return windowingFollowing;
    }

    public void setWindowingFollowing(boolean windowingFollowing) {
        this.windowingFollowing = windowingFollowing;
    }

    public SQLExpr getWindowingBetweenBegin() {
        return windowingBetweenBegin;
    }

    public void setWindowingBetweenBegin(SQLExpr windowingBetweenBegin) {
        this.windowingBetweenBegin = windowingBetweenBegin;
    }

    public boolean isWindowingBetweenBeginPreceding() {
        return windowingBetweenBeginPreceding;
    }

    public void setWindowingBetweenBeginPreceding(boolean windowingBetweenBeginPreceding) {
        this.windowingBetweenBeginPreceding = windowingBetweenBeginPreceding;
    }

    public boolean isWindowingBetweenBeginFollowing() {
        return windowingBetweenBeginFollowing;
    }

    public void setWindowingBetweenBeginFollowing(boolean windowingBetweenBeginFollowing) {
        this.windowingBetweenBeginFollowing = windowingBetweenBeginFollowing;
    }

    public SQLExpr getWindowingBetweenEnd() {
        return windowingBetweenEnd;
    }

    public void setWindowingBetweenEnd(SQLExpr windowingBetweenEnd) {
        this.windowingBetweenEnd = windowingBetweenEnd;
    }

    public boolean isWindowingBetweenEndPreceding() {
        return windowingBetweenEndPreceding;
    }

    public void setWindowingBetweenEndPreceding(boolean windowingBetweenEndPreceding) {
        this.windowingBetweenEndPreceding = windowingBetweenEndPreceding;
    }

    public boolean isWindowingBetweenEndFollowing() {
        return windowingBetweenEndFollowing;
    }

    public void setWindowingBetweenEndFollowing(boolean windowingBetweenEndFollowing) {
        this.windowingBetweenEndFollowing = windowingBetweenEndFollowing;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SQLOver sqlOver = (SQLOver) o;

        if (windowingPreceding != sqlOver.windowingPreceding) return false;
        if (windowingFollowing != sqlOver.windowingFollowing) return false;
        if (windowingBetweenBeginPreceding != sqlOver.windowingBetweenBeginPreceding) return false;
        if (windowingBetweenBeginFollowing != sqlOver.windowingBetweenBeginFollowing) return false;
        if (windowingBetweenEndPreceding != sqlOver.windowingBetweenEndPreceding) return false;
        if (windowingBetweenEndFollowing != sqlOver.windowingBetweenEndFollowing) return false;
        if (partitionBy != null ? !partitionBy.equals(sqlOver.partitionBy) : sqlOver.partitionBy != null) return false;
        if (orderBy != null ? !orderBy.equals(sqlOver.orderBy) : sqlOver.orderBy != null) return false;
        if (of != null ? !of.equals(sqlOver.of) : sqlOver.of != null) return false;
        if (windowing != null ? !windowing.equals(sqlOver.windowing) : sqlOver.windowing != null) return false;
        if (windowingType != sqlOver.windowingType) return false;
        if (windowingBetweenBegin != null ? !windowingBetweenBegin.equals(sqlOver.windowingBetweenBegin) : sqlOver.windowingBetweenBegin != null)
            return false;
        return windowingBetweenEnd != null ? windowingBetweenEnd.equals(sqlOver.windowingBetweenEnd) : sqlOver.windowingBetweenEnd == null;

    }

    @Override
    public int hashCode() {
        int result = partitionBy != null ? partitionBy.hashCode() : 0;
        result = 31 * result + (orderBy != null ? orderBy.hashCode() : 0);
        result = 31 * result + (of != null ? of.hashCode() : 0);
        result = 31 * result + (windowing != null ? windowing.hashCode() : 0);
        result = 31 * result + (windowingType != null ? windowingType.hashCode() : 0);
        result = 31 * result + (windowingPreceding ? 1 : 0);
        result = 31 * result + (windowingFollowing ? 1 : 0);
        result = 31 * result + (windowingBetweenBegin != null ? windowingBetweenBegin.hashCode() : 0);
        result = 31 * result + (windowingBetweenBeginPreceding ? 1 : 0);
        result = 31 * result + (windowingBetweenBeginFollowing ? 1 : 0);
        result = 31 * result + (windowingBetweenEnd != null ? windowingBetweenEnd.hashCode() : 0);
        result = 31 * result + (windowingBetweenEndPreceding ? 1 : 0);
        result = 31 * result + (windowingBetweenEndFollowing ? 1 : 0);
        return result;
    }

    public void cloneTo(SQLOver x) {
        for (SQLExpr item : partitionBy) {
            SQLExpr item1 = item.clone();
            item1.setParent(x);
            x.partitionBy.add(item);
        }

        if (orderBy != null) {
            x.setOrderBy(orderBy.clone());
        }

        if (of != null) {
            x.setOf(of.clone());
        }

        if (windowing != null) {
            x.setWindowing(windowing.clone());
        }
        x.windowingType = windowingType;
        x.windowingPreceding = windowingPreceding;
        x.windowingFollowing = windowingFollowing;

        if (windowingBetweenBegin != null) {
            x.setWindowingBetweenBegin(windowingBetweenBegin.clone());
        }
        x.windowingBetweenBeginPreceding = windowingBetweenBeginPreceding;
        x.windowingBetweenBeginFollowing = windowingBetweenBeginFollowing;

        if (windowingBetweenEnd != null) {
            x.setWindowingBetweenEnd(windowingBetweenEnd.clone());
        }
        x.windowingBetweenEndPreceding = windowingBetweenEndPreceding;
        x.windowingBetweenEndFollowing = windowingBetweenEndFollowing;
    }

    public SQLOver clone() {
        SQLOver x = new SQLOver();
        cloneTo(x);
        return x;
    }

    public static enum WindowingType {
        ROWS, RANGE
    }
}
