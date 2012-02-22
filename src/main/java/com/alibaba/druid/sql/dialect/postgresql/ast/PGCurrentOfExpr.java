package com.alibaba.druid.sql.dialect.postgresql.ast;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.dialect.postgresql.visitor.PGASTVisitor;

public class PGCurrentOfExpr extends PGSQLObjectImpl implements SQLExpr {

    private static final long serialVersionUID = 1L;
    private SQLExpr           cursor;

    public SQLExpr getCursor() {
        return cursor;
    }

    public void setCursor(SQLExpr cursor) {
        this.cursor = cursor;
    }

    @Override
    public void accept0(PGASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, cursor);
        }
        visitor.endVisit(this);
    }

}
