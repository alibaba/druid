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
import java.util.List;

public class SQLDropDatabaseStatement extends SQLStatementImpl implements SQLDropStatement, SQLReplaceable {

    private SQLExpr database;
    private boolean ifExists;
    private Boolean restrict;
    private boolean cascade;

    //adb
    private boolean physical;

    public SQLDropDatabaseStatement() {
        
    }
    
    public SQLDropDatabaseStatement(DbType dbType) {
        super (dbType);
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, database);
        }
        visitor.endVisit(this);
    }

    public SQLName getName() {
        return (SQLName) database;
    }

    public String getDatabaseName() {
        if (database == null) {
            return null;
        }

        if (database instanceof SQLName) {
            return ((SQLName) database).getSimpleName();
        }

        return null;
    }

    public SQLExpr getDatabase() {
        return database;
    }

    public void setDatabase(SQLExpr database) {
        if (database != null) {
            database.setParent(this);
        }
        this.database = database;
    }

    public void setDatabase(String database) {
        SQLExpr expr = SQLUtils.toSQLExpr(database);

        if (expr instanceof SQLIdentifierExpr && this.database instanceof SQLPropertyExpr) {
            ((SQLPropertyExpr) this.database).setName(database);
            return;
        }

        expr.setParent(this);
        this.database = expr;
    }

    public String getServer() {
        if (database instanceof SQLPropertyExpr) {
            SQLExpr owner = ((SQLPropertyExpr) database).getOwner();
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
        if (database == null) {
            return false;
        }

        if (database instanceof SQLIdentifierExpr) {
            SQLPropertyExpr propertyExpr = new SQLPropertyExpr(new SQLIdentifierExpr(server), ((SQLIdentifierExpr) database).getName());
            propertyExpr.setParent(this);
            database = propertyExpr;
            return true;
        }

        if (database instanceof SQLPropertyExpr) {
            SQLPropertyExpr propertyExpr = (SQLPropertyExpr) database;
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

    public boolean isIfExists() {
        return ifExists;
    }

    public void setIfExists(boolean ifExists) {
        this.ifExists = ifExists;
    }

    @Override
    public List<SQLObject> getChildren() {
        List<SQLObject> children = new ArrayList<SQLObject>();
        if (database != null) {
            children.add(database);
        }
        return children;
    }

    public boolean replace(SQLExpr expr, SQLExpr target) {
        if (database == expr) {
            setDatabase(target);
            return true;
        }

        return false;
    }

    public Boolean getRestrict() {
        return restrict;
    }

    public boolean isRestrict() {
        if (restrict == null) {
            return !cascade;
        }
        return restrict;
    }

    public void setRestrict(boolean restrict) {
        this.restrict = restrict;
    }

    public boolean isCascade() {
        return cascade;
    }

    public void setCascade(boolean cascade) {
        this.cascade = cascade;
    }

    public boolean isPhysical() {
        return physical;
    }

    public void setPhysical(boolean physical) {
        this.physical = physical;
    }
}
