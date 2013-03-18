/*
 * Copyright 1999-2011 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.wall;

public class WallContext {

    private final static ThreadLocal<WallContext> contextLocal = new ThreadLocal<WallContext>();

    private WallSqlStat                           sqlState;
    private final String                          dbType;

    public WallContext(String dbType){
        this.dbType = dbType;
    }

    public static WallContext createIfNotExists(String dbType) {
        WallContext context = contextLocal.get();
        if (context == null) {
            context = new WallContext(dbType);
            contextLocal.set(context);
        }
        return context;
    }

    public static WallContext current() {
        return contextLocal.get();
    }

    public static void clearContext() {
        contextLocal.remove();
    }

    public WallSqlStat getSqlState() {
        return sqlState;
    }

    public void setSqlState(WallSqlStat sqlState) {
        this.sqlState = sqlState;
    }

    public String getDbType() {
        return dbType;
    }

}
