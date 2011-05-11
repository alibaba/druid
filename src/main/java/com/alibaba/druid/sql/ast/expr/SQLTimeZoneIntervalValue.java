package com.alibaba.druid.sql.ast.expr;

public class SQLTimeZoneIntervalValue {
    private SQLUnaryOperator sign = SQLUnaryOperator.Plus;
    private int hours;
    private int minutes;

    public SQLUnaryOperator getSign() {
        return sign;
    }

    public void setSign(SQLUnaryOperator sign) {
        this.sign = sign;
    }

    public int getHours() {
        return hours;
    }

    public void setHours(int hours) {
        this.hours = hours;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    public void output(StringBuffer buf) {
        if (sign == SQLUnaryOperator.Plus) {
            buf.append('+');
        } else {
            buf.append('-');
        }
        buf.append(hours);
        buf.append(':');
        buf.append(minutes);
    }
}
