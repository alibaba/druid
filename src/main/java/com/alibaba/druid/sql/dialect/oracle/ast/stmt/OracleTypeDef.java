package com.alibaba.druid.sql.dialect.oracle.ast.stmt;

import com.alibaba.druid.sql.dialect.oracle.ast.OracleSQLObject;

public abstract class OracleTypeDef extends OracleSQLObject {
    private static final long serialVersionUID = 1L;

    public OracleTypeDef() {

    }

    public void output(StringBuffer buf) {
        buf.append(super.toString());
    }
}
