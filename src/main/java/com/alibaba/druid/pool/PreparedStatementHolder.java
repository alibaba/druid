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
package com.alibaba.druid.pool;

import com.alibaba.druid.pool.DruidPooledPreparedStatement.PreparedStatementKey;

import java.sql.PreparedStatement;

/**
 * @author wenshao [szujobs@hotmail.com]
 */
public final class PreparedStatementHolder {

    public final PreparedStatementKey key;
    public final PreparedStatement    statement;
    private int                       hitCount                 = 0;

    private int                       fetchRowPeak             = -1;

    private int                       defaultRowPrefetch       = -1;
    private int                       rowPrefetch              = -1;

    private boolean                   enterOracleImplicitCache = false;

    private int                       inUseCount               = 0;
    private boolean                   pooling                  = false;

    public PreparedStatementHolder(PreparedStatementKey key, PreparedStatement stmt){
        this.key = key;
        this.statement = stmt;
    }

    public boolean isEnterOracleImplicitCache() {
        return enterOracleImplicitCache;
    }

    public void setEnterOracleImplicitCache(boolean enterOracleImplicitCache) {
        this.enterOracleImplicitCache = enterOracleImplicitCache;
    }

    public int getDefaultRowPrefetch() {
        return defaultRowPrefetch;
    }

    public void setDefaultRowPrefetch(int defaultRowPrefetch) {
        this.defaultRowPrefetch = defaultRowPrefetch;
    }

    public int getRowPrefetch() {
        return rowPrefetch;
    }

    public void setRowPrefetch(int rowPrefetch) {
        this.rowPrefetch = rowPrefetch;
    }

    public int getFetchRowPeak() {
        return fetchRowPeak;
    }

    public void setFetchRowPeak(int fetchRowPeak) {
        if (fetchRowPeak > this.fetchRowPeak) {
            this.fetchRowPeak = fetchRowPeak;
        }
    }

    public void incrementHitCount() {
        hitCount++;
    }

    public int getHitCount() {
        return hitCount;
    }

    public boolean isInUse() {
        return inUseCount > 0;
    }

    public void incrementInUseCount() {
        inUseCount++;
    }

    public void decrementInUseCount() {
        inUseCount--;
    }

    public int getInUseCount() {
        return inUseCount;
    }

    public boolean isPooling() {
        return pooling;
    }

    public void setPooling(boolean pooling) {
        this.pooling = pooling;
    }

}
