package com.alibaba.druid.sql.dialect.sqlserver.ast;

import com.alibaba.druid.sql.ast.SQLExpr;

import java.util.ArrayList;
import java.util.List;

public abstract class SQLServerSelectBasePivot extends SQLServerObjectImpl {

    protected final List<SQLExpr> pivotFor = new ArrayList<>();

    public List<SQLExpr> getPivotFor() {
        return pivotFor;
    }
}
