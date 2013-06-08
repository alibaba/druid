package com.alibaba.druid.proxy.jdbc;

import java.sql.Types;
import java.util.Calendar;

public final class JdbcParameterString implements JdbcParameter {

    private final String value;

    public JdbcParameterString(String value){
        this.value = value;
    }

    @Override
    public Object getValue() {
        return value;
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
        return Types.VARCHAR;
    }

}
