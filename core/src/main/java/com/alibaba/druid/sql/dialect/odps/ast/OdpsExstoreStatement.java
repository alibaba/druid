package com.alibaba.druid.sql.dialect.odps.ast;

import com.alibaba.druid.sql.ast.statement.SQLAssignItem;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.dialect.odps.visitor.OdpsASTVisitor;

import java.util.ArrayList;
import java.util.List;

public class OdpsExstoreStatement extends OdpsStatementImpl {
    private SQLExprTableSource table;
    private final List<SQLAssignItem> partitions = new ArrayList<>();

    @Override
    protected void accept0(OdpsASTVisitor v) {
        if (v.visit(this)) {
            acceptChild(v, table);
            acceptChild(v, partitions);
        }
        v.endVisit(this);
    }

    public SQLExprTableSource getTable() {
        return table;
    }

    public void setTable(SQLExprTableSource x) {
        if (x != null) {
            x.setParent(this);
        }
        this.table = x;
    }

    public List<SQLAssignItem> getPartitions() {
        return partitions;
    }
}
