/*
 * Copyright 1999-2011 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.sql.dialect.hive.ast.stmt;

import com.alibaba.druid.sql.ast.SQLDataType;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.dialect.hive.ast.HiveSQLObjectImpl;
import com.alibaba.druid.sql.dialect.hive.ast.HiveStatement;
import com.alibaba.druid.sql.dialect.hive.visitor.HiveASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class HiveCreateTableStatement extends SQLCreateTableStatement implements HiveStatement {

    private static final long serialVersionUID = 1L;

    private PartitionedBy     partitionedBy;

    public PartitionedBy getPartitionedBy() {
        return partitionedBy;
    }

    public void setPartitionedBy(PartitionedBy partitionedBy) {
        this.partitionedBy = partitionedBy;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        this.accept0((HiveASTVisitor) visitor);
    }

    @Override
    public void accept0(HiveASTVisitor visitor) {
        if (visitor.visit(this)) {
            this.acceptChild(visitor, tableSource);
            this.acceptChild(visitor, tableElementList);
            this.acceptChild(visitor, partitionedBy);
        }
        visitor.endVisit(this);
    }

    public static class PartitionedBy extends HiveSQLObjectImpl {

        private static final long serialVersionUID = 1L;
        private String            name;
        private SQLDataType       type;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public SQLDataType getType() {
            return type;
        }

        public void setType(SQLDataType type) {
            this.type = type;
        }

        @Override
        public void accept0(HiveASTVisitor visitor) {
            if (visitor.visit(this)) {
                acceptChild(visitor, type);
            }
            visitor.endVisit(this);
        }

    }

}
