package com.alibaba.druid.sql.ast.expr;

import com.alibaba.druid.sql.ast.SQLDataType;

public abstract class SQLDateTypeExpr extends SQLTypeExpr {
    public SQLDateTypeExpr(SQLDataType sqlDataType) {
        super(sqlDataType);
    }

    @Override
    public abstract Object getValue();

    @Override
    public abstract SQLTypeExpr clone();
}
