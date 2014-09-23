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

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLSelectItem;
import com.alibaba.druid.sql.dialect.sqlserver.visitor.SQLServerASTVisitor;

public class SQLServerOutput extends SQLServerObjectImpl {

    protected SQLExprTableSource        into;

    protected final List<SQLExpr>       columns    = new ArrayList<SQLExpr>();

    protected final List<SQLSelectItem> selectList = new ArrayList<SQLSelectItem>();

    @Override
    public void accept0(SQLServerASTVisitor visitor) {
        if (visitor.visit(this)) {
            this.acceptChild(visitor, selectList);
            this.acceptChild(visitor, into);
            this.acceptChild(visitor, columns);
        }

        visitor.endVisit(this);
    }

    public SQLExprTableSource getInto() {
        return into;
    }

    public void setInto(SQLExprTableSource into) {
        this.into = into;
    }

    public List<SQLExpr> getColumns() {
        return columns;
    }

    public List<SQLSelectItem> getSelectList() {
        return selectList;
    }

}
