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
package com.alibaba.druid.sql.dialect.sqlserver.ast;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.ast.SQLDataType;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.statement.SQLTableElement;
import com.alibaba.druid.sql.dialect.sqlserver.visitor.SQLServerASTVisitor;

public class SQLServerDeclareItem extends SQLServerObjectImpl {

    protected Type                  type;

    protected SQLExpr               name;

    protected SQLDataType           dataType;

    protected SQLExpr               value;

    protected List<SQLTableElement> tableElementList = new ArrayList<SQLTableElement>();

    @Override
    public void accept0(SQLServerASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.name);
            acceptChild(visitor, this.dataType);
            acceptChild(visitor, this.value);
            acceptChild(visitor, this.tableElementList);
        }
        visitor.endVisit(this);
    }

    public SQLExpr getName() {
        return name;
    }

    public void setName(SQLExpr name) {
        this.name = name;
    }

    public SQLDataType getDataType() {
        return dataType;
    }

    public void setDataType(SQLDataType dataType) {
        this.dataType = dataType;
    }

    public SQLExpr getValue() {
        return value;
    }

    public void setValue(SQLExpr value) {
        this.value = value;
    }

    public List<SQLTableElement> getTableElementList() {
        return tableElementList;
    }

    public void setTableElementList(List<SQLTableElement> tableElementList) {
        this.tableElementList = tableElementList;
    }

    public enum Type {
        TABLE, LOCAL, CURSOR;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

}
