package com.alibaba.druid.proxy.jdbc;

import java.util.Calendar;

public class JdbcParameterNull implements JdbcParameter {

    private final int sqlType;

    public JdbcParameterNull(int sqlType){
        this.sqlType = sqlType;
    }

    @Override
    public Object getValue() {
        return null;
    }

    @Override
    public long getLength() {
        return 0;
    }

    @Override
    public Calendar getCalendar() {
        return null;
    }

    @Override
    public int getSqlType() {
        return sqlType;
    }

}
