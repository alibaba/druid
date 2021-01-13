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
import com.alibaba.druid.sql.ast.expr.SQLIntegerExpr;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.ArrayList;
import java.util.List;

public class SQLAlterTableDropSubpartition extends SQLObjectImpl implements SQLAlterTableItem {

    private List<SQLIntegerExpr> partitionIds = new ArrayList<SQLIntegerExpr>();
    private List<SQLIntegerExpr> subpartitionIds = new ArrayList<SQLIntegerExpr>();

    public SQLAlterTableDropSubpartition(){

    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, partitionIds);
            acceptChild(visitor, subpartitionIds);
        }
        visitor.endVisit(this);
    }

    public List<SQLIntegerExpr> getPartitionIds() {
        return partitionIds;
    }

    public void setPartitionIds(List<SQLIntegerExpr> partitionIds) {
        this.partitionIds = partitionIds;
    }

    public List<SQLIntegerExpr> getSubpartitionIds() {
        return subpartitionIds;
    }

    public void setSubpartitionIds(List<SQLIntegerExpr> subpartitionIds) {
        this.subpartitionIds = subpartitionIds;
    }
}
