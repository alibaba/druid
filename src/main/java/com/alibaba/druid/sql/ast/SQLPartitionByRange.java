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
package com.alibaba.druid.sql.ast;

import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class SQLPartitionByRange extends SQLPartitionBy {
    protected SQLExpr       interval;

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

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, columns);
            acceptChild(visitor, interval);
            acceptChild(visitor, storeIn);
            acceptChild(visitor, partitions);
        }
        visitor.endVisit(this);
    }

    public SQLPartitionByRange clone() {
        SQLPartitionByRange x = new SQLPartitionByRange();

        if (interval != null) {
            x.setInterval(interval.clone());
        }

        for (SQLExpr column : columns) {
            SQLExpr c2 = column.clone();
            c2.setParent(x);
            x.columns.add(c2);
        }

        return x;
    }
}
