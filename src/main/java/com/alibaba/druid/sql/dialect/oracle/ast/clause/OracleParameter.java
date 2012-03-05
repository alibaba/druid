package com.alibaba.druid.sql.dialect.oracle.ast.clause;

import com.alibaba.druid.sql.ast.SQLDataType;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.OracleSQLObjectImpl;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleASTVisitor;

public class OracleParameter extends OracleSQLObjectImpl {

    private static final long serialVersionUID = 1L;
    private SQLExpr           name;
    private SQLDataType       dataType;
    private SQLExpr           defaultValue;

    public SQLExpr getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(SQLExpr deaultValue) {
        this.defaultValue = deaultValue;
    }

    public SQLExpr getName() {
        return name;
    }

    public void setName(SQLExpr name) {
        this.name = name;
    }

    public SQLDataType getDataType() {
        return dataType;
    }

    public void setDataType(SQLDataType dataType) {
        this.dataType = dataType;
    }

    @Override
    public void accept0(OracleASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, name);
            acceptChild(visitor, dataType);
        }
        visitor.endVisit(this);
    }
}
