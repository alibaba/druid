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
package com.alibaba.druid.sql.ast.statement;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.SQLStatementImpl;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SQLDescribeStatement extends SQLStatementImpl {

    protected SQLName object;

    protected SQLName column;

    // for odps
    protected SQLObjectType objectType;
    protected List<SQLExpr> partition = new ArrayList<SQLExpr>();

    public SQLName getObject() {
        return object;
    }

    public void setObject(SQLName object) {
        this.object = object;
    }

    public SQLName getColumn() {
        return column;
    }

    public void setColumn(SQLName column) {
        if (column != null) {
            column.setParent(this);
        }
        this.column = column;
    }

    public List<SQLExpr> getPartition() {
        return partition;
    }

    public void setPartition(List<SQLExpr> partition) {
        this.partition = partition;
    }

    public SQLObjectType getObjectType() {
        return objectType;
    }

    public void setObjectType(SQLObjectType objectType) {
        this.objectType = objectType;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, object);
            acceptChild(visitor, column);
        }
        visitor.endVisit(this);
    }

    @Override
    public List<SQLObject> getChildren() {
        return Arrays.<SQLObject>asList(this.object, column);

    }
}
