package com.alibaba.druid.sql.ast.statement;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLStatementImpl;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class SQLShowTablesStatement extends SQLStatementImpl {

    protected SQLName database;
    protected SQLExpr like;

    public SQLName getDatabase() {
        return database;
    }

    public void setDatabase(SQLName database) {
        if (database != null) {
            database.setParent(this);
        }

        this.database = database;
    }

    public SQLExpr getLike() {
        return like;
    }

    public void setLike(SQLExpr like) {
        if (like != null) {
            like.setParent(this);
        }

        this.like = like;
    }
    
    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, database);
            acceptChild(visitor, like);
        }
    }
}
