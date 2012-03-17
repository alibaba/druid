package com.alibaba.druid.sql.ast.statement;

import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.SQLStatementImpl;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class SQLUseStatement extends SQLStatementImpl implements SQLStatement {

    private static final long serialVersionUID = 1L;
    private SQLName           database;

    public SQLName getDatabase() {
        return database;
    }

    public void setDatabase(SQLName database) {
        this.database = database;
    }

    @Override
    public void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, database);
        }
        visitor.endVisit(this);
    }

}
