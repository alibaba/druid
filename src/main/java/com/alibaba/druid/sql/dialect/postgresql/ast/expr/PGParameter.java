package com.alibaba.druid.sql.dialect.postgresql.ast.expr;

import com.alibaba.druid.sql.ast.SQLDataType;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.dialect.postgresql.ast.PGSQLObjectImpl;
import com.alibaba.druid.sql.dialect.postgresql.visitor.PGASTVisitor;

public class PGParameter extends PGSQLObjectImpl {

    private static final long serialVersionUID = 1L;
    private SQLExpr           name;
    private SQLDataType       dataType;


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
    public void accept0(PGASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, name);
            acceptChild(visitor, dataType);
        }
        visitor.endVisit(this);
    }
}
