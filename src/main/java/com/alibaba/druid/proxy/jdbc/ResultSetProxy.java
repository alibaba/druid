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

import java.sql.ResultSet;
import java.util.List;
import java.util.Map;

import com.alibaba.druid.stat.JdbcSqlStat;

/**
 * @author wenshao [szujobs@hotmail.com]
 */
public interface ResultSetProxy extends ResultSet, WrapperProxy {

    ResultSet getResultSetRaw();

    StatementProxy getStatementProxy();

    String getSql();

    JdbcSqlStat getSqlStat();

    int getCursorIndex();

    int getFetchRowCount();

    long getConstructNano();

    void setConstructNano(long constructNano);

    void setConstructNano();

    int getCloseCount();

    void addReadStringLength(int length);

    long getReadStringLength();

    void addReadBytesLength(int length);

    long getReadBytesLength();

    void incrementOpenInputStreamCount();

    int getOpenInputStreamCount();

    void incrementOpenReaderCount();

    int getOpenReaderCount();

    int getPhysicalColumn(int logicColumn);

    int getLogicColumn(int physicalColumn);

    List<Integer> getHiddenColumns();

    int getHiddenColumnCount();

    void setLogicColumnMap(Map<Integer, Integer> logicColumnMap);

    void setPhysicalColumnMap(Map<Integer, Integer> physicalColumnMap);

    void setHiddenColumns(List<Integer> hiddenColumns);

}
