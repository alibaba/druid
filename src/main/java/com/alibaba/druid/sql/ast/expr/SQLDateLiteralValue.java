package com.alibaba.druid.sql.ast.expr;

public class SQLDateLiteralValue {
    private int years;
    private int months;
    private int days;

    public int getYears() {
        return years;
    }

    public void setYears(int years) {
        this.years = years;
    }

    public int getMonths() {
        return months;
    }

    public void setMonths(int months) {
        this.months = months;
    }

    public int getDays() {
        return days;
    }

    public void setDays(int days) {
        this.days = days;
    }

    public void output(StringBuffer buf) {
        buf.append(years);
        buf.append("-");
        buf.append(months);
        buf.append("-");
        buf.append(days);
    }

}
