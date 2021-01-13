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
import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import com.alibaba.druid.sql.ast.expr.SQLIntegerExpr;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitor;

public class MySqlMigrateStatement extends MySqlStatementImpl {

    private SQLName schema;
    private SQLCharExpr shardNames;
    private SQLIntegerExpr migrateType;
    private SQLCharExpr fromInsId;
    private SQLCharExpr fromInsIp;
    private SQLIntegerExpr fromInsPort;
    private SQLCharExpr fromInsStatus;
    private SQLCharExpr toInsId;
    private SQLCharExpr toInsIp;
    private SQLIntegerExpr toInsPort;
    private SQLCharExpr toInsStatus;

    @Override
    public void accept0(MySqlASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, schema);
            acceptChild(visitor, shardNames);
            acceptChild(visitor, migrateType);
            acceptChild(visitor, fromInsId);
            acceptChild(visitor, fromInsIp);
            acceptChild(visitor, fromInsPort);
            acceptChild(visitor, fromInsStatus);
            acceptChild(visitor, toInsId);
            acceptChild(visitor, toInsIp);
            acceptChild(visitor, toInsPort);
            acceptChild(visitor, toInsStatus);
        }
        visitor.endVisit(this);
    }

    public SQLName getSchema() {
        return schema;
    }

    public void setSchema(SQLName schema) {
        this.schema = schema;
    }

    public SQLCharExpr getShardNames() {
        return shardNames;
    }

    public void setShardNames(SQLCharExpr shardNames) {
        this.shardNames = shardNames;
    }

    public SQLIntegerExpr getMigrateType() {
        return migrateType;
    }

    public void setMigrateType(SQLIntegerExpr migrateType) {
        this.migrateType = migrateType;
    }

    public SQLCharExpr getFromInsId() {
        return fromInsId;
    }

    public void setFromInsId(SQLCharExpr fromInsId) {
        this.fromInsId = fromInsId;
    }

    public SQLCharExpr getFromInsIp() {
        return fromInsIp;
    }

    public void setFromInsIp(SQLCharExpr fromInsIp) {
        this.fromInsIp = fromInsIp;
    }

    public SQLIntegerExpr getFromInsPort() {
        return fromInsPort;
    }

    public void setFromInsPort(SQLIntegerExpr fromInsPort) {
        this.fromInsPort = fromInsPort;
    }

    public SQLCharExpr getFromInsStatus() {
        return fromInsStatus;
    }

    public void setFromInsStatus(SQLCharExpr fromInsStatus) {
        this.fromInsStatus = fromInsStatus;
    }

    public SQLCharExpr getToInsId() {
        return toInsId;
    }

    public void setToInsId(SQLCharExpr toInsId) {
        this.toInsId = toInsId;
    }

    public SQLCharExpr getToInsIp() {
        return toInsIp;
    }

    public void setToInsIp(SQLCharExpr toInsIp) {
        this.toInsIp = toInsIp;
    }

    public SQLIntegerExpr getToInsPort() {
        return toInsPort;
    }

    public void setToInsPort(SQLIntegerExpr toInsPort) {
        this.toInsPort = toInsPort;
    }

    public SQLCharExpr getToInsStatus() {
        return toInsStatus;
    }

    public void setToInsStatus(SQLCharExpr toInsStatus) {
        this.toInsStatus = toInsStatus;
    }
}
