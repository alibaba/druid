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
package com.alibaba.druid.sql.ast;

import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.ArrayList;
import java.util.List;

public class SQLRecordDataType extends SQLDataTypeImpl implements SQLDataType {
    private final List<SQLColumnDefinition> columns = new ArrayList<SQLColumnDefinition>();

    public List<SQLColumnDefinition> getColumns() {
        return columns;
    }

    public void addColumn(SQLColumnDefinition column) {
        column.setParent(this);
        this.columns.add(column);
    }

    public SQLRecordDataType clone() {
        SQLRecordDataType x = new SQLRecordDataType();
        cloneTo(x);

        for (SQLColumnDefinition c : columns) {
            SQLColumnDefinition c2 = c.clone();
            c2.setParent(x);
            x.columns.add(c2);
        }

        return x;
    }

    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.columns);
        }

        visitor.endVisit(this);
    }
}
