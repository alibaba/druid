package com.alibaba.druid.sql.ast.statement;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.ast.SQLObjectImpl;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class SQLAlterTableDropPartition extends SQLObjectImpl implements SQLAlterTableItem {

    private boolean ifNotExists = false;

    private boolean purge;

    private final List<SQLAssignItem> partition = new ArrayList<SQLAssignItem>(4);

    public List<SQLAssignItem> getPartition() {
        return partition;
    }

    public boolean isIfNotExists() {
        return ifNotExists;
    }

    public void setIfNotExists(boolean ifNotExists) {
        this.ifNotExists = ifNotExists;
    }

    public boolean isPurge() {
        return purge;
    }

    public void setPurge(boolean purge) {
        this.purge = purge;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, partition);
        }
        visitor.endVisit(this);
    }
}
