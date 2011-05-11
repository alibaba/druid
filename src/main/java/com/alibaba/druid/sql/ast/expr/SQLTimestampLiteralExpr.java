package com.alibaba.druid.sql.ast.expr;

import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class SQLTimestampLiteralExpr extends SQLLiteralExpr {
    private static final long serialVersionUID = 1L;

    private SQLDateLiteralValue dateValue;
    private SQLTimeLiteralValue timeValue;
    private SQLTimeZoneIntervalValue timeZoneValue;

    public SQLTimestampLiteralExpr() {

    }

    public SQLDateLiteralValue getDateValue() {
        return dateValue;
    }

    public void setDateValue(SQLDateLiteralValue dateValue) {
        this.dateValue = dateValue;
    }

    public SQLTimeLiteralValue getTimeValue() {
        return timeValue;
    }

    public void setTimeValue(SQLTimeLiteralValue timeValue) {
        this.timeValue = timeValue;
    }

    public SQLTimeZoneIntervalValue getTimeZoneValue() {
        return timeZoneValue;
    }

    public void setTimeZoneValue(SQLTimeZoneIntervalValue timeZoneValue) {
        this.timeZoneValue = timeZoneValue;
    }

    public void output(StringBuffer buf) {
        buf.append("TIMESTAMP'");
        this.dateValue.output(buf);
        buf.append(' ');
        this.timeValue.output(buf);
        if (timeZoneValue != null) {
            timeZoneValue.output(buf);
        }
        buf.append("'");
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        visitor.visit(this);
        visitor.endVisit(this);
    }

}
