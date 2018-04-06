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

import java.sql.Statement;
import java.util.List;
import java.util.Map;

import com.alibaba.druid.stat.JdbcSqlStat;

/**
 * @author wenshao [szujobs@hotmail.com]
 */
public interface StatementProxy extends Statement, WrapperProxy {

    ConnectionProxy getConnectionProxy();

    Statement getRawObject();

    List<String> getBatchSqlList();

    String getBatchSql();
    
    JdbcSqlStat getSqlStat();
    
    StatementExecuteType getLastExecuteType();
    
    void setSqlStat(JdbcSqlStat sqlStat);

    String getLastExecuteSql();
    
    long getLastExecuteStartNano();
    void setLastExecuteStartNano(long lastExecuteStartNano);
    void setLastExecuteStartNano();
    
    long getLastExecuteTimeNano();
    void setLastExecuteTimeNano(long nano);
    void setLastExecuteTimeNano();
    
    Map<Integer, JdbcParameter> getParameters();
    
    int getParametersSize();
    JdbcParameter getParameter(int i);
    
    boolean isFirstResultSet();
}
