package com.alibaba.druid.proxy.jdbc;

import java.math.BigDecimal;
import java.sql.Types;
import java.util.Calendar;

public final class JdbcParameterDecimal implements JdbcParameter {

    private final BigDecimal value;

    public JdbcParameterDecimal(BigDecimal value){
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
        return Types.DECIMAL;
    }

}
