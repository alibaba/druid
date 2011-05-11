package com.alibaba.druid.sql.dialect.oracle.ast.expr;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLExprImpl;
import com.alibaba.druid.sql.dialect.oracle.ast.visitor.OracleASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class OracleExtractExpr extends SQLExprImpl {
    private OracleDateTimeUnit unit;
    private SQLExpr from;

    public OracleExtractExpr() {

    }

    public OracleDateTimeUnit getUnit() {
        return this.unit;
    }

    public void setUnit(OracleDateTimeUnit unit) {
        this.unit = unit;
    }

    public SQLExpr getFrom() {
        return this.from;
    }

    public void setFrom(SQLExpr from) {
        this.from = from;
    }

    public void output(StringBuffer buf) {
        buf.append("EXTRACT(");
        buf.append(this.unit.name());
        buf.append(" FROM ");
        this.from.output(buf);
        buf.append(")");
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        this.accept0((OracleASTVisitor) visitor);
    }

    protected void accept0(OracleASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.from);
        }

        visitor.endVisit(this);
    }
}
