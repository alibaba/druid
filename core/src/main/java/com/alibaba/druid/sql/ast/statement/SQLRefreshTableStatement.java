package com.alibaba.druid.sql.ast.statement;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLStatementImpl;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.ArrayList;
import java.util.List;

public class SQLRefreshTableStatement extends SQLStatementImpl {
    private SQLExpr name;
    private List<SQLAssignItem> partitions;
    public SQLRefreshTableStatement() {
        partitions = new ArrayList<>();
    }
    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, name);
            acceptChild(visitor, partitions);
        }
        visitor.endVisit(this);
    }

    public SQLExpr getName() {
        return name;
    }

    public void setName(SQLExpr x) {
        if (x != null) {
            x.setParent(this);
        }
        this.name = x;
    }

    public List<SQLAssignItem> getPartitions() {
        return partitions;
    }

    public void setPartitions(List<SQLAssignItem> partitions) {
        this.partitions = partitions;
    }

    public void addPartition(SQLAssignItem partition) {
        if (partition != null) {
            partition.setParent(this);
        }
        this.partitions.add(partition);
    }
}
