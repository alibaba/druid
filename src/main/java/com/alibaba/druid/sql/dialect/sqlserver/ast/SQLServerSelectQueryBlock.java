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
package com.alibaba.druid.sql.dialect.sqlserver.ast;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.expr.SQLIntegerExpr;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.dialect.sqlserver.visitor.SQLServerASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class SQLServerSelectQueryBlock extends SQLSelectQueryBlock {

    private SQLServerTop top;

    public SQLServerSelectQueryBlock() {
        dbType = DbType.sqlserver;
    }

    public SQLServerTop getTop() {
        return top;
    }

    public void setTop(SQLServerTop top) {
        if (top != null) {
            top.setParent(this);
        }
        this.top = top;
    }

    public void setTop(int rowCount) {
        setTop(new SQLServerTop(new SQLIntegerExpr(rowCount)));
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor instanceof SQLServerASTVisitor) {
            accept0((SQLServerASTVisitor) visitor);
        } else {
            super.accept0(visitor);
        }
    }

    protected void accept0(SQLServerASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.top);
            acceptChild(visitor, this.selectList);
            acceptChild(visitor, this.from);
            acceptChild(visitor, this.where);
            acceptChild(visitor, this.groupBy);
        }
        visitor.endVisit(this);
    }

    public void limit(int rowCount, int offset) {
        if (offset <= 0) {
            setTop(rowCount);
        } else {
            throw new UnsupportedOperationException("not support offset");
        }
    }
}
