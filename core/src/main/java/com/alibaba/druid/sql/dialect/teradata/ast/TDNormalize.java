package com.alibaba.druid.sql.dialect.teradata.ast;

import com.alibaba.druid.sql.ast.SQLObjectImpl;
import com.alibaba.druid.sql.dialect.teradata.visitor.TDASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class TDNormalize extends SQLObjectImpl implements TDObject {
    private boolean meets;
    private boolean overlaps;
    private boolean meetsFirst;

    public boolean isMeets() {
        return meets;
    }

    public void setMeets(boolean meets) {
        this.meets = meets;
    }

    public boolean isOverlaps() {
        return overlaps;
    }

    public void setOverlaps(boolean overlaps) {
        this.overlaps = overlaps;
    }

    public boolean isMeetsFirst() {
        return meetsFirst;
    }

    public void setMeetsFirst(boolean meetsFirst) {
        this.meetsFirst = meetsFirst;
    }

    @Override
    public void accept0(SQLASTVisitor v) {
        if (v instanceof TDASTVisitor) {
            accept0((TDASTVisitor) v);
        }
    }

    @Override
    public void accept0(TDASTVisitor visitor) {
        if (visitor.visit(this)) {
            visitor.endVisit(this);
        }
    }
}
