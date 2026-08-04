package com.alibaba.druid.sql.dialect.gaussdb.ast.stmt;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.dialect.postgresql.ast.stmt.PGInsertStatement;

import java.util.ArrayList;
import java.util.List;

public class GaussDbInsertStatement extends PGInsertStatement {
    private boolean isIgnore;
    private final List<SQLExpr> duplicateKeyUpdate = new ArrayList<SQLExpr>();
    private boolean duplicateKeyUpdateNothing;

    public void setIgnore(boolean ignore) {
        isIgnore = ignore;
    }

    public boolean isIgnore() {
        return isIgnore;
    }

    public List<SQLExpr> getDuplicateKeyUpdate() {
        return duplicateKeyUpdate;
    }

    public boolean isDuplicateKeyUpdateNothing() {
        return duplicateKeyUpdateNothing;
    }

    public void setDuplicateKeyUpdateNothing(boolean duplicateKeyUpdateNothing) {
        this.duplicateKeyUpdateNothing = duplicateKeyUpdateNothing;
    }
}
