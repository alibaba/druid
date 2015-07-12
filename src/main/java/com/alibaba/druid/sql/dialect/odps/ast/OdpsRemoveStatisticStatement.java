package com.alibaba.druid.sql.dialect.odps.ast;

import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.dialect.odps.visitor.OdpsASTVisitor;

public class OdpsRemoveStatisticStatement extends OdpsStatementImpl {

    private SQLExprTableSource table;

    private OdpsStatisticClause statisticClause;

    @Override
    protected void accept0(OdpsASTVisitor visitor) {
        if (visitor.visit(this)) {
            this.acceptChild(visitor, table);
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

    public OdpsStatisticClause getStatisticClause() {
        return statisticClause;
    }

    public void setStatisticClause(OdpsStatisticClause statisticClause) {
        this.statisticClause = statisticClause;
    }

}
