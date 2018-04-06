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
package com.alibaba.druid.sql.dialect.sqlserver.ast.expr;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.dialect.sqlserver.ast.SQLServerObjectImpl;
import com.alibaba.druid.sql.dialect.sqlserver.visitor.SQLServerASTVisitor;
import com.alibaba.druid.util.FnvHash;

import java.util.Collections;
import java.util.List;

public class SQLServerObjectReferenceExpr extends SQLServerObjectImpl implements SQLServerExpr, SQLName {

    private String server;
    private String database;
    private String schema;

    protected long schemaHashCode64;
    protected long hashCode64;

    public SQLServerObjectReferenceExpr(){

    }

    public SQLServerObjectReferenceExpr(SQLExpr owner){
        if (owner instanceof SQLIdentifierExpr) {
            this.database = ((SQLIdentifierExpr) owner).getName();
        } else if (owner instanceof SQLPropertyExpr) {
            SQLPropertyExpr propExpr = (SQLPropertyExpr) owner;

            this.server = ((SQLIdentifierExpr) propExpr.getOwner()).getName();
            this.database = propExpr.getName();
        } else {
            throw new IllegalArgumentException(owner.toString());
        }
    }

    public String getSimpleName() {
        if (schema != null) {
            return schema;
        }

        if (database != null) {
            return database;
        }
        return server;
    }

    @Override
    public void accept0(SQLServerASTVisitor visitor) {
        visitor.visit(this);
        visitor.endVisit(this);
    }

    public void output(StringBuffer buf) {
        boolean flag = false;
        if (server != null) {
            buf.append(server);
            flag = true;
        }

        if (flag) {
            buf.append('.');
        }
        if (database != null) {
            buf.append(database);
            flag = true;
        }

        if (flag) {
            buf.append('.');
        }

        if (schema != null) {
            buf.append(schema);
            flag = true;
        }
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public SQLServerObjectReferenceExpr clone() {
        SQLServerObjectReferenceExpr x = new SQLServerObjectReferenceExpr();

        x.server           = server;
        x.database         = database;
        x.schema           = schema;

        x.schemaHashCode64 = schemaHashCode64;
        x.hashCode64       = hashCode64;

        return x;
    }

    public long nameHashCode64() {
        if (schemaHashCode64 == 0
                && schema != null) {
            schemaHashCode64 = FnvHash.hashCode64(schema);
        }
        return schemaHashCode64;
    }

    @Override
    public long hashCode64() {
        if (hashCode64 == 0) {
            if (server == null) {
                hashCode64 = new SQLPropertyExpr(
                        new SQLPropertyExpr(server, database)
                        , schema)
                        .hashCode64();
            } else {
                hashCode64 = new SQLPropertyExpr(database, schema)
                        .hashCode64();
            }
        }
        return hashCode64;
    }

    public List<SQLObject> getChildren() {
        return Collections.<SQLObject>emptyList();
    }
}
