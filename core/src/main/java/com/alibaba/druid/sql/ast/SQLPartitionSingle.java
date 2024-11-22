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

import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.ArrayList;
import java.util.List;

public class SQLPartitionSingle extends SQLPartition {
    protected SQLName name;

    protected SQLExpr subPartitionsCount;
    protected List<SQLSubPartition> subPartitions = new ArrayList<>();

    protected SQLPartitionValue values;
    protected SQLName tablespace;
    protected SQLExpr locality;

    public SQLExpr getLocality() {
        return locality;
    }

    public void setLocality(SQLExpr x) {
        if (x != null) {
            x.setParent(this);
        }
        this.locality = x;
    }

    public SQLName getTablespace() {
        return tablespace;
    }

    public void setTablespace(SQLName tablespace) {
        this.tablespace = tablespace;
    }

    public SQLName getName() {
        return name;
    }

    public void setName(SQLName name) {
        if (name != null) {
            name.setParent(this);
        }
        this.name = name;
    }

    public SQLExpr getSubPartitionsCount() {
        return subPartitionsCount;
    }

    public void setSubPartitionsCount(SQLExpr subPartitionsCount) {
        if (subPartitionsCount != null) {
            subPartitionsCount.setParent(this);
        }
        this.subPartitionsCount = subPartitionsCount;
    }

    public SQLPartitionValue getValues() {
        return values;
    }

    public void setValues(SQLPartitionValue values) {
        if (values != null) {
            values.setParent(this);
        }
        this.values = values;
    }

    public List<SQLSubPartition> getSubPartitions() {
        return subPartitions;
    }

    public void addSubPartition(SQLSubPartition partition) {
        if (partition != null) {
            partition.setParent(this);
        }
        this.subPartitions.add(partition);
    }

    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, name);
            acceptChild(visitor, values);
            acceptChild(visitor, subPartitionsCount);
            acceptChild(visitor, subPartitions);
            acceptChild(visitor, tablespace);
            acceptChild(visitor, locality);
        }
        visitor.endVisit(this);
    }

    @Override
    public SQLPartitionSingle clone() {
        SQLPartitionSingle x = new SQLPartitionSingle();

        if (name != null) {
            x.setName(name.clone());
        }

        if (subPartitionsCount != null) {
            x.setSubPartitionsCount(subPartitionsCount.clone());
        }

        for (SQLSubPartition p : subPartitions) {
            SQLSubPartition p2 = p.clone();
            p2.setParent(x);
            x.subPartitions.add(p2);
        }

        if (values != null) {
            x.setValues(values.clone());
        }

        if (locality != null) {
            x.setLocality(locality.clone());
        }

        if (tablespace != null) {
            x.setTablespace(tablespace.clone());
        }
        return x;
    }
}
