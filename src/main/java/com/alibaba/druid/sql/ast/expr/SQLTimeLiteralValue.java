package com.alibaba.druid.sql.ast.expr;

public class SQLTimeLiteralValue {
    private int hours;
    private int minutes;
    private int seconds;
    private int secondsFraction;

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

    public int getSeconds() {
        return seconds;
    }

    public void setSeconds(int seconds) {
        this.seconds = seconds;
    }

    public int getSecondsFraction() {
        return secondsFraction;
    }

    public void setSecondsFraction(int secondsFraction) {
        this.secondsFraction = secondsFraction;
    }

    public void output(StringBuffer buf) {
        buf.append(hours);
        buf.append(':');
        buf.append(minutes);
        buf.append(':');
        buf.append(seconds);
        if (secondsFraction > 0) {
            buf.append('.');
            buf.append(secondsFraction);
        }
    }
}
