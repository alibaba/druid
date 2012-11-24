package com.alibaba.druid.sql.ast;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.visitor.SQLASTVisitor;

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
public class SQLOver extends SQLObjectImpl {

    private static final long     serialVersionUID = 1L;

    protected final List<SQLExpr> partitionBy      = new ArrayList<SQLExpr>();
    protected SQLOrderBy          orderBy;

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.partitionBy);
            acceptChild(visitor, this.orderBy);
        }
        visitor.endVisit(this);
    }

    public SQLOrderBy getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(SQLOrderBy orderBy) {
        this.orderBy = orderBy;
    }

    public List<SQLExpr> getPartitionBy() {
        return partitionBy;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((orderBy == null) ? 0 : orderBy.hashCode());
        result = prime * result + ((partitionBy == null) ? 0 : partitionBy.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        SQLOver other = (SQLOver) obj;
        if (orderBy == null) {
            if (other.orderBy != null) {
                return false;
            }
        } else if (!orderBy.equals(other.orderBy)) {
            return false;
        }
        if (partitionBy == null) {
            if (other.partitionBy != null) {
                return false;
            }
        } else if (!partitionBy.equals(other.partitionBy)) {
            return false;
        }
        return true;
    }

}
