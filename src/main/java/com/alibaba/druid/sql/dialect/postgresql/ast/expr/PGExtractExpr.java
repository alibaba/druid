package com.alibaba.druid.sql.dialect.postgresql.ast.expr;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.dialect.postgresql.visitor.PGASTVisitor;

public class PGExtractExpr extends PGExprImpl {

    private PGDateField field;
    private SQLExpr     source;

    public PGDateField getField() {
        return field;
    }

    public void setField(PGDateField field) {
        this.field = field;
    }

    public SQLExpr getSource() {
        return source;
    }

    public void setSource(SQLExpr source) {
        this.source = source;
    }

    @Override
    public void accept0(PGASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, source);
        }
        visitor.endVisit(this);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((field == null) ? 0 : field.hashCode());
        result = prime * result + ((source == null) ? 0 : source.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        PGExtractExpr other = (PGExtractExpr) obj;
        if (field != other.field) {
            return false;
        }
        if (source == null) {
            if (other.source != null) {
                return false;
            }
        } else if (!source.equals(other.source)) {
            return false;
        }
        return true;
    }

}
