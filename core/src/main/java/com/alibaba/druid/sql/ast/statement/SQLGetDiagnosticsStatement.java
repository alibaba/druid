package com.alibaba.druid.sql.ast.statement;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLReplaceable;
import com.alibaba.druid.sql.ast.SQLStatementImpl;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class SQLGetDiagnosticsStatement extends SQLStatementImpl implements SQLReplaceable {
    private SQLExpr expr;

    public SQLGetDiagnosticsStatement() {
    }

    public SQLGetDiagnosticsStatement(SQLExpr expr) {
        this.setExpr(expr);
    }

    @Override
    public void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, expr);
        }
        visitor.endVisit(this);

    }

    public SQLExpr getExpr() {
        return expr;
    }

    public void setExpr(SQLExpr x) {
        if (x != null) {
            x.setParent(this);
        }
        this.expr = x;
    }

    @Override
    public boolean replace(SQLExpr expr, SQLExpr target) {
        setExpr(target);
        return this.expr == expr;
    }
}
