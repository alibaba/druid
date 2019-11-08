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

public class SQLSubPartitionByHash extends SQLSubPartitionBy {

    protected SQLExpr expr;

    // for aliyun ads
    private boolean   key;

    public SQLExpr getExpr() {
        return expr;
    }

    public void setExpr(SQLExpr expr) {
        if (expr != null) {
            expr.setParent(this);
        }
        this.expr = expr;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, expr);
            acceptChild(visitor, subPartitionsCount);
        }
        visitor.endVisit(this);
    }

    public boolean isKey() {
        return key;
    }

    public void setKey(boolean key) {
        this.key = key;
    }

    public SQLSubPartitionByHash clone() {
        SQLSubPartitionByHash x = new SQLSubPartitionByHash();
        if (expr != null) {
            x.setExpr(expr.clone());
        }
        x.key = key;
        return x;
    }

    public boolean isPartitionByColumn(long columnNameHashCode64) {
        return expr instanceof SQLName
                && ((SQLName) expr).nameHashCode64() == columnNameHashCode64;
    }
}
