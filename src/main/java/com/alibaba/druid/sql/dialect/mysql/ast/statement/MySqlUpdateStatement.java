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
package com.alibaba.druid.sql.dialect.mysql.ast.statement;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLOrderBy;
import com.alibaba.druid.sql.ast.statement.SQLUpdateStatement;
import com.alibaba.druid.sql.ast.SQLLimit;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;
import com.alibaba.druid.util.JdbcConstants;

public class MySqlUpdateStatement extends SQLUpdateStatement implements MySqlStatement {
    private SQLLimit limit;

    private boolean             lowPriority        = false;
    private boolean             ignore             = false;
    private boolean             commitOnSuccess    = false;
    private boolean             rollBackOnFail     = false;
    private boolean             queryOnPk          = false;
    private SQLExpr             targetAffectRow;

    // for petadata
    private boolean             forceAllPartitions = false;
    private SQLName             forcePartition;

    public MySqlUpdateStatement(){
        super(JdbcConstants.MYSQL);
    }

    public SQLLimit getLimit() {
        return limit;
    }

    public void setLimit(SQLLimit limit) {
        if (limit != null) {
            limit.setParent(this);
        }
        this.limit = limit;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor instanceof MySqlASTVisitor) {
            accept0((MySqlASTVisitor) visitor);
        } else {
            super.accept0(visitor);
        }
    }

    public void accept0(MySqlASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, tableSource);
            acceptChild(visitor, items);
            acceptChild(visitor, where);
            acceptChild(visitor, orderBy);
            acceptChild(visitor, limit);
        }
        visitor.endVisit(this);
    }

    public boolean isLowPriority() {
        return lowPriority;
    }

    public void setLowPriority(boolean lowPriority) {
        this.lowPriority = lowPriority;
    }

    public boolean isIgnore() {
        return ignore;
    }

    public void setIgnore(boolean ignore) {
        this.ignore = ignore;
    }

    public boolean isCommitOnSuccess() {
        return commitOnSuccess;
    }

    public void setCommitOnSuccess(boolean commitOnSuccess) {
        this.commitOnSuccess = commitOnSuccess;
    }

    public boolean isRollBackOnFail() {
        return rollBackOnFail;
    }

    public void setRollBackOnFail(boolean rollBackOnFail) {
        this.rollBackOnFail = rollBackOnFail;
    }

    public boolean isQueryOnPk() {
        return queryOnPk;
    }

    public void setQueryOnPk(boolean queryOnPk) {
        this.queryOnPk = queryOnPk;
    }

    public SQLExpr getTargetAffectRow() {
        return targetAffectRow;
    }

    public void setTargetAffectRow(SQLExpr targetAffectRow) {
        if (targetAffectRow != null) {
            targetAffectRow.setParent(this);
        }
        this.targetAffectRow = targetAffectRow;
    }

    public boolean isForceAllPartitions() {
        return forceAllPartitions;
    }

    public void setForceAllPartitions(boolean forceAllPartitions) {
        this.forceAllPartitions = forceAllPartitions;
    }

    public SQLName getForcePartition() {
        return forcePartition;
    }

    public void setForcePartition(SQLName x) {
        if (x != null) {
            x.setParent(this);
        }
        this.forcePartition = x;
    }
}
