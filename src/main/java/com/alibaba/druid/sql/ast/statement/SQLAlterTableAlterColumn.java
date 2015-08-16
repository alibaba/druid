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

package com.alibaba.druid.sql.ast.statement;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLObjectImpl;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class SQLAlterTableAlterColumn extends SQLObjectImpl implements SQLAlterTableItem {

    private SQLColumnDefinition column;

    private boolean             setNotNull;

    private boolean             dropNotNull;

    private SQLExpr             setDefault;

    private boolean             dropDefault;

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, column);
            acceptChild(visitor, setDefault);
        }
        visitor.endVisit(this);
    }

    public SQLColumnDefinition getColumn() {
        return column;
    }

    public void setColumn(SQLColumnDefinition column) {
        this.column = column;
    }

    public boolean isSetNotNull() {
        return setNotNull;
    }

    public void setSetNotNull(boolean setNotNull) {
        this.setNotNull = setNotNull;
    }

    public boolean isDropNotNull() {
        return dropNotNull;
    }

    public void setDropNotNull(boolean dropNotNull) {
        this.dropNotNull = dropNotNull;
    }

    public SQLExpr getSetDefault() {
        return setDefault;
    }

    public void setSetDefault(SQLExpr setDefault) {
        this.setDefault = setDefault;
    }

    public boolean isDropDefault() {
        return dropDefault;
    }

    public void setDropDefault(boolean dropDefault) {
        this.dropDefault = dropDefault;
    }

}
