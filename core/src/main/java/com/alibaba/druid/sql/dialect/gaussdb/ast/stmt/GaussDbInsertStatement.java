package com.alibaba.druid.sql.dialect.gaussdb.ast.stmt;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.dialect.postgresql.ast.stmt.PGInsertStatement;

import java.util.ArrayList;
import java.util.List;

public class GaussDbInsertStatement extends PGInsertStatement {
    private boolean isIgnore;
    private final List<SQLExpr> duplicateKeyUpdate = new ArrayList<SQLExpr>();

    public void setIgnore(boolean ignore) {
        isIgnore = ignore;
    }

    public boolean isIgnore() {
        return isIgnore;
    }

    public List<SQLExpr> getDuplicateKeyUpdate() {
        return duplicateKeyUpdate;
    }

    public void cloneTo(GaussDbInsertStatement x) {
        super.cloneTo(x);
        x.isIgnore = isIgnore;
        for (SQLExpr item : duplicateKeyUpdate) {
            SQLExpr item2 = item.clone();
            item2.setParent(x);
            x.duplicateKeyUpdate.add(item2);
        }
    }

    @Override
    public GaussDbInsertStatement clone() {
        GaussDbInsertStatement x = new GaussDbInsertStatement();
        cloneTo(x);
        return x;
    }
}
