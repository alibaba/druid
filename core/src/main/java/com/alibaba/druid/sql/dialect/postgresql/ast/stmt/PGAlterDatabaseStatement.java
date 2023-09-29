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
package com.alibaba.druid.sql.dialect.postgresql.ast.stmt;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLStatementImpl;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.statement.SQLAlterStatement;
import com.alibaba.druid.sql.dialect.postgresql.visitor.PGASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

/**
 * @author lizongbo
 * @see <a href="https://www.postgresql.org/docs/current/sql-alterdatabase.html">alter table</a>
 *
 */
public class PGAlterDatabaseStatement extends SQLStatementImpl implements PGSQLStatement, SQLAlterStatement {
    private SQLIdentifierExpr databaseName;
    private SQLIdentifierExpr renameToName;
    private SQLIdentifierExpr ownerToName;
    private SQLIdentifierExpr setTableSpaceName;
    private boolean refreshCollationVersion;
    private SQLIdentifierExpr setParameterName;
    private boolean useEquals;
    private SQLExpr setParameterValue;
    private boolean setFromCurrent;
    private SQLIdentifierExpr resetParameterName;

    public SQLIdentifierExpr getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(SQLIdentifierExpr databaseName) {
        this.databaseName = databaseName;
    }

    public SQLIdentifierExpr getRenameToName() {
        return renameToName;
    }

    public void setRenameToName(SQLIdentifierExpr renameToName) {
        this.renameToName = renameToName;
    }

    public SQLIdentifierExpr getOwnerToName() {
        return ownerToName;
    }

    public void setOwnerToName(SQLIdentifierExpr ownerToName) {
        this.ownerToName = ownerToName;
    }

    public SQLIdentifierExpr getSetTableSpaceName() {
        return setTableSpaceName;
    }

    public void setSetTableSpaceName(SQLIdentifierExpr setTableSpaceName) {
        this.setTableSpaceName = setTableSpaceName;
    }

    public boolean isRefreshCollationVersion() {
        return refreshCollationVersion;
    }

    public void setRefreshCollationVersion(boolean refreshCollationVersion) {
        this.refreshCollationVersion = refreshCollationVersion;
    }

    public SQLIdentifierExpr getSetParameterName() {
        return setParameterName;
    }

    public void setSetParameterName(SQLIdentifierExpr setParameterName) {
        this.setParameterName = setParameterName;
    }

    public boolean isUseEquals() {
        return useEquals;
    }

    public void setUseEquals(boolean useEquals) {
        this.useEquals = useEquals;
    }

    public SQLExpr getSetParameterValue() {
        return setParameterValue;
    }

    public void setSetParameterValue(SQLExpr setParameterValue) {
        this.setParameterValue = setParameterValue;
    }

    public boolean isSetFromCurrent() {
        return setFromCurrent;
    }

    public void setSetFromCurrent(boolean setFromCurrent) {
        this.setFromCurrent = setFromCurrent;
    }

    public SQLIdentifierExpr getResetParameterName() {
        return resetParameterName;
    }

    public void setResetParameterName(SQLIdentifierExpr resetParameterName) {
        this.resetParameterName = resetParameterName;
    }
    public PGAlterDatabaseStatement(DbType dbType) {
        super.dbType = dbType;
    }

    @Override
    public DDLObjectType getDDLObjectType() {
        return DDLObjectType.DATABASE;
    }

    @Override
    public void accept0(PGASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.databaseName);
            acceptChild(visitor, this.renameToName);
            acceptChild(visitor, this.ownerToName);
            acceptChild(visitor, this.setTableSpaceName);
            acceptChild(visitor, this.setParameterName);
            acceptChild(visitor, this.setParameterValue);
            acceptChild(visitor, this.resetParameterName);
        }
        visitor.endVisit(this);
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor instanceof PGASTVisitor) {
            accept0((PGASTVisitor) visitor);
        } else {
            super.accept0(visitor);
        }
    }
}
