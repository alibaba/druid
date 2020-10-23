package com.alibaba.druid.sql.dialect.odps.ast;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.dialect.odps.visitor.OdpsASTVisitor;

import java.util.List;
import java.util.ArrayList;

public class OdpsExstoreStatement extends OdpsStatementImpl {
    private SQLExprTableSource table;
    private final List<SQLExpr> partitions = new ArrayList<SQLExpr>();

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

    public List<SQLExpr> getPartitions() {
        return partitions;
    }
}
