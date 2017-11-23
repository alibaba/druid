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

public final class SQLKeep extends SQLObjectImpl {

    protected DenseRank  denseRank;

    protected SQLOrderBy orderBy;

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.orderBy);
        }
        visitor.endVisit(this);
    }

    public DenseRank getDenseRank() {
        return denseRank;
    }

    public void setDenseRank(DenseRank denseRank) {
        this.denseRank = denseRank;
    }

    public SQLOrderBy getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(SQLOrderBy orderBy) {
        if (orderBy != null) {
            orderBy.setParent(this);
        }
        this.orderBy = orderBy;
    }


    public SQLKeep clone() {
        SQLKeep x = new SQLKeep();

        x.denseRank = denseRank;

        if (orderBy != null) {
            x.setOrderBy(orderBy.clone());
        }

        return x;
    }

    public static enum DenseRank {
                                  FIRST, //
                                  LAST
    }
}
