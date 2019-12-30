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

public class SQLSubPartitionByList extends SQLSubPartitionBy {

    protected SQLName column;

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, column);
            acceptChild(visitor, subPartitionsCount);
        }
        visitor.endVisit(this);
    }

    public SQLName getColumn() {
        return column;
    }

    public void setColumn(SQLName column) {
        if (column != null) {
            column.setParent(this);
        }
        this.column = column;
    }

    public SQLSubPartitionByList clone() {
        SQLSubPartitionByList x = new SQLSubPartitionByList();
        if (column != null) {
            x.setColumn(column.clone());
        }
        return x;
    }

    public boolean isPartitionByColumn(long columnNameHashCode64) {
        return column != null
                && column.nameHashCode64() == columnNameHashCode64;
    }
}
