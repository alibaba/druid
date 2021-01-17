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

import com.alibaba.druid.sql.ast.SQLObjectImpl;
import com.alibaba.druid.sql.ast.SQLPartitionSpec;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.ArrayList;
import java.util.List;

public class SQLAlterTableMergePartition extends SQLObjectImpl implements SQLAlterTableItem {

    private final List<SQLPartitionSpec> partitions  = new ArrayList<SQLPartitionSpec>(4);
    private boolean ifExists = false;
    private SQLPartitionSpec overwritePartition;

    public List<SQLPartitionSpec> getPartitions() {
        return partitions;
    }

    public void addPartition(SQLPartitionSpec x) {
        if (x != null) {
            x.setParent(this);
        }
        this.partitions.add(x);
    }

    public boolean isIfExists() {
        return ifExists;
    }

    public void setIfExists(boolean ifExists) {
        this.ifExists = ifExists;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, partitions);
        }
        visitor.endVisit(this);
    }

    public SQLPartitionSpec getOverwritePartition() {
        return overwritePartition;
    }

    public void setOverwritePartition(SQLPartitionSpec x) {
        if (x != null) {
            x.setParent(this);
        }
        this.overwritePartition = x;
    }
}
