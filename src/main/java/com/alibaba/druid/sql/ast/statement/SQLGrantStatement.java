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
package com.alibaba.druid.sql.ast.statement;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.SQLStatementImpl;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class SQLGrantStatement extends SQLStatementImpl {

    protected final List<SQLExpr> privileges = new ArrayList<SQLExpr>();

    protected SQLObject           on;
    protected SQLExpr             to;

    public SQLGrantStatement(){

    }

    public SQLGrantStatement(String dbType){
        super(dbType);
    }

    // mysql
    protected SQLObjectType objectType;
    private SQLExpr         maxQueriesPerHour;
    private SQLExpr         maxUpdatesPerHour;
    private SQLExpr         maxConnectionsPerHour;
    private SQLExpr         maxUserConnections;

    private boolean         adminOption;

    private SQLExpr         identifiedBy;
    private String          identifiedByPassword;

    private boolean         withGrantOption;

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.privileges);
            acceptChild(visitor, on);
            acceptChild(visitor, to);
            acceptChild(visitor, identifiedBy);
        }
        visitor.endVisit(this);
    }

    @Override
    public List<SQLObject> getChildren() {
        List<SQLObject> children = new ArrayList<SQLObject>();
        children.addAll(privileges);
        if (on != null) {
            children.add(on);
        }
        if (to != null) {
            children.add(to);
        }
        if (identifiedBy != null) {
            children.add(identifiedBy);
        }
        return children;
    }

    public SQLObjectType getObjectType() {
        return objectType;
    }

    public void setObjectType(SQLObjectType objectType) {
        this.objectType = objectType;
    }

    public SQLObject getOn() {
        return on;
    }

    public void setOn(SQLObject on) {
        this.on = on;
        on.setParent(this);
    }

    public SQLExpr getTo() {
        return to;
    }

    public void setTo(SQLExpr to) {
        this.to = to;
    }

    public List<SQLExpr> getPrivileges() {
        return privileges;
    }

    public SQLExpr getMaxQueriesPerHour() {
        return maxQueriesPerHour;
    }

    public void setMaxQueriesPerHour(SQLExpr maxQueriesPerHour) {
        this.maxQueriesPerHour = maxQueriesPerHour;
    }

    public SQLExpr getMaxUpdatesPerHour() {
        return maxUpdatesPerHour;
    }

    public void setMaxUpdatesPerHour(SQLExpr maxUpdatesPerHour) {
        this.maxUpdatesPerHour = maxUpdatesPerHour;
    }

    public SQLExpr getMaxConnectionsPerHour() {
        return maxConnectionsPerHour;
    }

    public void setMaxConnectionsPerHour(SQLExpr maxConnectionsPerHour) {
        this.maxConnectionsPerHour = maxConnectionsPerHour;
    }

    public SQLExpr getMaxUserConnections() {
        return maxUserConnections;
    }

    public void setMaxUserConnections(SQLExpr maxUserConnections) {
        this.maxUserConnections = maxUserConnections;
    }

    public boolean isAdminOption() {
        return adminOption;
    }

    public void setAdminOption(boolean adminOption) {
        this.adminOption = adminOption;
    }

    public SQLExpr getIdentifiedBy() {
        return identifiedBy;
    }

    public void setIdentifiedBy(SQLExpr identifiedBy) {
        this.identifiedBy = identifiedBy;
    }

    public String getIdentifiedByPassword() {
        return identifiedByPassword;
    }

    public void setIdentifiedByPassword(String identifiedByPassword) {
        this.identifiedByPassword = identifiedByPassword;
    }

    public boolean getWithGrantOption() {
        return withGrantOption;
    }

    public void setWithGrantOption(boolean withGrantOption) {
        this.withGrantOption = withGrantOption;
    }
}
