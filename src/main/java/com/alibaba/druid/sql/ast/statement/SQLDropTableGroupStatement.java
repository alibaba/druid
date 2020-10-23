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

public class SQLDropTableGroupStatement extends SQLStatementImpl implements SQLDropStatement, SQLReplaceable {

    protected SQLName name;
    protected boolean ifExists = false;

    public SQLDropTableGroupStatement(){
    }

    public SQLDropTableGroupStatement(DbType dbType){
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

    public String getTableGroupName() {
        if (name == null) {
            return null;
        }

        if (name instanceof SQLName) {
            return name.getSimpleName();
        }

        return null;
    }

    public boolean isIfExists() {
        return ifExists;
    }
    
    public void setIfExists(boolean ifNotExists) {
        this.ifExists = ifNotExists;
    }

    public boolean replace(SQLExpr expr, SQLExpr target) {
        if (name == expr) {
            setName((SQLName) target);
            return true;
        }

        return false;
    }

}
