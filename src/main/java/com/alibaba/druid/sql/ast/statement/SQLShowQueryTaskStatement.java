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

import com.alibaba.druid.sql.ast.*;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class SQLShowQueryTaskStatement extends SQLStatementImpl implements SQLShowStatement, SQLReplaceable {
    private boolean    full;
    private SQLExpr    where;
    private SQLOrderBy orderBy;
    private SQLLimit   limit;
    private SQLExpr    user;

    public SQLShowQueryTaskStatement() {

    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, where);
            acceptChild(visitor, orderBy);
            acceptChild(visitor, limit);
            acceptChild(visitor, user);
        }
        visitor.endVisit(this);
    }

    public SQLExpr getWhere() {
        return where;
    }

    public void setWhere(SQLExpr x) {
        if (x != null) {
            x.setParent(this);
        }
        this.where = x;
    }

    public SQLExpr getUser() {
        return user;
    }

    public SQLExpr getFor() {
        return user;
    }

    public void setUser(SQLExpr user) {
        this.user = user;
    }

    public SQLOrderBy getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(SQLOrderBy x) {
        if (x != null) {
            x.setParent(this);
        }
        this.orderBy = x;
    }

    public SQLLimit getLimit() {
        return limit;
    }

    public void setLimit(SQLLimit x) {
        if (x != null) {
            x.setParent(this);
        }
        this.limit = x;
    }

    @Override
    public boolean replace(SQLExpr expr, SQLExpr target) {
        if (where == expr) {
            setWhere(target);
            return true;
        }

        if (user == expr) {
            setUser(target);
            return true;
        }

        return false;
    }

    public boolean isFull() {
        return full;
    }

    public void setFull(boolean full) {
        this.full = full;
    }
}
