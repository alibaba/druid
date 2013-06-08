package com.alibaba.druid.proxy.jdbc;

import java.sql.Types;
import java.util.Calendar;

public final class JdbcParameterLong implements JdbcParameter {
    private static JdbcParameterLong[] cache;
    
    static {
        int cacheSize = 127;
        cache = new JdbcParameterLong[cacheSize];
        for (int i = 0; i < cache.length; ++i) {
            cache[i] = new JdbcParameterLong(i);
        }
    }
    
    private final long value;

    private JdbcParameterLong(long value){
        this.value = value;
    }
    
    public static JdbcParameterLong valueOf(long value) {
        if (value >= 0 && value < cache.length) {
            return cache[(int) value];
        }
        
        return new JdbcParameterLong(value);
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
