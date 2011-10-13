package com.alibaba.druid.sql.dialect.oracle.ast.expr;

import com.alibaba.druid.sql.ast.expr.SQLNumericLiteralExpr;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class OracleBinaryFloatExpr extends SQLNumericLiteralExpr implements OracleExpr {

    private static final long serialVersionUID = 1L;

    private Float             value;

    public OracleBinaryFloatExpr(){

    }

    public OracleBinaryFloatExpr(Float value){
        super();
        this.value = value;
    }

    @Override
    public Number getNumber() {
        return value;
    }

    public Float getValue() {
        return value;
    }

    public void setValue(Float value) {
        this.value = value;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        accept0((OracleASTVisitor) visitor);
    }

    public void accept0(OracleASTVisitor visitor) {
        visitor.visit(this);
        visitor.endVisit(this);
    }

}
