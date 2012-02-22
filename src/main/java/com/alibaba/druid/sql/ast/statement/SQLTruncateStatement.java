package com.alibaba.druid.sql.ast.statement;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLStatementImpl;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class SQLTruncateStatement extends SQLStatementImpl {

    private static final long serialVersionUID = 1L;
    protected List<SQLName>           tableNames = new ArrayList<SQLName>(2);

    public List<SQLName> getTableNames() {
        return tableNames;
    }

    public void setTableNames(List<SQLName> tableNames) {
        this.tableNames = tableNames;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, tableNames);
        }
    }
}
