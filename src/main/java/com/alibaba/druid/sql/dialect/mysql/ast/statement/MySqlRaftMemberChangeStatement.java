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
package com.alibaba.druid.sql.dialect.mysql.ast.statement;

import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import com.alibaba.druid.sql.ast.expr.SQLIntegerExpr;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitor;

public class MySqlRaftMemberChangeStatement extends MySqlStatementImpl {

    private SQLCharExpr shard;
    private SQLCharExpr host;
    private SQLCharExpr status;
    private boolean force;
    private boolean noLeader;

    @Override
    public void accept0(MySqlASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, shard);
            acceptChild(visitor, host);
            acceptChild(visitor, status);
        }
        visitor.endVisit(this);
    }

    public SQLCharExpr getShard() {
        return shard;
    }

    public void setShard(SQLCharExpr shard) {
        this.shard = shard;
    }

    public SQLCharExpr getHost() {
        return host;
    }

    public void setHost(SQLCharExpr host) {
        this.host = host;
    }

    public SQLCharExpr getStatus() {
        return status;
    }

    public void setStatus(SQLCharExpr status) {
        this.status = status;
    }

    public boolean isForce() {
        return force;
    }

    public void setForce(boolean force) {
        this.force = force;
    }

    public boolean isNoLeader() {
        return noLeader;
    }

    public void setNoLeader(boolean noLeader) {
        this.noLeader = noLeader;
    }
}
