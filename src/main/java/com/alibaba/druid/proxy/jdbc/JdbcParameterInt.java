/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
