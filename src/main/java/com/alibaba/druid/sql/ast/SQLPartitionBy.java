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

import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;

import java.util.ArrayList;
import java.util.List;

public abstract class SQLPartitionBy extends SQLObjectImpl {
    protected SQLSubPartitionBy  subPartitionBy;
    protected SQLExpr            partitionsCount;
    protected boolean            linear;
    protected List<SQLPartition> partitions = new ArrayList<SQLPartition>();
    protected List<SQLName>      storeIn    = new ArrayList<SQLName>();
    protected List<SQLExpr>      columns    = new ArrayList<SQLExpr>();

    public List<SQLPartition> getPartitions() {
        return partitions;
    }
    
    public void addPartition(SQLPartition partition) {
        if (partition != null) {
            partition.setParent(this);
        }
        this.partitions.add(partition);
    }

    public SQLSubPartitionBy getSubPartitionBy() {
        return subPartitionBy;
    }

    public void setSubPartitionBy(SQLSubPartitionBy subPartitionBy) {
        if (subPartitionBy != null) {
            subPartitionBy.setParent(this);
        }
        this.subPartitionBy = subPartitionBy;
    }

    public SQLExpr getPartitionsCount() {
        return partitionsCount;
    }

    public void setPartitionsCount(SQLExpr partitionsCount) {
        if (partitionsCount != null) {
            partitionsCount.setParent(this);
        }
        this.partitionsCount = partitionsCount;
    }

    public boolean isLinear() {
        return linear;
    }

    public void setLinear(boolean linear) {
        this.linear = linear;
    }

    public List<SQLName> getStoreIn() {
        return storeIn;
    }

    public List<SQLExpr> getColumns() {
        return columns;
    }

    public void addColumn(SQLExpr column) {
        if (column != null) {
            column.setParent(this);
        }
        this.columns.add(column);
    }

    public void cloneTo(SQLPartitionBy x) {
        if (subPartitionBy != null) {
            x.setSubPartitionBy(subPartitionBy.clone());
        }
        if (partitionsCount != null) {
            x.setPartitionsCount(partitionsCount.clone());
        }
        x.linear = linear;
        for (SQLPartition p : partitions) {
            SQLPartition p2 = p.clone();
            p2.setParent(x);
            x.partitions.add(p2);
        }
        for (SQLName name : storeIn) {
            SQLName name2 = name.clone();
            name2.setParent(x);
            x.storeIn.add(name2);
        }
    }

    public boolean isPartitionByColumn(long columnNameHashCode64) {
        for (SQLExpr column : columns) {
            if (column instanceof SQLIdentifierExpr
                    && ((SQLIdentifierExpr) column)
                    .nameHashCode64() == columnNameHashCode64) {
                return true;
            }
        }

        if (subPartitionBy != null) {
            return subPartitionBy.isPartitionByColumn(columnNameHashCode64);
        }
        return false;
    }

    public abstract SQLPartitionBy clone();
}
