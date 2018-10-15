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

import com.alibaba.druid.support.monitor.annotation.AggregateType;
import com.alibaba.druid.support.monitor.annotation.MField;
import com.alibaba.druid.support.monitor.annotation.MTable;

import java.util.LinkedHashMap;
import java.util.Map;

@MTable(name = "druid_wall_sql")
public class WallSqlStatValue {

    private String  sql;

    @MField(groupBy = true, aggregate = AggregateType.None, hashFor = "sql", hashForType = "sql")
    private long    sqlHash;

    private String  sqlSample;

    @MField(groupBy = true, aggregate = AggregateType.None, hashFor = "sqlSample", hashForType = "sqlSample")
    private long    sqlSampleHash;

    @MField(aggregate = AggregateType.Sum)
    private long    executeCount;

    @MField(aggregate = AggregateType.Sum)
    private long    executeErrorCount;

    @MField(aggregate = AggregateType.Sum)
    private long    fetchRowCount;

    @MField(aggregate = AggregateType.Sum)
    private long    updateCount;

    @MField(aggregate = AggregateType.Last)
    private boolean syntaxError;

    @MField(aggregate = AggregateType.Last)
    private String  violationMessage;

    public WallSqlStatValue(){

    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public long getSqlHash() {
        return sqlHash;
    }

    public void setSqlHash(long sqlHash) {
        this.sqlHash = sqlHash;
    }

    public String getSqlSample() {
        return sqlSample;
    }

    public void setSqlSample(String sqlSample) {
        this.sqlSample = sqlSample;
    }

    public long getExecuteCount() {
        return executeCount;
    }

    public void setExecuteCount(long executeCount) {
        this.executeCount = executeCount;
    }

    public long getFetchRowCount() {
        return fetchRowCount;
    }

    public void setFetchRowCount(long fetchRowCount) {
        this.fetchRowCount = fetchRowCount;
    }

    public long getUpdateCount() {
        return updateCount;
    }

    public void setUpdateCount(long updateCount) {
        this.updateCount = updateCount;
    }

    public boolean isSyntaxError() {
        return syntaxError;
    }

    public void setSyntaxError(boolean syntaxError) {
        this.syntaxError = syntaxError;
    }

    public String getViolationMessage() {
        return violationMessage;
    }

    public void setViolationMessage(String violationMessage) {
        this.violationMessage = violationMessage;
    }

    public long getExecuteErrorCount() {
        return executeErrorCount;
    }

    public void setExecuteErrorCount(long executeErrorCount) {
        this.executeErrorCount = executeErrorCount;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> sqlStatMap = new LinkedHashMap<String, Object>();
        sqlStatMap.put("sql", sql);
        if (!sql.equals(sqlSample)) {
            sqlStatMap.put("sample", sqlSample);
        }
        sqlStatMap.put("executeCount", getExecuteCount());
        
        if (executeErrorCount > 0) {
            sqlStatMap.put("executeErrorCount", executeErrorCount);
        }

        if (fetchRowCount > 0) {
            sqlStatMap.put("fetchRowCount", fetchRowCount);
        }

        if (updateCount > 0) {
            sqlStatMap.put("updateCount", updateCount);
        }

        if (violationMessage != null) {
            sqlStatMap.put("violationMessage", violationMessage);
        }

        return sqlStatMap;
    }
}
