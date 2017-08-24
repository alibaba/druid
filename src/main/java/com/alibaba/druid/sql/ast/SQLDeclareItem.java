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
package com.alibaba.druid.sql.ast;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.ast.statement.SQLTableElement;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class SQLDeclareItem extends SQLObjectImpl implements SQLObjectWithDataType {

    protected Type                  type;

    protected SQLExpr               name;

    protected SQLDataType           dataType;

    protected SQLExpr               value;

    protected List<SQLTableElement> tableElementList = new ArrayList<SQLTableElement>();

    public SQLDeclareItem() {

    }

    public SQLDeclareItem(SQLExpr name, SQLDataType dataType) {
        this.setName(name);
        this.setDataType(dataType);
    }

    public SQLDeclareItem(SQLExpr name, SQLDataType dataType, SQLExpr value) {
        this.setName(name);
        this.setDataType(dataType);
        this.setValue(value);
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
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
        if (name != null) {
            name.setParent(this);
        }
        this.name = name;
    }

    public SQLDataType getDataType() {
        return dataType;
    }

    public void setDataType(SQLDataType dataType) {
        if (dataType != null) {
            dataType.setParent(this);
        }
        this.dataType = dataType;
    }

    public SQLExpr getValue() {
        return value;
    }

    public void setValue(SQLExpr value) {
        if (value != null) {
            value.setParent(this);
        }
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
