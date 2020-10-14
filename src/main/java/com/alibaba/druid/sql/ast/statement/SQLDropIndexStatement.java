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
import com.alibaba.druid.sql.ast.*;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.ArrayList;
import java.util.List;

public class SQLDropIndexStatement extends SQLStatementImpl implements SQLDropStatement, SQLReplaceable {

    private SQLName            indexName;
    private SQLExprTableSource tableName;

    private SQLExpr            algorithm;
    private SQLExpr            lockOption;
    private boolean ifExists;
    
    public SQLDropIndexStatement() {
        
    }
    
    public SQLDropIndexStatement(DbType dbType) {
        super (dbType);
    }

    public SQLName getIndexName() {
        return indexName;
    }

    public void setIndexName(SQLName indexName) {
        this.indexName = indexName;
    }

    public SQLExprTableSource getTableName() {
        return tableName;
    }

    public void setTableName(SQLName tableName) {
        this.setTableName(new SQLExprTableSource(tableName));
    }

    public void setTableName(SQLExprTableSource tableName) {
        this.tableName = tableName;
    }

    public SQLExpr getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(SQLExpr x) {
        if (x != null) {
            x.setParent(this);
        }
        this.algorithm = x;
    }

    public SQLExpr getLockOption() {
        return lockOption;
    }

    public void setLockOption(SQLExpr x) {
        if (x != null) {
            x.setParent(this);
        }
        this.lockOption = x;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, indexName);
            acceptChild(visitor, tableName);
            acceptChild(visitor, algorithm);
            acceptChild(visitor, lockOption);
        }
        visitor.endVisit(this);
    }

    @Override
    public List<SQLObject> getChildren() {
        List<SQLObject> children = new ArrayList<SQLObject>();
        if (indexName != null) {
            children.add(indexName);
        }
        if (tableName != null) {
            children.add(tableName);
        }
        return children;
    }

    public boolean replace(SQLExpr expr, SQLExpr target) {
        if (indexName == expr) {
            setIndexName((SQLName) target);
            return true;
        }

        if (algorithm == expr) {
            setAlgorithm(target);
            return true;
        }

        if (lockOption == expr) {
            setLockOption(target);
            return true;
        }

        return false;
    }

    public boolean isIfExists() {
        return ifExists;
    }

    public void setIfExists(boolean ifExists) {
        this.ifExists = ifExists;
    }
}
