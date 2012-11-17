package com.alibaba.druid.sql.ast.statement;

import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLObjectImpl;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class SQLAlterTableDropIndex extends SQLObjectImpl implements SQLAlterTableItem {

    private static final long serialVersionUID = 1L;

    private SQLName           indexName;

    public SQLName getIndexName() {
        return indexName;
    }

    public void setIndexName(SQLName indexName) {
        this.indexName = indexName;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, indexName);
        }
        visitor.endVisit(this);
    }

}
