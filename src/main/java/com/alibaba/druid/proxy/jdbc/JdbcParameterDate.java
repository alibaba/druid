package com.alibaba.druid.proxy.jdbc;

import java.sql.Types;
import java.util.Calendar;
import java.util.Date;

public final class JdbcParameterDate implements JdbcParameter {

    private final Date value;

    public JdbcParameterDate(Date value){
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
        return Types.DATE;
    }

}
