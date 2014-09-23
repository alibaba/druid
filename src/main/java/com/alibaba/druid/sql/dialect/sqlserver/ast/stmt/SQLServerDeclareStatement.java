/*
 * Copyright 2014 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.alibaba.druid.sql.dialect.sqlserver.ast.stmt;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.dialect.sqlserver.ast.SQLServerDeclareItem;
import com.alibaba.druid.sql.dialect.sqlserver.ast.SQLServerObjectImpl;
import com.alibaba.druid.sql.dialect.sqlserver.ast.SQLServerStatement;
import com.alibaba.druid.sql.dialect.sqlserver.visitor.SQLServerASTVisitor;

public class SQLServerDeclareStatement extends SQLServerObjectImpl implements SQLServerStatement {

    protected List<SQLServerDeclareItem> items = new ArrayList<SQLServerDeclareItem>();

    @Override
    public void accept0(SQLServerASTVisitor visitor) {
        if (visitor.visit(this)) {
            this.acceptChild(visitor, items);
        }
        visitor.endVisit(this);
    }

    public List<SQLServerDeclareItem> getItems() {
        return items;
    }

    public void setItems(List<SQLServerDeclareItem> items) {
        this.items = items;
    }

}
