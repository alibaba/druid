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
package com.alibaba.druid.sql.dialect.starrocks.ast;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLPartitionBy;
import com.alibaba.druid.sql.dialect.starrocks.visitor.StarRocksASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

/**
 * The {@code PARTITION BY} clause of a StarRocks asynchronous materialized view, whose key is a bare
 * expression or column list with no {@code RANGE}/{@code LIST} keyword, e.g.
 * {@code PARTITION BY date_trunc('day', dt)} or {@code PARTITION BY (dt)}.
 * <p>
 * This deliberately is <em>not</em> a {@link com.alibaba.druid.sql.ast.SQLPartitionByRange}: the base
 * output visitor unconditionally prints the {@code RANGE} keyword for that node, which would corrupt
 * the SQL on round-trip.
 */
public class StarRocksPartitionByExpr extends SQLPartitionBy {
    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor instanceof StarRocksASTVisitor) {
            StarRocksASTVisitor v = (StarRocksASTVisitor) visitor;
            if (v.visit(this)) {
                acceptChildren(visitor);
            }
            v.endVisit(this);
            return;
        }
        acceptChildren(visitor);
    }

    private void acceptChildren(SQLASTVisitor visitor) {
        acceptChild(visitor, columns);
        acceptChild(visitor, partitions);
        acceptChild(visitor, subPartitionBy);
    }

    @Override
    public StarRocksPartitionByExpr clone() {
        StarRocksPartitionByExpr x = new StarRocksPartitionByExpr();
        cloneTo(x);

        for (SQLExpr column : columns) {
            SQLExpr c2 = column.clone();
            c2.setParent(x);
            x.columns.add(c2);
        }

        return x;
    }
}
