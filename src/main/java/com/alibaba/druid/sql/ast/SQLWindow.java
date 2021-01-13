package com.alibaba.druid.sql.ast;

import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class SQLWindow extends SQLObjectImpl {
    private SQLName name;
    private SQLOver over;

    public SQLWindow(SQLName name, SQLOver over) {
        this.setName(name);
        this.setOver(over);
    }

    public SQLName getName() {
        return name;
    }

    public void setName(SQLName x) {
        if (x != null) {
            x.setParent(this);
        }
        this.name = x;
    }

    public SQLOver getOver() {
        return over;
    }

    public void setOver(SQLOver x) {
        if (x != null) {
            x.setParent(this);
        }
        this.over = x;
    }

    @Override
    protected void accept0(SQLASTVisitor v) {
        if (v.visit(this)) {
            acceptChild(v, name);
            acceptChild(v, over);
        }
        v.endVisit(this);
    }
}
