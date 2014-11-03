package com.alibaba.druid.sql.dialect.postgresql.ast.expr;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.dialect.postgresql.visitor.PGASTVisitor;

public class PGArrayExpr extends PGExprImpl {

    private List<SQLExpr> values = new ArrayList<SQLExpr>();

    public List<SQLExpr> getValues() {
        return values;
    }

    public void setValues(List<SQLExpr> values) {
        this.values = values;
    }

    @Override
    public void accept0(PGASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, values);
        }
        visitor.endVisit(this);
    }

    @Override
    public int hashCode() {
        return values.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        PGArrayExpr other = (PGArrayExpr) obj;
        if (values == null) {
            if (other.values != null) return false;
        } else if (!values.equals(other.values)) return false;
        return true;
    }

}
