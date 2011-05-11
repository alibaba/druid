package com.alibaba.druid.sql.dialect.oracle.ast.stmt;

import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.dialect.oracle.ast.visitor.OracleASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class OracleSelectTableReference extends SQLExprTableSource implements OracleSelectTableSource {
    private static final long serialVersionUID = 1L;

    private boolean only = false;
    protected OracleSelectPivotBase pivot;

    public OracleSelectTableReference() {

    }

    public boolean isOnly() {
        return this.only;
    }

    public void setOnly(boolean only) {
        this.only = only;
    }

    public OracleSelectPivotBase getPivot() {
        return pivot;
    }

    public void setPivot(OracleSelectPivotBase pivot) {
        this.pivot = pivot;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        this.accept0((OracleASTVisitor) visitor);
    }

    protected void accept0(OracleASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.expr);
            acceptChild(visitor, this.pivot);
        }
        visitor.endVisit(this);
    }

    public void output(StringBuffer buf) {
        if (this.only) {
            buf.append("ONLY (");
            this.expr.output(buf);
            buf.append(")");
        } else {
            this.expr.output(buf);
        }

        if (this.pivot != null) {
            buf.append(" ");
            this.pivot.output(buf);
        }

        if ((this.alias != null) && (this.alias.length() != 0)) buf.append(this.alias);
    }
}
