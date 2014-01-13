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

package com.alibaba.druid.proxy.jdbc;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import com.alibaba.druid.filter.FilterChain;

/**
 * @author kiki
 */
public class ResultSetMetaDataProxyImpl extends WrapperProxyImpl implements ResultSetMetaDataProxy {

    private final ResultSetMetaData metaData;

    private final ResultSetProxy    resultSetProxy;

    public ResultSetMetaDataProxyImpl(ResultSetMetaData metaData, long id, ResultSetProxy resultSetProxy){
        super(metaData, id);
        this.metaData = metaData;
        this.resultSetProxy = resultSetProxy;
    }

    @Override
    public int getColumnCount() throws SQLException {
        return metaData.getColumnCount() - resultSetProxy.getHiddenColumnCount();
    }

    @Override
    public boolean isAutoIncrement(int column) throws SQLException {
        return metaData.isAutoIncrement(resultSetProxy.getPhysicalColumn(column));
    }

    @Override
    public boolean isCaseSensitive(int column) throws SQLException {
        return metaData.isCaseSensitive(resultSetProxy.getPhysicalColumn(column));
    }

    @Override
    public boolean isSearchable(int column) throws SQLException {
        return metaData.isSearchable(resultSetProxy.getPhysicalColumn(column));
    }

    @Override
    public boolean isCurrency(int column) throws SQLException {
        return metaData.isCurrency(resultSetProxy.getPhysicalColumn(column));
    }

    @Override
    public int isNullable(int column) throws SQLException {
        return metaData.isNullable(resultSetProxy.getPhysicalColumn(column));
    }

    @Override
    public boolean isSigned(int column) throws SQLException {
        return metaData.isSigned(resultSetProxy.getPhysicalColumn(column));
    }

    @Override
    public int getColumnDisplaySize(int column) throws SQLException {
        return metaData.getColumnDisplaySize(resultSetProxy.getPhysicalColumn(column));
    }

    @Override
    public String getColumnLabel(int column) throws SQLException {
        return metaData.getColumnLabel(resultSetProxy.getPhysicalColumn(column));
    }

    @Override
    public String getColumnName(int column) throws SQLException {
        return metaData.getColumnName(resultSetProxy.getPhysicalColumn(column));
    }

    @Override
    public String getSchemaName(int column) throws SQLException {
        return metaData.getSchemaName(resultSetProxy.getPhysicalColumn(column));
    }

    @Override
    public int getPrecision(int column) throws SQLException {
        return metaData.getPrecision(resultSetProxy.getPhysicalColumn(column));
    }

    @Override
    public int getScale(int column) throws SQLException {
        return metaData.getScale(resultSetProxy.getPhysicalColumn(column));
    }

    @Override
    public String getTableName(int column) throws SQLException {
        return metaData.getTableName(resultSetProxy.getPhysicalColumn(column));
    }

    @Override
    public String getCatalogName(int column) throws SQLException {
        return metaData.getCatalogName(resultSetProxy.getPhysicalColumn(column));
    }

    @Override
    public int getColumnType(int column) throws SQLException {
        return metaData.getColumnType(resultSetProxy.getPhysicalColumn(column));
    }

    @Override
    public String getColumnTypeName(int column) throws SQLException {
        return metaData.getColumnTypeName(resultSetProxy.getPhysicalColumn(column));
    }

    @Override
    public boolean isReadOnly(int column) throws SQLException {
        return metaData.isReadOnly(resultSetProxy.getPhysicalColumn(column));
    }

    @Override
    public boolean isWritable(int column) throws SQLException {
        return metaData.isWritable(resultSetProxy.getPhysicalColumn(column));
    }

    @Override
    public boolean isDefinitelyWritable(int column) throws SQLException {
        return metaData.isDefinitelyWritable(resultSetProxy.getPhysicalColumn(column));
    }

    @Override
    public String getColumnClassName(int column) throws SQLException {
        return metaData.getColumnClassName(resultSetProxy.getPhysicalColumn(column));
    }

    @Override
    public ResultSetMetaData getResultSetMetaDataRaw() {
        return metaData;
    }

    @Override
    public FilterChain createChain() {
        return null;
    }

}
