package com.alibaba.druid.proxy.jdbc;

import java.util.Calendar;

public class JdbcParameterImpl implements JdbcParameter {

    private final int      sqlType;
    private final Object   value;
    private final long     length;
    private final Calendar calendar;
    private final int      scaleOrLength;

    public JdbcParameterImpl(int sqlType, Object value, long length, Calendar calendar, int scaleOrLength){
        this.sqlType = sqlType;
        this.value = value;
        this.length = length;
        this.calendar = calendar;
        this.scaleOrLength = scaleOrLength;
    }

    public JdbcParameterImpl(int sqlType, Object value, long length, Calendar calendar){
        this(sqlType, value, -1, null, -1);
    }

    public JdbcParameterImpl(int sqlType, Object value){
        this(sqlType, value, -1, null);
    }

    public JdbcParameterImpl(int sqlType, Object value, long length){
        this(sqlType, value, length, null);
    }

    public JdbcParameterImpl(int sqlType, Object value, Calendar calendar){
        this(sqlType, value, -1, calendar);
    }

    public int getScaleOrLength() {
        return scaleOrLength;
    }

    public Object getValue() {
        return value;
    }

    public long getLength() {
        return length;
    }

    public Calendar getCalendar() {
        return calendar;
    }

    public int getSqlType() {
        return sqlType;
    }
}
