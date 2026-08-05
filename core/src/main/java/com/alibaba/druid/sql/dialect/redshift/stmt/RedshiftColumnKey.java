package com.alibaba.druid.sql.dialect.redshift.stmt;

import com.alibaba.druid.sql.dialect.redshift.visitor.RedshiftASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class RedshiftColumnKey extends RedshiftColumnConstraint {
    private boolean isDistKey;
    private boolean isSortKey;

    public boolean isDistKey() {
        return isDistKey;
    }

    public void setDistKey(boolean distKey) {
        isDistKey = distKey;
    }

    public boolean isSortKey() {
        return isSortKey;
    }

    public void setSortKey(boolean sortKey) {
        isSortKey = sortKey;
    }

    @Override
    protected void accept0(SQLASTVisitor v) {
        if (v instanceof RedshiftASTVisitor) {
            ((RedshiftASTVisitor) v).visit(this);
            ((RedshiftASTVisitor) v).endVisit(this);
        }
    }

    @Override
    public RedshiftColumnKey clone() {
        RedshiftColumnKey x = new RedshiftColumnKey();
        cloneTo(x);
        return x;
    }

    public void cloneTo(RedshiftColumnKey x) {
        super.cloneTo(x);
        x.isDistKey = isDistKey;
        x.isSortKey = isSortKey;
    }
}
