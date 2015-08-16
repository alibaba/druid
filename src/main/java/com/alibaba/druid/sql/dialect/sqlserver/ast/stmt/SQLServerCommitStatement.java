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
package com.alibaba.druid.sql.dialect.sqlserver.ast.stmt;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.dialect.sqlserver.ast.SQLServerObjectImpl;
import com.alibaba.druid.sql.dialect.sqlserver.ast.SQLServerStatement;
import com.alibaba.druid.sql.dialect.sqlserver.visitor.SQLServerASTVisitor;

public class SQLServerCommitStatement extends SQLServerObjectImpl implements SQLServerStatement {

    private boolean work = false;

    private SQLExpr transactionName;

    private SQLExpr delayedDurability;
    
    private String  dbType;

    public boolean isWork() {
        return work;
    }

    public void setWork(boolean work) {
        this.work = work;
    }

    @Override
    public void accept0(SQLServerASTVisitor visitor) {
        visitor.visit(this);

        visitor.endVisit(this);

    }

    public SQLExpr getTransactionName() {
        return transactionName;
    }

    public void setTransactionName(SQLExpr transactionName) {
        this.transactionName = transactionName;
    }

    public SQLExpr getDelayedDurability() {
        return delayedDurability;
    }

    public void setDelayedDurability(SQLExpr delayedDurability) {
        this.delayedDurability = delayedDurability;
    }
    
    public String getDbType() {
        return dbType;
    }
    
    public void setDbType(String dbType) {
        this.dbType = dbType;
    }
}
