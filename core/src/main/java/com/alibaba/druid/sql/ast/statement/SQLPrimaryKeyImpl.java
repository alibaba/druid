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

import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class SQLPrimaryKeyImpl extends SQLUnique implements SQLPrimaryKey, SQLTableConstraint {
    protected boolean disableNovalidate;
    protected boolean clustered; // sql server
    protected boolean notEnforced; // bigquery

    public SQLPrimaryKeyImpl() {
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.getName());
            acceptChild(visitor, this.getColumns());
        }
        visitor.endVisit(this);
    }

    public SQLPrimaryKeyImpl clone() {
        SQLPrimaryKeyImpl x = new SQLPrimaryKeyImpl();
        cloneTo(x);
        return x;
    }

    public void cloneTo(SQLPrimaryKeyImpl x) {
        super.cloneTo(x);
        x.disableNovalidate = disableNovalidate;
        x.clustered = clustered;
    }

    public boolean isDisableNovalidate() {
        return disableNovalidate;
    }

    public void setDisableNovalidate(boolean disableNovalidate) {
        this.disableNovalidate = disableNovalidate;
    }

    public boolean isClustered() {
        return clustered;
    }

    public void setClustered(boolean clustered) {
        this.clustered = clustered;
    }

    public boolean isNotEnforced() {
        return notEnforced;
    }

    public void setNotEnforced(boolean notEnforced) {
        this.notEnforced = notEnforced;
    }
}
