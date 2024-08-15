package com.alibaba.druid.sql.dialect.redshift.stmt;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.dialect.redshift.visitor.RedshiftASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.ArrayList;
import java.util.List;

public class RedshiftSortKey extends RedshiftObjectImpl {
    private boolean compound;
    private boolean interleaved;
    private boolean auto;
    private List<SQLExpr> columns;

    public RedshiftSortKey() {
        compound = false;
        interleaved = false;
        auto = false;
        columns = new ArrayList<>();
    }

    public boolean isCompound() {
        return compound;
    }

    public void setCompound(boolean compound) {
        this.compound = compound;
    }

    public boolean isInterleaved() {
        return interleaved;
    }

    public void setInterleaved(boolean interleaved) {
        this.interleaved = interleaved;
    }

    public boolean isAuto() {
        return auto;
    }

    public void setAuto(boolean auto) {
        this.auto = auto;
    }

    public List<SQLExpr> getColumns() {
        return columns;
    }

    public void addColumn(SQLExpr sqlExpr) {
        if (sqlExpr != null) {
            sqlExpr.setParent(this);
            columns.add(sqlExpr);
        }
    }

    @Override
    public void accept0(SQLASTVisitor v) {
        if (v instanceof RedshiftASTVisitor) {
            accept0((RedshiftASTVisitor) v);
        }
    }

    @Override
    public void accept0(RedshiftASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, columns);
            visitor.endVisit(this);
        }
    }
}
