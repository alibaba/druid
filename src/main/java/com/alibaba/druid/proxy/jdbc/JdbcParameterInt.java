package com.alibaba.druid.proxy.jdbc;

import java.sql.Types;
import java.util.Calendar;

public final class JdbcParameterInt implements JdbcParameter {
    
    private static JdbcParameterInt[] cache;
    
    static {
        int cacheSize = 127;
        cache = new JdbcParameterInt[cacheSize];
        for (int i = 0; i < cache.length; ++i) {
            cache[i] = new JdbcParameterInt(i);
        }
    }

    private final int value;

    private JdbcParameterInt(int value){
        this.value = value;
    }
    
    public static JdbcParameterInt valueOf(int value) {
        if (value >= 0 && value < cache.length) {
            return cache[value];
        }
        
        return new JdbcParameterInt(value);
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
