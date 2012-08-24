/*
 * Copyright 1999-2011 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.sql.dialect.oracle.ast.stmt;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.statement.SQLCreateIndexStatement;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class OracleCreateIndexStatement extends SQLCreateIndexStatement implements OracleDDLStatement {

    private static final long serialVersionUID  = 1L;

    private boolean           online            = false;

    private boolean           indexOnlyTopLevel = false;

    private boolean           noParallel;

    private SQLExpr           parallel;

    private SQLName           tablespace;

    public SQLName getTablespace() {
        return tablespace;
    }

    public void setTablespace(SQLName tablespace) {
        this.tablespace = tablespace;
    }

    public SQLExpr getParallel() {
        return parallel;
    }

    public void setParallel(SQLExpr parallel) {
        this.parallel = parallel;
    }

    public boolean isNoParallel() {
        return noParallel;
    }

    public void setNoParallel(boolean noParallel) {
        this.noParallel = noParallel;
    }

    public boolean isIndexOnlyTopLevel() {
        return indexOnlyTopLevel;
    }

    public void setIndexOnlyTopLevel(boolean indexOnlyTopLevel) {
        this.indexOnlyTopLevel = indexOnlyTopLevel;
    }

    protected void accept0(SQLASTVisitor visitor) {
        accept0((OracleASTVisitor) visitor);
    }

    @Override
    public void accept0(OracleASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, getName());
            acceptChild(visitor, getTable());
            acceptChild(visitor, getItems());
            acceptChild(visitor, getTablespace());
            acceptChild(visitor, parallel);
        }
        visitor.endVisit(this);
    }

    // public static enum Type {
    // UNIQUE, BITMAP
    // }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

}
