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

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class SQLForeignKeyImpl extends SQLConstraintImpl implements SQLForeignKeyConstraint {

    private SQLName       referencedTableName;
    private List<SQLName> referencingColumns = new ArrayList<SQLName>();
    private List<SQLName> referencedColumns  = new ArrayList<SQLName>();
    private boolean       onDeleteCascade    = false;
    private boolean       onDeleteSetNull    = false;

    public SQLForeignKeyImpl(){

    }

    @Override
    public List<SQLName> getReferencingColumns() {
        return referencingColumns;
    }

    @Override
    public SQLName getReferencedTableName() {
        return referencedTableName;
    }

    @Override
    public void setReferencedTableName(SQLName value) {
        this.referencedTableName = value;
    }

    @Override
    public List<SQLName> getReferencedColumns() {
        return referencedColumns;
    }

    public boolean isOnDeleteCascade() {
        return onDeleteCascade;
    }

    public void setOnDeleteCascade(boolean onDeleteCascade) {
        this.onDeleteCascade = onDeleteCascade;
    }

    public boolean isOnDeleteSetNull() {
        return onDeleteSetNull;
    }

    public void setOnDeleteSetNull(boolean onDeleteSetNull) {
        this.onDeleteSetNull = onDeleteSetNull;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.getName());
            acceptChild(visitor, this.getReferencedTableName());
            acceptChild(visitor, this.getReferencingColumns());
            acceptChild(visitor, this.getReferencedColumns());
        }
        visitor.endVisit(this);        
    }

    public void cloneTo(SQLForeignKeyImpl x) {
        super.cloneTo(x);

        if (referencedTableName != null) {
            x.setReferencedTableName(referencedTableName.clone());
        }

        for (SQLName column : referencingColumns) {
            SQLName columnClone = column.clone();
            columnClone.setParent(x);
            x.getReferencingColumns().add(columnClone);
        }

        for (SQLName column : referencedColumns) {
            SQLName columnClone = column.clone();
            columnClone.setParent(x);
            x.getReferencedColumns().add(columnClone);
        }
    }

    public SQLForeignKeyImpl clone() {
        SQLForeignKeyImpl x = new SQLForeignKeyImpl();
        cloneTo(x);
        return x;
    }
}
