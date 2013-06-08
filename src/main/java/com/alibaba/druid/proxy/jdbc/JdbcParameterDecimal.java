package com.alibaba.druid.proxy.jdbc;

import java.math.BigDecimal;
import java.sql.Types;
import java.util.Calendar;

public final class JdbcParameterDecimal implements JdbcParameter {

    private final BigDecimal           value;

    public static JdbcParameterDecimal NULL = new JdbcParameterDecimal(null);
    public static JdbcParameterDecimal ZERO = new JdbcParameterDecimal(BigDecimal.ZERO);
    public static JdbcParameterDecimal TEN = new JdbcParameterDecimal(BigDecimal.TEN);
    
    private JdbcParameterDecimal(BigDecimal value){
        this.value = value;
    }

    public static JdbcParameterDecimal valueOf(BigDecimal x) {
        if (x == null) {
            return NULL;
        }
        
        if (x == BigDecimal.ZERO) {
            return ZERO;
        }
        
        if (x == BigDecimal.TEN) {
            return TEN;
        }
        
        return new JdbcParameterDecimal(x);
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
