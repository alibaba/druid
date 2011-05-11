package com.alibaba.druid.sql.ast.expr;

import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class SQLTimeLiteralExpr extends SQLLiteralExpr {
    private static final long serialVersionUID = 1L;

    private SQLTimeLiteralValue time;
    private SQLTimeZoneIntervalValue timeZone;

    public SQLTimeLiteralExpr() {

    }

    public SQLTimeLiteralValue getTime() {
        return time;
    }

    public void setTime(SQLTimeLiteralValue time) {
        this.time = time;
    }

    public SQLTimeZoneIntervalValue getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(SQLTimeZoneIntervalValue timeZone) {
        this.timeZone = timeZone;
    }

    public void output(StringBuffer buf) {
        buf.append("TIME'");
        this.time.output(buf);
        if (timeZone != null) {
            timeZone.output(buf);
        }
        buf.append("'");
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        visitor.visit(this);

        visitor.endVisit(this);
    }

}
