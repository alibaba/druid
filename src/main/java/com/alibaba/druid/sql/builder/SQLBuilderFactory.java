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
package com.alibaba.druid.sql.builder;

import com.alibaba.druid.sql.builder.impl.SQLDeleteBuilderImpl;
import com.alibaba.druid.sql.builder.impl.SQLSelectBuilderImpl;
import com.alibaba.druid.sql.builder.impl.SQLUpdateBuilderImpl;

public class SQLBuilderFactory {

    public static SQLSelectBuilder createSelectSQLBuilder(String dbType) {
        return new SQLSelectBuilderImpl(dbType);
    }
    
    public static SQLSelectBuilder createSelectSQLBuilder(String sql, String dbType) {
        return new SQLSelectBuilderImpl(sql, dbType);
    }

    public static SQLDeleteBuilder createDeleteBuilder(String dbType) {
        return new SQLDeleteBuilderImpl(dbType);
    }
    
    public static SQLDeleteBuilder createDeleteBuilder(String sql, String dbType) {
        return new SQLDeleteBuilderImpl(sql, dbType);
    }

    public static SQLUpdateBuilder createUpdateBuilder(String dbType) {
        return new SQLUpdateBuilderImpl(dbType);
    }
    
    public static SQLUpdateBuilder createUpdateBuilder(String sql, String dbType) {
        return new SQLUpdateBuilderImpl(sql, dbType);
    }
}
