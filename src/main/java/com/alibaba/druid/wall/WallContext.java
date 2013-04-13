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

import java.util.HashMap;
import java.util.Map;

public class WallContext {

    private final static ThreadLocal<WallContext> contextLocal                 = new ThreadLocal<WallContext>();

    private WallSqlStat                           sqlStat;
    private Map<String, WallSqlTableStat>         tableStats;
    private Map<String, WallSqlFunctionStat>      functionStats;
    private final String                          dbType;
    private int                                   commentCount;
    private int                                   warnnings                    = 0;
    private int                                   unionWarnnings               = 0;
    private int                                   updateNoneConditionWarnnings = 0;
    private int                                   deleteNoneConditionWarnnings = 0;
    private int                                   likeNumberWarnnings          = 0;

    public WallContext(String dbType){
        this.dbType = dbType;
    }

    public void incrementFunctionInvoke(String tableName) {
        if (functionStats == null) {
            functionStats = new HashMap<String, WallSqlFunctionStat>();
        }

        String lowerCaseName = tableName.toLowerCase();

        WallSqlFunctionStat stat = functionStats.get(lowerCaseName);
        if (stat == null) {
            if (functionStats.size() > 100) {
                return;
            }

            stat = new WallSqlFunctionStat();
            functionStats.put(tableName, stat);
        }

        stat.incrementInvokeCount();
    }

    public WallSqlTableStat getTableStat(String tableName) {
        if (tableStats == null) {
            tableStats = new HashMap<String, WallSqlTableStat>(2);
        }

        String lowerCaseName = tableName.toLowerCase();

        WallSqlTableStat stat = tableStats.get(lowerCaseName);
        if (stat == null) {
            if (tableStats.size() > 100) {
                return null;
            }

            stat = new WallSqlTableStat();
            tableStats.put(tableName, stat);
        }
        return stat;
    }

    public static WallContext createIfNotExists(String dbType) {
        WallContext context = contextLocal.get();
        if (context == null) {
            context = new WallContext(dbType);
            contextLocal.set(context);
        }
        return context;
    }

    public static WallContext create(String dbType) {
        WallContext context = new WallContext(dbType);
        contextLocal.set(context);
        return context;
    }

    public static WallContext current() {
        return contextLocal.get();
    }

    public static void clearContext() {
        contextLocal.remove();
    }

    public static void setContext(WallContext context) {
        contextLocal.set(context);
    }

    public WallSqlStat getSqlStat() {
        return sqlStat;
    }

    public void setSqlStat(WallSqlStat sqlStat) {
        this.sqlStat = sqlStat;
    }

    public Map<String, WallSqlTableStat> getTableStats() {
        return tableStats;
    }

    public Map<String, WallSqlFunctionStat> getFunctionStats() {
        return functionStats;
    }

    public String getDbType() {
        return dbType;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void incrementCommentCount() {
        if (this.commentCount == 0) {
            this.warnnings++;
        }
        this.commentCount++;
    }

    public int getWarnnings() {
        return warnnings;
    }

    public void incrementWarnnings() {
        this.warnnings++;
    }

    public int getLikeNumberWarnnings() {
        return likeNumberWarnnings;
    }

    public void incrementLikeNumberWarnnings() {
        if (likeNumberWarnnings == 0) {
            this.warnnings++;
        }
        likeNumberWarnnings++;
    }

    public int getUnionWarnnings() {
        return unionWarnnings;
    }

    public void incrementUnionWarnnings() {
        if (this.unionWarnnings == 0) {
            this.incrementWarnnings();
        }
        this.unionWarnnings++;
    }

    public int getUpdateNoneConditionWarnnings() {
        return updateNoneConditionWarnnings;
    }

    public void incrementUpdateNoneConditionWarnnings() {
        if (this.updateNoneConditionWarnnings == 0) {
            this.incrementWarnnings();
        }
        this.updateNoneConditionWarnnings++;
    }

    public int getDeleteNoneConditionWarnnings() {
        return deleteNoneConditionWarnnings;
    }

    public void incrementDeleteNoneConditionWarnnings() {
        if (this.deleteNoneConditionWarnnings == 0) {
            this.incrementWarnnings();
        }
        this.deleteNoneConditionWarnnings++;
    }

}
