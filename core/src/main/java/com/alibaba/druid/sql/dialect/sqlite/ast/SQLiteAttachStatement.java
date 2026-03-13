package com.alibaba.druid.sql.dialect.sqlite.ast;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLStatementImpl;
import com.alibaba.druid.sql.dialect.sqlite.visitor.SQLiteASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class SQLiteAttachStatement extends SQLStatementImpl {
    private SQLExpr database;
    private SQLName schemaName;

    public SQLiteAttachStatement() {
        super(DbType.sqlite);
    }

    public SQLExpr getDatabase() {
        return database;
    }

    public void setDatabase(SQLExpr database) {
        if (database != null) {
            database.setParent(this);
        }
        this.database = database;
    }

    public SQLName getSchemaName() {
        return schemaName;
    }

    public void setSchemaName(SQLName schemaName) {
        if (schemaName != null) {
            schemaName.setParent(this);
        }
        this.schemaName = schemaName;
    }

    @Override
    protected void accept0(SQLASTVisitor v) {
        if (v instanceof SQLiteASTVisitor) {
            accept0((SQLiteASTVisitor) v);
        }
    }

    protected void accept0(SQLiteASTVisitor v) {
        if (v.visit(this)) {
            acceptChild(v, database);
            acceptChild(v, schemaName);
        }
        v.endVisit(this);
    }
}
