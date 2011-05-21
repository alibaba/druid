package com.alibaba.druid.sql.dialect.oracle.ast.expr;

import com.alibaba.druid.sql.ast.expr.SQLNumericLiteralExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.visitor.OracleASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class OracleBinaryDoubleExpr extends SQLNumericLiteralExpr {

    private static final long serialVersionUID = 1L;

    private Double            value;

    public OracleBinaryDoubleExpr(){

    }

    public OracleBinaryDoubleExpr(Double value){
        super();
        this.value = value;
    }

    @Override
    public Number getNumber() {
        return value;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        accept0((OracleASTVisitor) visitor);
    }

    protected void accept0(OracleASTVisitor visitor) {
        visitor.visit(this);
        visitor.endVisit(this);
    }

}
