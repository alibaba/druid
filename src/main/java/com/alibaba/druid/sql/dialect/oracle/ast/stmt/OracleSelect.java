package com.alibaba.druid.sql.dialect.oracle.ast.stmt;

import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.dialect.oracle.ast.visitor.OracleASTVisitor;

public class OracleSelect extends SQLSelect {
    private static final long serialVersionUID = 1L;

    private OracleSelectForUpdate forUpdate;
    private OracleSelectRestriction restriction;

    public OracleSelect() {

    }

    public OracleSelectRestriction getRestriction() {
        return this.restriction;
    }

    public void setRestriction(OracleSelectRestriction restriction) {
        this.restriction = restriction;
    }

    public OracleSelectForUpdate getForUpdate() {
        return this.forUpdate;
    }

    public void setForUpdate(OracleSelectForUpdate forUpdate) {
        this.forUpdate = forUpdate;
    }

    public void output(StringBuffer buf) {
        this.query.output(buf);
        buf.append(" ");

        if (this.orderBy != null) this.orderBy.output(buf);
    }

    protected void accept0(OracleASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.query);
            acceptChild(visitor, this.restriction);
            acceptChild(visitor, this.orderBy);
            acceptChild(visitor, this.forUpdate);
        }
        visitor.endVisit(this);
    }
}
