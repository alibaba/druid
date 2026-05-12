package com.alibaba.druid.sql.dialect.sqlite.ast;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLStatementImpl;
import com.alibaba.druid.sql.dialect.sqlite.visitor.SQLiteASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class SQLiteReindexStatement extends SQLStatementImpl {
    private SQLName name;

    public SQLiteReindexStatement() {
        super(DbType.sqlite);
    }

    public SQLName getName() {
        return name;
    }

    public void setName(SQLName name) {
        if (name != null) {
            name.setParent(this);
        }
        this.name = name;
    }

    @Override
    protected void accept0(SQLASTVisitor v) {
        if (v instanceof SQLiteASTVisitor) {
            accept0((SQLiteASTVisitor) v);
        }
    }

    protected void accept0(SQLiteASTVisitor v) {
        if (v.visit(this)) {
            acceptChild(v, name);
        }
        v.endVisit(this);
    }
}
