package com.alibaba.druid.sql.dialect.oracle.ast.stmt;

import com.alibaba.druid.sql.ast.expr.SQLMethodInvokeExpr;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleASTVisitor;

public class OracleMethodInvokeStatement extends OracleStatementImpl {

    private static final long   serialVersionUID = 1L;

    private SQLMethodInvokeExpr expr;

    public OracleMethodInvokeStatement(){

    }

    public OracleMethodInvokeStatement(SQLMethodInvokeExpr expr){
        this.expr = expr;
    }

    @Override
    public void accept0(OracleASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, expr);
        }
        visitor.endVisit(this);

    }

    public SQLMethodInvokeExpr getExpr() {
        return expr;
    }

    public void setExpr(SQLMethodInvokeExpr expr) {
        this.expr = expr;
    }

}
