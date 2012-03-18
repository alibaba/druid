package com.alibaba.druid.sql.dialect.oracle.ast.expr;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.OracleSQLObjectImpl;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleASTVisitor;

public class OracleArgumentExpr extends OracleSQLObjectImpl implements SQLExpr {

    private static final long serialVersionUID = 1L;
    private String            argumentName;
    private SQLExpr           value;

    public OracleArgumentExpr(){

    }

    public OracleArgumentExpr(String argumentName, SQLExpr value){
        this.argumentName = argumentName;
        this.value = value;
    }

    public String getArgumentName() {
        return argumentName;
    }

    public void setArgumentName(String argumentName) {
        this.argumentName = argumentName;
    }

    public SQLExpr getValue() {
        return value;
    }

    public void setValue(SQLExpr value) {
        this.value = value;
    }

    @Override
    public void accept0(OracleASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, value);
        }
        visitor.endVisit(this);
    }

}
