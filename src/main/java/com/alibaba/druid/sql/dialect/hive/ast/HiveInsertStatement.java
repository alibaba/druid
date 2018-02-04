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
package com.alibaba.druid.sql.dialect.hive.ast;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLAssignItem;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import com.alibaba.druid.sql.dialect.hive.visitor.HiveASTVisitor;
import com.alibaba.druid.sql.dialect.odps.visitor.OdpsASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;
import com.alibaba.druid.util.JdbcConstants;

import java.util.ArrayList;
import java.util.List;

public class HiveInsertStatement extends SQLInsertStatement implements SQLStatement {
    private boolean              overwrite  = false;
    private List<SQLAssignItem>  partitions = new ArrayList<SQLAssignItem>();

    public HiveInsertStatement() {
        dbType = JdbcConstants.HIVE;
    }

    @Override
    public String getDbType() {
        return dbType;
    }

    public boolean isOverwrite() {
        return overwrite;
    }

    public void setOverwrite(boolean overwrite) {
        this.overwrite = overwrite;
    }

    public List<SQLAssignItem> getPartitions() {
        return partitions;
    }

    public HiveInsertStatement clone() {
        HiveInsertStatement x = new HiveInsertStatement();
        super.cloneTo(x);
        return x;
    }

    public void addPartition(SQLAssignItem partition) {
        if (partition != null) {
            partition.setParent(this);
        }
        this.partitions.add(partition);
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor instanceof OdpsASTVisitor) {
            accept0((OdpsASTVisitor) visitor);
        } else if (visitor instanceof HiveASTVisitor) {
            accept0((HiveASTVisitor) visitor);
        } else {
            super.accept0(visitor);
        }
    }

    protected void accept0(HiveASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, tableSource);
            acceptChild(visitor, partitions);
            acceptChild(visitor, valuesList);
            acceptChild(visitor, query);
        }
        visitor.endVisit(this);
    }

    protected void accept0(OdpsASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, tableSource);
            acceptChild(visitor, partitions);
            acceptChild(visitor, valuesList);
            acceptChild(visitor, query);
        }
        visitor.endVisit(this);
    }
}
