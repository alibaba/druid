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

import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.ArrayList;
import java.util.List;

public class SQLColumnReference extends SQLConstraintImpl implements SQLColumnConstraint {

    private SQLName       table;
    private List<SQLName> columns = new ArrayList<SQLName>();

    private SQLForeignKeyImpl.Match referenceMatch;
    protected SQLForeignKeyImpl.Option onUpdate;
    protected SQLForeignKeyImpl.Option onDelete;

    public SQLColumnReference() {

    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.getName());
        }
        visitor.endVisit(this);
    }

    public SQLName getTable() {
        return table;
    }

    public void setTable(SQLName table) {
        this.table = table;
    }

    public List<SQLName> getColumns() {
        return columns;
    }

    public void setColumns(List<SQLName> columns) {
        this.columns = columns;
    }

    public SQLColumnReference clone() {
        SQLColumnReference x = new SQLColumnReference();

        super.cloneTo(x);

        if (table != null) {
            x.setTable(table.clone());
        }

        for (SQLName column : columns) {
            SQLName columnCloned = column.clone();
            columnCloned.setParent(x);
            x.columns.add(columnCloned);
        }

        x.referenceMatch = referenceMatch;
        x.onUpdate = onUpdate;
        x.onDelete = onDelete;

        return x;
    }

    public SQLForeignKeyImpl.Match getReferenceMatch() {
        return referenceMatch;
    }

    public void setReferenceMatch(SQLForeignKeyImpl.Match referenceMatch) {
        this.referenceMatch = referenceMatch;
    }

    public SQLForeignKeyImpl.Option getOnUpdate() {
        return onUpdate;
    }

    public void setOnUpdate(SQLForeignKeyImpl.Option onUpdate) {
        this.onUpdate = onUpdate;
    }

    public SQLForeignKeyImpl.Option getOnDelete() {
        return onDelete;
    }

    public void setOnDelete(SQLForeignKeyImpl.Option onDelete) {
        this.onDelete = onDelete;
    }
}
