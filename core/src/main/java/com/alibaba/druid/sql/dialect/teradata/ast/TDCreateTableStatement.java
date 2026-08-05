package com.alibaba.druid.sql.dialect.teradata.ast;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLPrimaryKey;
import com.alibaba.druid.sql.dialect.teradata.visitor.TDASTVisitor;

public class TDCreateTableStatement extends SQLCreateTableStatement implements TDObject {
    private OnCommitType onCommitRows;
    private SQLPrimaryKey primaryKey;
    public OnCommitType getOnCommitRows() {
        return onCommitRows;
    }

    public void setOnCommitRows(OnCommitType onCommitRows) {
        this.onCommitRows = onCommitRows;
    }

    public SQLPrimaryKey getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(SQLPrimaryKey primaryKey) {
        if (primaryKey != null) {
            primaryKey.setParent(this);
        }
        this.primaryKey = primaryKey;
    }

    public TDCreateTableStatement(DbType dbType) {
        super(dbType);
    }

    @Override
    public TDCreateTableStatement clone() {
        TDCreateTableStatement x = new TDCreateTableStatement(dbType);
        cloneTo(x);
        return x;
    }

    public void cloneTo(TDCreateTableStatement x) {
        super.cloneTo(x);
        x.onCommitRows = onCommitRows;
        if (primaryKey != null) {
            x.setPrimaryKey((SQLPrimaryKey) primaryKey.clone());
        }
    }

    @Override
    public void accept0(TDASTVisitor visitor) {
        if (visitor.visit(this)) {
            visitor.endVisit(this);
        }
    }

    public enum OnCommitType {
        DELETE,
        PRESERVE
    }
}
