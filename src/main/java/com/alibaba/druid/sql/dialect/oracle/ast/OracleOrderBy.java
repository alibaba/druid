package com.alibaba.druid.sql.dialect.oracle.ast;

import com.alibaba.druid.sql.ast.SQLOrderBy;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSelectOrderByItem;
import com.alibaba.druid.sql.dialect.oracle.ast.visitor.OracleASTVisitor;

public class OracleOrderBy extends SQLOrderBy {
    private static final long serialVersionUID = 1L;

    private boolean sibings;

    public OracleOrderBy() {

    }

    public boolean isSibings() {
        return this.sibings;
    }

    public void setSibings(boolean sibings) {
        this.sibings = sibings;
    }

    protected void accept0(OracleASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.items);
        }

        visitor.endVisit(this);
    }

    public void output(StringBuffer buf) {
        buf.append("ORDER ");
        if (this.sibings) {
            buf.append("SIBLINGS ");
        }
        buf.append("BY ");

        int i = 0;
        for (int size = this.items.size(); i < size; ++i) {
            if (i != 0) {
                buf.append(", ");
            }
            ((OracleSelectOrderByItem) this.items.get(i)).output(buf);
        }
    }
}
