package com.alibaba.druid.sql.dialect.gaussdb.ast.stmt;

import com.alibaba.druid.sql.dialect.postgresql.ast.stmt.PGInsertStatement;

public class GaussDbInsertStatement extends PGInsertStatement {
    private boolean isIgnore;

    public void setIgnore(boolean ignore) {
        isIgnore = ignore;
    }

    public boolean isIgnore() {
        return isIgnore;
    }
}
