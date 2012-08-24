/*
 * Copyright 1999-2011 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.sql.dialect.mysql.ast.statement;

import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableItem;
import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
import com.alibaba.druid.sql.dialect.mysql.ast.MySqlObjectImpl;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitor;

public class MySqlAlterTableChangeColumn extends MySqlObjectImpl implements SQLAlterTableItem {

    private static final long   serialVersionUID = 1L;

    private SQLName             columnName;

    private SQLColumnDefinition newColumnDefinition;

    private Boolean             first;

    @Override
    public void accept0(MySqlASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, columnName);
            acceptChild(visitor, newColumnDefinition);
        }
    }

    public SQLName getColumnName() {
        return columnName;
    }

    public void setColumnName(SQLName columnName) {
        this.columnName = columnName;
    }

    public SQLColumnDefinition getNewColumnDefinition() {
        return newColumnDefinition;
    }

    public void setNewColumnDefinition(SQLColumnDefinition newColumnDefinition) {
        this.newColumnDefinition = newColumnDefinition;
    }

    public Boolean getFirst() {
        return first;
    }

    public void setFirst(Boolean first) {
        this.first = first;
    }

}
