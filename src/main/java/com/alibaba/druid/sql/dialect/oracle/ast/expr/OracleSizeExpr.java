package com.alibaba.druid.sql.dialect.oracle.ast.expr;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.OracleSQLObjectImpl;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleASTVisitor;

public class OracleSizeExpr extends OracleSQLObjectImpl implements OracleExpr {

    private static final long serialVersionUID = 1L;

    private SQLExpr           value;
    private Unit              unit;

    public OracleSizeExpr(){

    }

    public OracleSizeExpr(SQLExpr value, Unit unit){
        super();
        this.value = value;
        this.unit = unit;
    }

    @Override
    public void accept0(OracleASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, value);
        }
        visitor.endVisit(this);
    }

    public SQLExpr getValue() {
        return value;
    }

    public void setValue(SQLExpr value) {
        this.value = value;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public static enum Unit {
        K, M, G, T, P, E
    }
}
