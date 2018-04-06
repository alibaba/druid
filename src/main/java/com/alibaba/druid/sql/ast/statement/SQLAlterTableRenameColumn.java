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

import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLObjectImpl;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class SQLAlterTableRenameColumn extends SQLObjectImpl implements SQLAlterTableItem {

    private SQLName column;
    private SQLName to;

    public SQLAlterTableRenameColumn(){

    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, column);
            acceptChild(visitor, to);
        }
        visitor.endVisit(this);
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

    public SQLName getTo() {
        return to;
    }

    public void setTo(SQLName to) {
        if (to != null) {
            to.setParent(this);
        }
        this.to = to;
    }

}
