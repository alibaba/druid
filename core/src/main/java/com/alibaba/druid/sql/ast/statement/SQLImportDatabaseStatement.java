package com.alibaba.druid.sql.ast.statement;

import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLStatementImpl;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class SQLImportDatabaseStatement extends SQLStatementImpl {
    private SQLName db;
    private SQLName status;

    public SQLName getDb() {
        return db;
    }

    public void setDb(SQLName db) {
        this.db = db;
    }

    public SQLName getStatus() {
        return status;
    }

    public void setStatus(SQLName status) {
        this.status = status;
    }

    protected void accept0(SQLASTVisitor v) {
        if (v.visit(this)) {
            acceptChild(v, db);
            acceptChild(v, status);
        }
        v.endVisit(this);
    }
}
