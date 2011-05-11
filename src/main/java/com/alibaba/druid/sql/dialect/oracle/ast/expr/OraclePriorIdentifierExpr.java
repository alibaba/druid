package com.alibaba.druid.sql.dialect.oracle.ast.expr;

import java.io.Serializable;

import com.alibaba.druid.sql.ast.SQLExprImpl;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.visitor.OracleASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class OraclePriorIdentifierExpr extends SQLExprImpl implements Serializable {
    private static final long serialVersionUID = 1L;
    public SQLIdentifierExpr value;

    public OraclePriorIdentifierExpr() {

    }

    public OraclePriorIdentifierExpr(SQLIdentifierExpr value) {

        this.value = value;
    }

    public SQLIdentifierExpr getValue() {
        return value;
    }

    public void setValue(SQLIdentifierExpr value) {
        this.value = value;
    }

    public void output(StringBuffer buf) {
        buf.append("PRIOR ");
        buf.append(this.value);
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        this.accept0((OracleASTVisitor) visitor);
    }

    protected void accept0(OracleASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, value);
        }

        visitor.endVisit(this);
    }
}
