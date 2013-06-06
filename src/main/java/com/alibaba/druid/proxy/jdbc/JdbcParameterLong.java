package com.alibaba.druid.proxy.jdbc;

import java.sql.Types;
import java.util.Calendar;

public class JdbcParameterLong implements JdbcParameter {

    private final long value;

    public JdbcParameterLong(long value){
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
        return Types.BIGINT;
    }

}
