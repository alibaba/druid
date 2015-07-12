package com.alibaba.druid.sql.ast.statement;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.ast.SQLObjectImpl;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class SQLAlterTableEnableLifecycle extends SQLObjectImpl implements SQLAlterTableItem {

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
