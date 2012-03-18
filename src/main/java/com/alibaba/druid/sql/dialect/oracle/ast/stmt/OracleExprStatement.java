package com.alibaba.druid.sql.dialect.oracle.ast.stmt;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleASTVisitor;

public class OracleExprStatement extends OracleStatementImpl {

    private static final long   serialVersionUID = 1L;

    private SQLExpr expr;

    public OracleExprStatement(){

    }

    public OracleExprStatement(SQLExpr expr){
        this.expr = expr;
    }

    @Override
    public void accept0(OracleASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, expr);
        }
        visitor.endVisit(this);

    }

    public SQLExpr getExpr() {
        return expr;
    }

    public void setExpr(SQLExpr expr) {
        this.expr = expr;
    }

}
