/*
 * Copyright 1999-2101 Alibaba Group Holding Ltd.
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
        
        if (0 == (x.compareTo(BigDecimal.ZERO))) {
            return ZERO;
        }
        
        if (0 == (x.compareTo(BigDecimal.TEN))) {
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
