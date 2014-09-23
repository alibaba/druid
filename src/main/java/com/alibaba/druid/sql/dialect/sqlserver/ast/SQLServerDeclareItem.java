/*
 * Copyright 2014 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
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
