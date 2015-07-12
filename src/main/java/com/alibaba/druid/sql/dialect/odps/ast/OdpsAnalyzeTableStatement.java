package com.alibaba.druid.sql.dialect.odps.ast;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.statement.SQLAssignItem;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.dialect.odps.visitor.OdpsASTVisitor;

public class OdpsAnalyzeTableStatement extends OdpsStatementImpl {

    private SQLExprTableSource  table;
    private List<SQLAssignItem> partition = new ArrayList<SQLAssignItem>();

    @Override
    protected void accept0(OdpsASTVisitor visitor) {
        if (visitor.visit(this)) {
            this.acceptChild(visitor, table);
            this.acceptChild(visitor, partition);
        }
        visitor.endVisit(this);
    }

    public SQLExprTableSource getTable() {
        return table;
    }

    public void setTable(SQLExprTableSource table) {
        if (table != null) {
            table.setParent(table);
        }
        this.table = table;
    }
    
    public void setTable(SQLName table) {
        this.setTable(new SQLExprTableSource(table));
    }

    public List<SQLAssignItem> getPartition() {
        return partition;
    }

}
