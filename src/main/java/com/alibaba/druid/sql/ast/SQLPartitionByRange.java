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
package com.alibaba.druid.sql.ast;

import com.alibaba.druid.sql.ast.expr.SQLIntegerExpr;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class SQLPartitionByRange extends SQLPartitionBy {
    protected SQLExpr interval;
    protected boolean isColumns;
    protected SQLExpr startWith;
    protected SQLIntegerExpr expireAfter;
    protected SQLIntegerExpr preAllocate;
    protected SQLExpr pivotDateExpr;
    protected boolean disableSchedule;

    public SQLPartitionByRange() {
    }

    public SQLExpr getInterval() {
        return interval;
    }

    public void setInterval(SQLExpr interval) {
        if (interval != null) {
            interval.setParent(this);
        }

        this.interval = interval;
    }

    public SQLExpr getStartWith() {
        return this.startWith;
    }

    public void setStartWith(final SQLExpr startWith) {
        if (startWith != null) {
            startWith.setParent(this);
        }

        this.startWith = startWith;
    }

    public SQLIntegerExpr getExpireAfter() {
        return this.expireAfter;
    }

    public void setExpireAfter(final SQLIntegerExpr expireAfter) {
        if (expireAfter != null) {
            expireAfter.setParent(this);
        }
        this.expireAfter = expireAfter;
    }

    public SQLIntegerExpr getPreAllocate() {
        return this.preAllocate;
    }

    public void setPreAllocate(final SQLIntegerExpr preAllocate) {
        if (preAllocate != null) {
            preAllocate.setParent(this);
        }
        this.preAllocate = preAllocate;
    }

    public SQLExpr getPivotDateExpr() {
        return this.pivotDateExpr;
    }

    public void setPivotDateExpr(final SQLExpr pivotDateExpr) {
        if (pivotDateExpr != null) {
            pivotDateExpr.setParent(this);
        }
        this.pivotDateExpr = pivotDateExpr;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, columns);
            acceptChild(visitor, interval);
            acceptChild(visitor, expireAfter);
            acceptChild(visitor, preAllocate);
            acceptChild(visitor, pivotDateExpr);
            acceptChild(visitor, columns);
            acceptChild(visitor, storeIn);
            acceptChild(visitor, partitions);
            acceptChild(visitor, subPartitionBy);
        }
        visitor.endVisit(this);
    }

    public SQLPartitionByRange clone() {
        SQLPartitionByRange x = new SQLPartitionByRange();

        this.cloneTo(x);

        if (interval != null) {
            x.setInterval(interval.clone());
        }

        if (startWith != null) {
            x.setStartWith(startWith.clone());
        }

        if (expireAfter != null) {
            x.setExpireAfter(expireAfter.clone());
        }

        for (SQLExpr column : columns) {
            SQLExpr c2 = column.clone();
            c2.setParent(x);
            x.columns.add(c2);
        }

        x.setColumns(this.isColumns);
        x.setDisableSchedule(this.disableSchedule);

        return x;
    }

    public void cloneTo(SQLPartitionByRange x) {
        super.cloneTo(x);
    }

    public boolean isColumns() {
        return isColumns;
    }

    public void setColumns(boolean columns) {
        isColumns = columns;
    }

    public boolean isDisableSchedule() {
        return this.disableSchedule;
    }

    public void setDisableSchedule(final boolean disableSchedule) {
        this.disableSchedule = disableSchedule;
    }
}
