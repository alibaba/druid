package com.alibaba.druid.sql.ast.statement;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLStatementImpl;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class SQLTruncateStatement extends SQLStatementImpl {

    private static final long          serialVersionUID = 1L;
    protected List<SQLExprTableSource> tableSources     = new ArrayList<SQLExprTableSource>(2);

    public List<SQLExprTableSource> getTableSources() {
        return tableSources;
    }

    public void setTableSources(List<SQLExprTableSource> tableSources) {
        this.tableSources = tableSources;
    }
    
    public void addTableSource(SQLName name) {
        this.tableSources.add(new SQLExprTableSource(name));
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, tableSources);
        }
        visitor.endVisit(this);
    }
}
