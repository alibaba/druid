package com.alibaba.druid.sql.dialect.oracle.ast.stmt;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.OracleSQLObject;

public abstract class OracleSelectPivotBase extends OracleSQLObject {
    private static final long serialVersionUID = 1L;

    protected final List<SQLExpr> pivotFor = new ArrayList<SQLExpr>();

    public OracleSelectPivotBase() {

    }

    public List<SQLExpr> getPivotFor() {
        return this.pivotFor;
    }
}
