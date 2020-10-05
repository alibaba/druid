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
package com.alibaba.druid.wall;

import com.alibaba.druid.DbType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WallContext {

    private final static ThreadLocal<WallContext> contextLocal                 = new ThreadLocal<WallContext>();

    private WallSqlStat                           sqlStat;
    private Map<String, WallSqlTableStat>         tableStats;
    private Map<String, WallSqlFunctionStat>      functionStats;
    private final DbType                          dbType;
    private int                                   commentCount;
    private int                                   warnings                     = 0;
    private int                                   unionWarnings                = 0;
    private int                                   updateNoneConditionWarnings  = 0;
    private int                                   deleteNoneConditionWarnings  = 0;
    private int                                   likeNumberWarnings           = 0;

    private List<WallUpdateCheckItem>             wallUpdateCheckItems;

    public WallContext(String dbType){
        this(DbType.of(dbType));
    }

    public WallContext(DbType dbType){
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

    public static WallContext createIfNotExists(DbType dbType) {
        WallContext context = contextLocal.get();
        if (context == null) {
            context = new WallContext(dbType);
            contextLocal.set(context);
        }
        return context;
    }

    public static WallContext create(String dbType) {
        return create(DbType.of(dbType));
    }

    public static WallContext create(DbType dbType) {
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

    public DbType getDbType() {
        return dbType;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void incrementCommentCount() {
        if (this.commentCount == 0) {
            this.warnings++;
        }
        this.commentCount++;
    }

    public int getWarnings() {
        return warnings;
    }

    public void incrementWarnings() {
        this.warnings++;
    }

    public int getLikeNumberWarnings() {
        return likeNumberWarnings;
    }

    public void incrementLikeNumberWarnings() {
        if (likeNumberWarnings == 0) {
            this.warnings++;
        }
        likeNumberWarnings++;
    }

    public int getUnionWarnings() {
        return unionWarnings;
    }

    public void incrementUnionWarnings() {
        if (this.unionWarnings == 0) {
            this.incrementWarnings();
        }
        this.unionWarnings++;
    }

    public int getUpdateNoneConditionWarnings() {
        return updateNoneConditionWarnings;
    }

    public void incrementUpdateNoneConditionWarnings() {
        // if (this.updateNoneConditionWarnings == 0) {
        // this.incrementWarnings();
        // }
        this.updateNoneConditionWarnings++;
    }

    public int getDeleteNoneConditionWarnings() {
        return deleteNoneConditionWarnings;
    }

    public void incrementDeleteNoneConditionWarnings() {
        // if (this.deleteNoneConditionWarnings == 0) {
        // this.incrementWarnings();
        // }
        this.deleteNoneConditionWarnings++;
    }

    public List<WallUpdateCheckItem> getWallUpdateCheckItems() {
        return wallUpdateCheckItems;
    }

    public void setWallUpdateCheckItems(List<WallUpdateCheckItem> wallUpdateCheckItems) {
        this.wallUpdateCheckItems = wallUpdateCheckItems;
    }
}
