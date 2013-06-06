package com.alibaba.druid.proxy.jdbc;

import java.sql.Types;
import java.util.Calendar;

public class JdbcParameterInt implements JdbcParameter {

    private final int value;

    public JdbcParameterInt(int value){
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
        return Types.INTEGER;
    }

}
