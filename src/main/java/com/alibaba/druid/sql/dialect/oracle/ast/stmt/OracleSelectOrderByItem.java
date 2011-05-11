package com.alibaba.druid.sql.dialect.oracle.ast.stmt;

import com.alibaba.druid.sql.ast.SQLOrderingSpecification;
import com.alibaba.druid.sql.ast.statement.SQLSelectOrderByItem;
import com.alibaba.druid.sql.dialect.oracle.ast.visitor.OracleASTVisitor;

public class OracleSelectOrderByItem extends SQLSelectOrderByItem {
    private static final long serialVersionUID = 1L;

    private NullsOrderType nullsOrderType;

    public OracleSelectOrderByItem() {

    }

    public NullsOrderType getNullsOrderType() {
        return this.nullsOrderType;
    }

    public void setNullsOrderType(NullsOrderType nullsOrderType) {
        this.nullsOrderType = nullsOrderType;
    }

    protected void accept0(OracleASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.expr);
        }

        visitor.endVisit(this);
    }

    public void output(StringBuffer buf) {
        this.expr.output(buf);
        if (SQLOrderingSpecification.ASC.equals(this.type)) buf.append(" ASC");
        else if (SQLOrderingSpecification.DESC.equals(this.type)) {
            buf.append(" DESC");
        }
        if (NullsOrderType.NullsFirst.equals(this.nullsOrderType)) buf.append(" NULLS FIRST");
        else if (NullsOrderType.NullsLast.equals(this.nullsOrderType)) buf.append(" NULLS LAST");
    }

    public static enum NullsOrderType {
        NullsFirst,
        NullsLast;

        public String toFormalString() {
            if (NullsFirst.equals(this)) {
                return "NULLS FIRST";
            }

            if (NullsLast.equals(this)) {
                return "NULLS LAST";
            }

            throw new IllegalArgumentException();
        }
    }

}
