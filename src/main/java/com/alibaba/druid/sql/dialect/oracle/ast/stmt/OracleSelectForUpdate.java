/*
 * Copyright 1999-2101 Alibaba Group Holding Ltd.
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

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.OracleSQLObjectImpl;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleASTVisitor;

public class OracleSelectForUpdate extends OracleSQLObjectImpl {

    private final List<SQLExpr> of         = new ArrayList<SQLExpr>();

    private boolean             notWait    = false;
    private SQLExpr             wait;
    private boolean             skipLocked = false;

    public OracleSelectForUpdate(){

    }

    public boolean isNotWait() {
        return notWait;
    }

    public void setNotWait(boolean notWait) {
        this.notWait = notWait;
    }

    public SQLExpr getWait() {
        return wait;
    }

    public void setWait(SQLExpr wait) {
        this.wait = wait;
    }

    public boolean isSkipLocked() {
        return skipLocked;
    }

    public void setSkipLocked(boolean skipLocked) {
        this.skipLocked = skipLocked;
    }

    public void accept0(OracleASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.of);
            acceptChild(visitor, this.wait);
        }

        visitor.endVisit(this);
    }

    public List<SQLExpr> getOf() {
        return this.of;
    }
}
