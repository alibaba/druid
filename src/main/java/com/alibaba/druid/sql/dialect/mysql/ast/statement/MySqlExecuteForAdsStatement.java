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

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitor;

import java.util.ArrayList;
import java.util.List;

public class MySqlExecuteForAdsStatement extends MySqlStatementImpl {

    private SQLName action;
    private SQLName role;
    private SQLCharExpr targetId;
    private SQLName status;

    @Override
    public void accept0(MySqlASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, action);
            acceptChild(visitor, role);
            acceptChild(visitor, targetId);
            acceptChild(visitor, status);
        }
        visitor.endVisit(this);
    }

    public SQLName getAction() {
        return action;
    }

    public void setAction(SQLName action) {
        this.action = action;
    }

    public SQLName getRole() {
        return role;
    }

    public void setRole(SQLName role) {
        this.role = role;
    }

    public SQLCharExpr getTargetId() {
        return targetId;
    }

    public void setTargetId(SQLCharExpr targetId) {
        this.targetId = targetId;
    }

    public SQLName getStatus() {
        return status;
    }

    public void setStatus(SQLName status) {
        this.status = status;
    }

    @Override
    public List<SQLObject> getChildren() {
        List<SQLObject> children = new ArrayList<SQLObject>();
        if (action != null) {
            children.add(action);
        }
        if (role != null) {
            children.add(role);
        }
        if (targetId != null) {
            children.add(targetId);
        }
        if (status != null) {
            children.add(status);
        }
        return children;
    }
}
