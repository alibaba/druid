package com.alibaba.druid.sql.dialect.redshift.stmt;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.dialect.redshift.visitor.RedshiftASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class RedshiftCreateTableStatement extends SQLCreateTableStatement implements RedshiftObject {
    private SQLExpr distStyle;
    private SQLExpr distKey;
    private SQLExpr backup;
    private RedshiftSortKey sortKey;
    private boolean encodeAuto;

    public RedshiftCreateTableStatement() {
        super(DbType.redshift);
        encodeAuto = false;
    }

    public SQLExpr getDistStyle() {
        return distStyle;
    }

    public void setDistStyle(SQLExpr distStyle) {
        if (distStyle != null) {
            distStyle.setParent(this);
        }
        this.distStyle = distStyle;
    }

    public SQLExpr getDistKey() {
        return distKey;
    }

    public void setDistKey(SQLExpr distKey) {
        if (distKey != null) {
            distKey.setParent(this);
        }
        this.distKey = distKey;
    }

    public RedshiftSortKey getSortKey() {
        return sortKey;
    }

    public void setSortKey(RedshiftSortKey sortKey) {
        if (sortKey != null) {
            sortKey.setParent(this);
        }
        this.sortKey = sortKey;
    }

    public boolean isEncodeAuto() {
        return encodeAuto;
    }

    public void setEncodeAuto(boolean encodeAuto) {
        this.encodeAuto = encodeAuto;
    }

    public SQLExpr getBackup() {
        return backup;
    }

    public void setBackup(SQLExpr backup) {
        if (backup != null) {
            backup.setParent(this);
        }
        this.backup = backup;
    }

    @Override
    public void accept0(SQLASTVisitor v) {
        if (v instanceof RedshiftASTVisitor) {
            accept0((RedshiftASTVisitor) v);
        } else {
            super.accept0(v);
        }
    }

    @Override
    public void accept0(RedshiftASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.backup);
            acceptChild(visitor, this.distStyle);
            acceptChild(visitor, this.distKey);
            acceptChild(visitor, this.sortKey);
        }
    }
}
