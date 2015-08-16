/*
 * Copyright 1999-2101 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.sql.dialect.sqlserver.ast;

import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
import com.alibaba.druid.sql.dialect.sqlserver.visitor.SQLServerASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class SQLServerColumnDefinition extends SQLColumnDefinition implements SQLServerObject {

    private Identity identity;

    public SQLServerColumnDefinition(){

    }

    public Identity getIdentity() {
        return identity;
    }

    public void setIdentity(Identity identity) {
        if (identity != null) {
            identity.setParent(this);
        }
        this.identity = identity;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        this.accept0((SQLServerASTVisitor) visitor);
    }

    @Override
    public void accept0(SQLServerASTVisitor visitor) {
        if (visitor.visit(this)) {
            this.acceptChild(visitor, name);
            this.acceptChild(visitor, dataType);
            this.acceptChild(visitor, defaultExpr);
            this.acceptChild(visitor, constraints);
            this.acceptChild(visitor, identity);
        }
        visitor.endVisit(this);
    }

    public static class Identity extends SQLServerObjectImpl {

        private Integer seed;
        private Integer increment;

        private boolean notForReplication;

        public Identity(){

        }

        public Integer getSeed() {
            return seed;
        }

        public void setSeed(Integer seed) {
            this.seed = seed;
        }

        public Integer getIncrement() {
            return increment;
        }

        public void setIncrement(Integer increment) {
            this.increment = increment;
        }

        public boolean isNotForReplication() {
            return notForReplication;
        }

        public void setNotForReplication(boolean notForReplication) {
            this.notForReplication = notForReplication;
        }

        @Override
        public void accept0(SQLServerASTVisitor visitor) {
            visitor.visit(this);
            visitor.endVisit(this);
        }

    }
}
