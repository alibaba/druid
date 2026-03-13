package com.alibaba.druid.sql.dialect.sqlite.ast;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLStatementImpl;
import com.alibaba.druid.sql.dialect.sqlite.visitor.SQLiteASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class SQLitePragmaStatement extends SQLStatementImpl {
    private SQLName name;
    private SQLExpr value;

    public SQLitePragmaStatement() {
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

    public SQLExpr getValue() {
        return value;
    }

    public void setValue(SQLExpr value) {
        if (value != null) {
            value.setParent(this);
        }
        this.value = value;
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
            acceptChild(v, value);
        }
        v.endVisit(this);
    }
}
