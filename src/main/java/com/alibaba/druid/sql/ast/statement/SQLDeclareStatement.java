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

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.ast.SQLDataType;
import com.alibaba.druid.sql.ast.SQLDeclareItem;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLStatementImpl;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class SQLDeclareStatement extends SQLStatementImpl {

    protected List<SQLDeclareItem> items = new ArrayList<SQLDeclareItem>();
    
    public SQLDeclareStatement() {

    }

    public SQLDeclareStatement(SQLName name, SQLDataType dataType) {
        this.addItem(new SQLDeclareItem(name, dataType));
    }

    public SQLDeclareStatement(SQLName name, SQLDataType dataType, SQLExpr value) {
        this.addItem(new SQLDeclareItem(name, dataType, value));
    }

    @Override
    public void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            this.acceptChild(visitor, items);
        }
        visitor.endVisit(this);
    }

    public List<SQLDeclareItem> getItems() {
        return items;
    }

    public void addItem(SQLDeclareItem item) {
        if (item != null) {
            item.setParent(this);
        }
        this.items.add(item);
    }
}
