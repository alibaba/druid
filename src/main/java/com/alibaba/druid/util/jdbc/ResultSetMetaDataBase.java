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
package com.alibaba.druid.util.jdbc;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ResultSetMetaDataBase implements ResultSetMetaData {

    public ResultSetMetaDataBase(){

    }

    private final List<ColumnMetaData> columns = new ArrayList<ColumnMetaData>();

    public List<ColumnMetaData> getColumns() {
        return columns;
    }
    
    public int findColumn(String columnName) throws SQLException {
        for (int i = 0; i < columns.size(); ++i) {
            ColumnMetaData column = columns.get(i);
            if (column.getColumnName().equals(columnName)) {
                return i + 1;
            }
        }
        
        throw new SQLException("column '" + columnName + "' not found.");
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        if (isWrapperFor(iface)) {
            return (T) this;
        }
        return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        if (iface == null) {
            return false;
        }

        return iface.isAssignableFrom(this.getClass());

    }

    @Override
    public int getColumnCount() throws SQLException {
        return columns.size();
    }

    @Override
    public boolean isAutoIncrement(int column) throws SQLException {
        return getColumn(column).isAutoIncrement();
    }

    @Override
    public boolean isCaseSensitive(int column) throws SQLException {
        return getColumn(column).isCaseSensitive();
    }

    @Override
    public boolean isSearchable(int column) throws SQLException {
        return getColumn(column).isSearchable();
    }

    @Override
    public boolean isCurrency(int column) throws SQLException {
        return getColumn(column).isCurrency();
    }

    @Override
    public int isNullable(int column) throws SQLException {
        return getColumn(column).getNullable();
    }

    @Override
    public boolean isSigned(int column) throws SQLException {
        return getColumn(column).isSigned();
    }

    @Override
    public int getColumnDisplaySize(int column) throws SQLException {
        return getColumn(column).getColumnDisplaySize();
    }

    @Override
    public String getColumnLabel(int column) throws SQLException {
        return getColumn(column).getColumnLabel();
    }
    
    public ColumnMetaData getColumn(int column) {
        return columns.get(column - 1);
    }

    @Override
    public String getColumnName(int column) throws SQLException {
        return getColumn(column).getColumnName();
    }

    @Override
    public String getSchemaName(int column) throws SQLException {
        return getColumn(column).getSchemaName();
    }

    @Override
    public int getPrecision(int column) throws SQLException {
        return getColumn(column).getPrecision();
    }

    @Override
    public int getScale(int column) throws SQLException {
        return getColumn(column).getScale();
    }

    @Override
    public String getTableName(int column) throws SQLException {
        return getColumn(column).getTableName();
    }

    @Override
    public String getCatalogName(int column) throws SQLException {
        return getColumn(column).getCatalogName();
    }

    @Override
    public int getColumnType(int column) throws SQLException {
        return getColumn(column).getColumnType();
    }

    @Override
    public String getColumnTypeName(int column) throws SQLException {
        return getColumn(column).getColumnTypeName();
    }

    @Override
    public boolean isReadOnly(int column) throws SQLException {
        return getColumn(column).isReadOnly();
    }

    @Override
    public boolean isWritable(int column) throws SQLException {
        return getColumn(column).isWritable();
    }

    @Override
    public boolean isDefinitelyWritable(int column) throws SQLException {
        return getColumn(column).isDefinitelyWritable();
    }

    @Override
    public String getColumnClassName(int column) throws SQLException {
        return getColumn(column).getColumnClassName();
    }

    public static class ColumnMetaData {

        private boolean autoIncrement = false;
        private boolean caseSensitive;
        private boolean searchable;
        private boolean currency;
        private int     nullable      = 0;
        private boolean signed;
        private int     columnDisplaySize;
        private String  columnLabel;
        private String  columnName;
        private String  schemaName;
        private int     precision;
        private int     scale;
        private String  tableName;
        private String  catalogName;
        private int     columnType;
        private String  columnTypeName;
        private boolean readOnly;
        private boolean writable;
        private boolean definitelyWritable;
        private String  columnClassName;

        public boolean isAutoIncrement() {
            return autoIncrement;
        }

        public void setAutoIncrement(boolean autoIncrement) {
            this.autoIncrement = autoIncrement;
        }

        public boolean isCaseSensitive() {
            return caseSensitive;
        }

        public void setCaseSensitive(boolean caseSensitive) {
            this.caseSensitive = caseSensitive;
        }

        public boolean isSearchable() {
            return searchable;
        }

        public void setSearchable(boolean searchable) {
            this.searchable = searchable;
        }

        public boolean isCurrency() {
            return currency;
        }

        public void setCurrency(boolean currency) {
            this.currency = currency;
        }

        public int getNullable() {
            return nullable;
        }

        public void setNullable(int nullable) {
            this.nullable = nullable;
        }

        public boolean isSigned() {
            return signed;
        }

        public void setSigned(boolean signed) {
            this.signed = signed;
        }

        public int getColumnDisplaySize() {
            return columnDisplaySize;
        }

        public void setColumnDisplaySize(int columnDisplaySize) {
            this.columnDisplaySize = columnDisplaySize;
        }

        public String getColumnLabel() {
            return columnLabel;
        }

        public void setColumnLabel(String columnLabel) {
            this.columnLabel = columnLabel;
        }

        public String getColumnName() {
            return columnName;
        }

        public void setColumnName(String columnName) {
            this.columnName = columnName;
        }

        public String getSchemaName() {
            return schemaName;
        }

        public void setSchemaName(String schemaName) {
            this.schemaName = schemaName;
        }

        public int getPrecision() {
            return precision;
        }

        public void setPrecision(int precision) {
            this.precision = precision;
        }

        public int getScale() {
            return scale;
        }

        public void setScale(int scale) {
            this.scale = scale;
        }

        public String getTableName() {
            return tableName;
        }

        public void setTableName(String tableName) {
            this.tableName = tableName;
        }

        public String getCatalogName() {
            return catalogName;
        }

        public void setCatalogName(String catalogName) {
            this.catalogName = catalogName;
        }

        public int getColumnType() {
            return columnType;
        }

        public void setColumnType(int columnType) {
            this.columnType = columnType;
        }

        public String getColumnTypeName() {
            return columnTypeName;
        }

        public void setColumnTypeName(String columnTypeName) {
            this.columnTypeName = columnTypeName;
        }

        public boolean isReadOnly() {
            return readOnly;
        }

        public void setReadOnly(boolean readOnly) {
            this.readOnly = readOnly;
        }

        public boolean isWritable() {
            return writable;
        }

        public void setWritable(boolean writable) {
            this.writable = writable;
        }

        public boolean isDefinitelyWritable() {
            return definitelyWritable;
        }

        public void setDefinitelyWritable(boolean definitelyWritable) {
            this.definitelyWritable = definitelyWritable;
        }

        public String getColumnClassName() {
            return columnClassName;
        }

        public void setColumnClassName(String columnClassName) {
            this.columnClassName = columnClassName;
        }

    }
}
