package com.alibaba.druid.sql.dialect.oracle.ast.expr;

import com.alibaba.druid.sql.dialect.oracle.ast.visitor.OracleASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class OracleDateExpr extends OracleDatetimeLiteralExpr {
    public OracleDateExpr() {

    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        this.accept0((OracleASTVisitor) visitor);
    }

    protected void accept0(OracleASTVisitor visitor) {
        visitor.visit(this);
        visitor.endVisit(this);
    }

    public void output(StringBuffer buf) {
        buf.append("DATE '");

        buf.append(this.year);
        buf.append('-');
        buf.append(this.month);
        buf.append("-");
        buf.append(this.dayOfMonth);

        if ((this.hour != 0) || (this.minute != 0) || (this.second != 0)) {
            buf.append(' ');
            buf.append(this.hour);
            buf.append(":");
            buf.append(this.minute);
            if (this.second != 0) {
                buf.append(":");
                buf.append(this.second);
            }
        }

        buf.append("'");
    }
}
