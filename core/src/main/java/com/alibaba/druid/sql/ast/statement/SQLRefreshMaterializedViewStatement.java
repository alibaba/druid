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

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.SQLStatementImpl;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.ArrayList;
import java.util.List;

public class SQLRefreshMaterializedViewStatement extends SQLStatementImpl {
    private SQLExpr name;

    private boolean concurrently;

    private boolean withNoData;

    private boolean withData;

    private boolean force;
    private boolean syncMode;
    private boolean asyncMode;

    public SQLRefreshMaterializedViewStatement() {
        this.setConcurrently(false);
        this.setWithData(false);
        this.setWithNoData(false);
    }

    public SQLRefreshMaterializedViewStatement(DbType dbType) {
        super(dbType);
        this.setConcurrently(false);
        this.setWithData(false);
        this.setWithNoData(false);
    }

    @Override
    public SQLRefreshMaterializedViewStatement clone() {
        SQLRefreshMaterializedViewStatement x = new SQLRefreshMaterializedViewStatement(getDbType());
        if (name != null) {
            x.setName(name.clone());
        }
        x.concurrently = concurrently;
        x.withNoData = withNoData;
        x.withData = withData;
        x.force = force;
        x.syncMode = syncMode;
        x.asyncMode = asyncMode;
        x.afterSemi = afterSemi;
        return x;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, name);
        }
        visitor.endVisit(this);
    }

    public SQLExpr getName() {
        return name;
    }

    public void setName(SQLExpr x) {
        if (x != null) {
            x.setParent(this);
        }
        this.name = x;
    }

    public boolean isConcurrently() {
        return concurrently;
    }

    public void setConcurrently(boolean concurrently) {
        this.concurrently = concurrently;
    }

    public boolean isWithNoData() {
        return withNoData;
    }

    public void setWithNoData(boolean withNoData) {
        this.withNoData = withNoData;
    }

    public void setWithData(boolean withData) {
        this.withData = withData;
    }

    public boolean isWithData() {
        return withData;
    }

    public boolean isForce() {
        return force;
    }

    public void setForce(boolean force) {
        this.force = force;
    }

    public boolean isSyncMode() {
        return syncMode;
    }

    public void setSyncMode(boolean syncMode) {
        this.syncMode = syncMode;
    }

    public boolean isAsyncMode() {
        return asyncMode;
    }

    public void setAsyncMode(boolean asyncMode) {
        this.asyncMode = asyncMode;
    }

    @Override
    public List<SQLObject> getChildren() {
        List<SQLObject> children = new ArrayList<SQLObject>();
        if (name != null) {
            children.add(name);
        }
        return children;
    }

}
