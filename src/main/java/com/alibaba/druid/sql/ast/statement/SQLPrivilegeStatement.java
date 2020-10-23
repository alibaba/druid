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

import java.util.ArrayList;
import java.util.List;

public abstract class SQLPrivilegeStatement extends SQLStatementImpl {
    protected final List<SQLPrivilegeItem> privileges = new ArrayList<SQLPrivilegeItem>();
    protected List<SQLExpr> users = new ArrayList<SQLExpr>();
    protected SQLObject resource;

    // mysql
    protected SQLObjectType resourceType;

    public SQLPrivilegeStatement() {
    }

    public SQLPrivilegeStatement(DbType dbType) {
        super(dbType);
    }

    public List<SQLExpr> getUsers() {
        return users;
    }

    public void addUser(SQLExpr user) {
        if (user == null) {
            return;
        }
        user.setParent(this);
        this.users.add(user);
    }

    public void setUsers(List<SQLExpr> users) {
        this.users = users;
    }

    public SQLObject getResource() {
        return resource;
    }

    public void setResource(SQLObject x) {
        if (x != null) {
            x.setParent(this);
        }

        this.resource = x;
    }

    public void setResource(SQLExpr resource) {
        if (resource != null) {
            resource.setParent(this);
        }
        this.resource = resource;
    }

    public List<SQLPrivilegeItem> getPrivileges() {
        return privileges;
    }


    public SQLObjectType getResourceType() {
        return resourceType;
    }

    public void setResourceType(SQLObjectType x) {
        this.resourceType = x;
    }


}
