package com.alibaba.druid.sql.dialect.gaussdb.ast.stmt;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.dialect.postgresql.ast.stmt.PGInsertStatement;

import java.util.ArrayList;
import java.util.List;

public class GaussDbInsertStatement extends PGInsertStatement {
    private final List<SQLExpr> partition = new ArrayList<SQLExpr>(4);
    private boolean hasTableIdentifier;

    public List<SQLExpr> getPartition() {
        return partition;
    }

    public void setHasTableIdentifier(boolean hasTableIdentifier) {
        this.hasTableIdentifier = hasTableIdentifier;
    }

    public boolean isHasTableIdentifier() {
        return hasTableIdentifier;
    }
}
