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

import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import com.alibaba.druid.filter.FilterChainImpl;

/**
 * @author kiki
 */
public class ResultSetMetaDataProxyImpl extends WrapperProxyImpl implements ResultSetMetaDataProxy {

    private final ResultSetMetaData metaData;
    private final ResultSetProxy    resultSet;

    private FilterChainImpl         filterChain = null;

    public ResultSetMetaDataProxyImpl(ResultSetMetaData metaData, long id, ResultSetProxy resultSet){
        super(metaData, id);
        this.metaData = metaData;
        this.resultSet = resultSet;
    }

    @Override
    public int getColumnCount() throws SQLException {
        FilterChainImpl chain = createChain();
        int value = chain.resultSetMetaData_getColumnCount(this);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public boolean isAutoIncrement(int column) throws SQLException {
        FilterChainImpl chain = createChain();
        boolean value = chain.resultSetMetaData_isAutoIncrement(this, column);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public boolean isCaseSensitive(int column) throws SQLException {
        FilterChainImpl chain = createChain();
        boolean value = chain.resultSetMetaData_isCaseSensitive(this, column);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public boolean isSearchable(int column) throws SQLException {
        FilterChainImpl chain = createChain();
        boolean value = chain.resultSetMetaData_isSearchable(this, column);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public boolean isCurrency(int column) throws SQLException {
        FilterChainImpl chain = createChain();
        boolean value = chain.resultSetMetaData_isCurrency(this, column);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public int isNullable(int column) throws SQLException {
        FilterChainImpl chain = createChain();
        int value = chain.resultSetMetaData_isNullable(this, column);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public boolean isSigned(int column) throws SQLException {
        FilterChainImpl chain = createChain();
        boolean value = chain.resultSetMetaData_isSigned(this, column);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public int getColumnDisplaySize(int column) throws SQLException {
        FilterChainImpl chain = createChain();
        int value = chain.resultSetMetaData_getColumnDisplaySize(this, column);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public String getColumnLabel(int column) throws SQLException {
        FilterChainImpl chain = createChain();
        String value = chain.resultSetMetaData_getColumnLabel(this, column);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public String getColumnName(int column) throws SQLException {
        FilterChainImpl chain = createChain();
        String value = chain.resultSetMetaData_getColumnName(this, column);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public String getSchemaName(int column) throws SQLException {
        FilterChainImpl chain = createChain();
        String value = chain.resultSetMetaData_getSchemaName(this, column);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public int getPrecision(int column) throws SQLException {
        FilterChainImpl chain = createChain();
        int value = chain.resultSetMetaData_getPrecision(this, column);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public int getScale(int column) throws SQLException {
        FilterChainImpl chain = createChain();
        int value = chain.resultSetMetaData_getScale(this, column);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public String getTableName(int column) throws SQLException {
        FilterChainImpl chain = createChain();
        String value = chain.resultSetMetaData_getTableName(this, column);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public String getCatalogName(int column) throws SQLException {
        FilterChainImpl chain = createChain();
        String value = chain.resultSetMetaData_getCatalogName(this, column);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public int getColumnType(int column) throws SQLException {
        FilterChainImpl chain = createChain();
        int value = chain.resultSetMetaData_getColumnType(this, column);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public String getColumnTypeName(int column) throws SQLException {
        FilterChainImpl chain = createChain();
        String value = chain.resultSetMetaData_getColumnTypeName(this, column);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public boolean isReadOnly(int column) throws SQLException {
        FilterChainImpl chain = createChain();
        boolean value = chain.resultSetMetaData_isReadOnly(this, column);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public boolean isWritable(int column) throws SQLException {
        FilterChainImpl chain = createChain();
        boolean value = chain.resultSetMetaData_isWritable(this, column);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public boolean isDefinitelyWritable(int column) throws SQLException {
        FilterChainImpl chain = createChain();
        boolean value = chain.resultSetMetaData_isDefinitelyWritable(this, column);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public String getColumnClassName(int column) throws SQLException {

        FilterChainImpl chain = createChain();
        String value = chain.resultSetMetaData_getColumnClassName(this, column);
        recycleFilterChain(chain);
        return value;
    }

    public FilterChainImpl createChain() {
        FilterChainImpl chain = this.filterChain;
        if (chain == null) {
            chain = new FilterChainImpl(this.resultSet.getStatementProxy().getConnectionProxy().getDirectDataSource());
        } else {
            this.filterChain = null;
        }

        return chain;
    }

    public void recycleFilterChain(FilterChainImpl chain) {
        chain.reset();
        this.filterChain = chain;
    }

    @Override
    public ResultSetProxy getResultSetProxy() {
        return this.resultSet;
    }

    @Override
    public ResultSetMetaData getResultSetMetaDataRaw() {
        return this.metaData;
    }

}
