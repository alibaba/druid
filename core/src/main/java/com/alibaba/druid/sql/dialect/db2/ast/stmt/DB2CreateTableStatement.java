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
package com.alibaba.druid.sql.dialect.db2.ast.stmt;

import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.dialect.db2.ast.DB2Statement;
import com.alibaba.druid.sql.dialect.db2.visitor.DB2ASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class DB2CreateTableStatement extends SQLCreateTableStatement implements DB2Statement {
    private boolean dataCaptureNone;
    private boolean dataCaptureChanges;

    protected SQLName database;
    protected SQLName validproc;
    protected SQLName indexIn;

    public boolean isDataCaptureNone() {
        return dataCaptureNone;
    }

    public void setDataCaptureNone(boolean dataCaptureNone) {
        this.dataCaptureNone = dataCaptureNone;
    }

    public boolean isDataCaptureChanges() {
        return dataCaptureChanges;
    }

    public void setDataCaptureChanges(boolean dataCaptureChanges) {
        this.dataCaptureChanges = dataCaptureChanges;
    }

    public SQLName getDatabase() {
        return database;
    }

    public void setDatabase(SQLName database) {
        if (database != null) {
            database.setParent(this);
        }
        this.database = database;
    }

    public SQLName getValidproc() {
        return validproc;
    }

    public void setValidproc(SQLName x) {
        if (validproc != null) {
            x.setParent(this);
        }
        this.validproc = x;
    }

    public SQLName getIndexIn() {
        return indexIn;
    }

    public void setIndexIn(SQLName x) {
        if (validproc != null) {
            x.setParent(this);
        }
        this.indexIn = x;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor instanceof DB2ASTVisitor) {
            accept0((DB2ASTVisitor) visitor);
            return;
        }

        super.accept0(visitor);
    }

    @Override
    public void accept0(DB2ASTVisitor visitor) {
        if (visitor.visit(this)) {
            this.acceptChild(visitor, tableSource);
            this.acceptChild(visitor, tableElementList);
            this.acceptChild(visitor, inherits);
            this.acceptChild(visitor, select);
            this.acceptChild(visitor, database);
            this.acceptChild(visitor, validproc);
            this.acceptChild(visitor, indexIn);
        }
        visitor.endVisit(this);
    }
}
