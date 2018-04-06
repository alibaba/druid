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
package com.alibaba.druid.sql.ast.expr;

public enum SQLIntervalUnit {
    YEAR, YEAR_MONTH,

    QUARTER,

    MONTH, WEEK, DAY, DAY_HOUR, DAY_MINUTE, DAY_SECOND, DAY_MICROSECOND,

    HOUR, HOUR_MINUTE, HOUR_SECOND, HOUR_MICROSECOND,

    MINUTE, MINUTE_SECOND, MINUTE_MICROSECOND,

    SECOND, SECOND_MICROSECOND,

    MICROSECOND;
    
    public final String name_lcase;
    
    private SQLIntervalUnit() {
        this.name_lcase = name().toLowerCase();
    }
}
