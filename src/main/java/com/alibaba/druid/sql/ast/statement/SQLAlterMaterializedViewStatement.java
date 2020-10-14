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

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLStatementImpl;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class SQLAlterMaterializedViewStatement extends SQLStatementImpl implements SQLAlterStatement {
    private SQLName name;

    private boolean refreshFast;
    private boolean refreshComplete;
    private boolean refreshForce;
    private boolean refreshOnCommit;
    private boolean refreshOnDemand;
    private boolean refreshStartWith;
    private boolean refreshNext;

    private Boolean enableQueryRewrite;

    private SQLExpr startWith;
    private SQLExpr next;

    // for ADB
    protected boolean refreshOnOverWrite;


    public SQLName getName() {
        return name;
    }

    public void setName(SQLName name) {
        if (name != null) {
            name.setParent(this);
        }
        this.name = name;
    }


    public boolean isRefresh() {
        return refreshFast || refreshComplete || refreshForce || refreshOnDemand || refreshOnCommit || refreshStartWith || refreshNext || refreshOnOverWrite;
    }

    public boolean isRefreshFast() {
        return refreshFast;
    }

    public void setRefreshFast(boolean refreshFast) {
        this.refreshFast = refreshFast;
    }

    public boolean isRefreshComplete() {
        return refreshComplete;
    }

    public void setRefreshComplete(boolean refreshComplete) {
        this.refreshComplete = refreshComplete;
    }

    public boolean isRefreshForce() {
        return refreshForce;
    }

    public void setRefreshForce(boolean refreshForce) {
        this.refreshForce = refreshForce;
    }

    public boolean isRefreshOnCommit() {
        return refreshOnCommit;
    }

    public void setRefreshOnCommit(boolean refreshOnCommit) {
        this.refreshOnCommit = refreshOnCommit;
    }

    public boolean isRefreshOnDemand() {
        return refreshOnDemand;
    }

    public void setRefreshOnDemand(boolean refreshOnDemand) {
        this.refreshOnDemand = refreshOnDemand;
    }

    public boolean isRefreshOnOverWrite() {
        return refreshOnOverWrite;
    }

    public void setRefreshOnOverWrite(boolean refreshOnOverWrite) {
        this.refreshOnOverWrite = refreshOnOverWrite;
    }

    public boolean isRefreshStartWith() {
        return refreshStartWith;
    }

    public void setRefreshStartWith(boolean refreshStartWith) {
        this.refreshStartWith = refreshStartWith;
    }

    public boolean isRefreshNext() {
        return refreshNext;
    }

    public void setRefreshNext(boolean refreshNext) {
        this.refreshNext = refreshNext;
    }

    public Boolean getEnableQueryRewrite() {
        return enableQueryRewrite;
    }

    public void setEnableQueryRewrite(Boolean enableQueryRewrite) {
        this.enableQueryRewrite = enableQueryRewrite;
    }


    public SQLExpr getStartWith() {
        return startWith;
    }

    public void setStartWith(SQLExpr x) {
        if (x != null) {
            x.setParent(this);
        }
        this.startWith = x;
    }

    public SQLExpr getNext() {
        return next;
    }

    public void setNext(SQLExpr x) {
        if (x != null) {
            x.setParent(this);
        }
        this.next = x;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, name);
            acceptChild(visitor, startWith);
            acceptChild(visitor, next);
        }
        visitor.endVisit(this);
    }
}
