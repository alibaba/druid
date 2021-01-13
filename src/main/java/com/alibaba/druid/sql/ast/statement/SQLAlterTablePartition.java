/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2019 All Rights Reserved.
 */
package com.alibaba.druid.sql.ast.statement;

import com.alibaba.druid.sql.ast.SQLObjectImpl;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author gouzhiwen
 * @version : SQLAlterTablePartition.java, v 0.1 2019年11月14日 21:50 gouzhiwen Exp $
 */
public class SQLAlterTablePartition extends SQLObjectImpl implements SQLAlterTableItem {

    private final List<SQLAssignItem> partition = new ArrayList<SQLAssignItem>(4);

    public List<SQLAssignItem> getPartition() {
        return partition;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, partition);
        }
        visitor.endVisit(this);
    }
}
