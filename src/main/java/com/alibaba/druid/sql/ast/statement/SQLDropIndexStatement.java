package com.alibaba.druid.sql.ast.statement;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLStatementImpl;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class SQLDropIndexStatement extends SQLStatementImpl implements SQLDDLStatement {

    private static final long serialVersionUID = 1L;

    private SQLExpr           indexName;
    private SQLExpr           tableName;

    public SQLExpr getIndexName() {
        return indexName;
    }

    public void setIndexName(SQLExpr indexName) {
        this.indexName = indexName;
    }

    public SQLExpr getTableName() {
        return tableName;
    }

    public void setTableName(SQLExpr tableName) {
        this.tableName = tableName;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, indexName);
            acceptChild(visitor, tableName);
        }
        visitor.endVisit(this);
    }
}
