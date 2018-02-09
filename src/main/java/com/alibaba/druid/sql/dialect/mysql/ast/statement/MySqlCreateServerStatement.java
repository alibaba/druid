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
import com.alibaba.druid.sql.ast.statement.SQLAlterStatement;
import com.alibaba.druid.sql.ast.statement.SQLCreateStatement;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitor;

public class MySqlCreateServerStatement extends MySqlStatementImpl implements SQLCreateStatement {
    private SQLName name;
    private SQLName foreignDataWrapper;
    private SQLExpr host;
    private SQLExpr database;
    private SQLExpr user;
    private SQLExpr password;
    private SQLExpr socket;
    private SQLExpr owner;
    private SQLExpr port;

    public SQLName getName() {
        return name;
    }

    public void setName(SQLName x) {
        if (x != null) {
            x.setParent(this);
        }
        this.name = x;
    }

    public SQLName getForeignDataWrapper() {
        return foreignDataWrapper;
    }

    public void setForeignDataWrapper(SQLName x) {
        if (x != null) {
            x.setParent(this);
        }
        this.foreignDataWrapper = x;
    }

    public SQLExpr getHost() {
        return host;
    }

    public void setHost(SQLExpr x) {
        if (x != null) {
            x.setParent(this);
        }
        this.host = x;
    }

    public SQLExpr getDatabase() {
        return database;
    }

    public void setDatabase(SQLExpr x) {
        if (x != null) {
            x.setParent(this);
        }
        this.database = x;
    }

    public SQLExpr getUser() {
        return user;
    }

    public void setUser(SQLExpr x) {
        if (x != null) {
            x.setParent(this);
        }
        this.user = x;
    }

    public SQLExpr getPassword() {
        return password;
    }

    public void setPassword(SQLExpr x) {
        if (x != null) {
            x.setParent(this);
        }
        this.password = x;
    }

    public SQLExpr getSocket() {
        return socket;
    }

    public void setSocket(SQLExpr x) {
        if (x != null) {
            x.setParent(this);
        }
        this.socket = x;
    }

    public SQLExpr getOwner() {
        return owner;
    }

    public void setOwner(SQLExpr x) {
        if (x != null) {
            x.setParent(this);
        }
        this.owner = x;
    }

    public SQLExpr getPort() {
        return port;
    }

    public void setPort(SQLExpr x) {
        if (x != null) {
            x.setParent(this);
        }
        this.port = x;
    }

    public void accept0(MySqlASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, name);
            acceptChild(visitor, foreignDataWrapper);
            acceptChild(visitor, host);
            acceptChild(visitor, database);
            acceptChild(visitor, user);
            acceptChild(visitor, password);
            acceptChild(visitor, socket);
            acceptChild(visitor, owner);
            acceptChild(visitor, port);
        }
        visitor.endVisit(this);
    }
}
