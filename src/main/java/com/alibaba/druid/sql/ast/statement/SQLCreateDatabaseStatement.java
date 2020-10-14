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
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.*;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SQLCreateDatabaseStatement extends SQLStatementImpl implements SQLCreateStatement {
    protected SQLName                         name;
    protected String                          characterSet;
    protected String                          collate;
    protected List<SQLCommentHint>            hints;
    protected boolean                         ifNotExists = false;
    protected SQLExpr                         comment;
    protected SQLExpr                         location; // hive
    protected final List<SQLAssignItem>       dbProperties = new ArrayList<SQLAssignItem>();
    protected Map<String, SQLExpr>            options = new HashMap<String, SQLExpr>(); // for ads
    protected String                          user;

    protected SQLExpr                         password; // drds
    protected final List<SQLAssignItem> storedOn = new ArrayList<SQLAssignItem>(); // drds
    protected final List<List<SQLAssignItem>> storedBy = new ArrayList<List<SQLAssignItem>>(); // drds stored by
    protected SQLExpr                         storedAs;  // drds
    protected SQLExpr                         storedIn;  // drds

    //adb
    protected boolean physical;

    public SQLCreateDatabaseStatement() {
    }

    public SQLCreateDatabaseStatement(DbType dbType){
        super (dbType);
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, name);
        }
        visitor.endVisit(this);
    }

    @Override
    public List<SQLObject> getChildren() {
        List<SQLObject> children = new ArrayList<SQLObject>();
        if (name != null) {
            children.add(name);
        }
        return children;
    }

    public SQLName getName() {
        return name;
    }

    public void setName(SQLName name) {
        this.name = name;
    }

    public String getCharacterSet() {
        return characterSet;
    }

    public void setCharacterSet(String characterSet) {
        this.characterSet = characterSet;
    }

    public String getCollate() {
        return collate;
    }

    public void setCollate(String collate) {
        this.collate = collate;
    }

    public List<SQLCommentHint> getHints() {
        return hints;
    }

    public void setHints(List<SQLCommentHint> hints) {
        this.hints = hints;
    }

    public boolean isIfNotExists() {
        return ifNotExists;
    }

    public void setIfNotExists(boolean ifNotExists) {
        this.ifNotExists = ifNotExists;
    }

    public SQLExpr getComment() {
        return comment;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setComment(SQLExpr x) {
        if (x != null) {
            x.setParent(this);
        }
        this.comment = x;
    }

    public SQLExpr getLocation() {
        return location;
    }

    public Map<String, SQLExpr> getOptions() {
        return options;
    }

    public void setLocation(SQLExpr x) {
        if (x != null) {
            x.setParent(this);
        }
        this.location = x;
    }

    public List<SQLAssignItem> getDbProperties() {
        return dbProperties;
    }

    public void setOptions(Map<String, SQLExpr> options) {
        this.options = options;
    }

    public List<SQLAssignItem> getStoredOn() {
        return storedOn;
    }

    public List<List<SQLAssignItem>> getStoredBy() {
        return storedBy;
    }

    public SQLExpr getStoredAs() {
        return storedAs;
    }

    public void setStoredAs(SQLExpr x) {
        if (x != null) {
            x.setParent(this);
        }
        this.storedAs = x;
    }

    public SQLExpr getStoredIn() {
        return storedIn;
    }

    public void setStoredIn(SQLExpr x) {
        if (x != null) {
            x.setParent(this);
        }
        this.storedIn = x;
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

    public String getDatabaseName() {
        if (name == null) {
            return null;
        }

        if (name instanceof SQLName) {
            return ((SQLName) name).getSimpleName();
        }

        return null;
    }

    public void setDatabase(String database) {
        SQLExpr expr = SQLUtils.toSQLExpr(database);

        if (expr instanceof SQLIdentifierExpr && name instanceof SQLPropertyExpr) {
            ((SQLPropertyExpr) this.name).setName(database);
            return;
        }

        expr.setParent(this);
        this.name = (SQLName) expr;
    }

    public String getServer() {
        if (name instanceof SQLPropertyExpr) {
            SQLExpr owner = ((SQLPropertyExpr) name).getOwner();
            if (owner instanceof SQLIdentifierExpr) {
                return ((SQLIdentifierExpr) owner).getName();
            }

            if (owner instanceof SQLPropertyExpr) {
                return ((SQLPropertyExpr) owner).getName();
            }
        }

        return null;
    }

    public boolean setServer(String server) {
        if (name == null) {
            return false;
        }

        if (name instanceof SQLIdentifierExpr) {
            SQLPropertyExpr propertyExpr = new SQLPropertyExpr(new SQLIdentifierExpr(server), ((SQLIdentifierExpr) name).getName());
            propertyExpr.setParent(this);
            name = propertyExpr;
            return true;
        }

        if (name instanceof SQLPropertyExpr) {
            SQLPropertyExpr propertyExpr = (SQLPropertyExpr) name;
            SQLExpr owner = propertyExpr.getOwner();
            if (owner instanceof SQLIdentifierExpr) {
                propertyExpr.setOwner(new SQLIdentifierExpr(server));
                return true;
            } else if (owner instanceof SQLPropertyExpr) {
                ((SQLPropertyExpr) owner).setName(server);
                return true;
            }
        }

        return false;
    }

    public boolean isPhysical() {
        return physical;
    }

    public void setPhysical(boolean physical) {
        this.physical = physical;
    }

    //    public static class StoredAs
}
