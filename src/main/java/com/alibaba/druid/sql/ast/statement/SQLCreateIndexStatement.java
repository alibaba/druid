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

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLStatementImpl;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class SQLCreateIndexStatement extends SQLStatementImpl implements SQLDDLStatement {

    private SQLName                    name;

    private SQLTableSource             table;

    private List<SQLSelectOrderByItem> items = new ArrayList<SQLSelectOrderByItem>();

    private String                     type;

    public SQLCreateIndexStatement(){

    }
    
    public SQLCreateIndexStatement(String dbType){
        super (dbType);
    }

    public SQLTableSource getTable() {
        return table;
    }

    public void setTable(SQLName table) {
        this.setTable(new SQLExprTableSource(table));
    }

    public void setTable(SQLTableSource table) {
        this.table = table;
    }

    public List<SQLSelectOrderByItem> getItems() {
        return items;
    }

    public void setItems(List<SQLSelectOrderByItem> items) {
        this.items = items;
    }

    public SQLName getName() {
        return name;
    }

    public void setName(SQLName name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, getName());
            acceptChild(visitor, getTable());
            acceptChild(visitor, getItems());
        }
        visitor.endVisit(this);
    }
}
