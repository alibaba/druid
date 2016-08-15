/*
 * Copyright 1999-2101 Alibaba Group Holding Ltd.
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

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.NClob;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.alibaba.druid.mock.MockParameterMetaData;
import com.alibaba.druid.mock.MockResultSetMetaData;

public abstract class PreparedStatementBase extends StatementBase implements PreparedStatement {

    private List<Object>          parameters        = new ArrayList<Object>();

    private MockParameterMetaData metadata          = new MockParameterMetaData();

    private MockResultSetMetaData resultSetMetaData = new MockResultSetMetaData();

    public PreparedStatementBase(Connection connection){
        super(connection);
    }

    public List<Object> getParameters() {
        return parameters;
    }

    @Override
    public void setNull(int parameterIndex, int sqlType) throws SQLException {
        parameters.add(parameterIndex - 1, null);
    }

    @Override
    public void setBoolean(int parameterIndex, boolean x) throws SQLException {
        parameters.add(parameterIndex - 1, x);
    }

    @Override
    public void setByte(int parameterIndex, byte x) throws SQLException {
        parameters.add(parameterIndex - 1, x);
    }

    @Override
    public void setShort(int parameterIndex, short x) throws SQLException {
        parameters.add(parameterIndex - 1, x);
    }

    @Override
    public void setInt(int parameterIndex, int x) throws SQLException {
        parameters.add(parameterIndex - 1, x);
    }

    @Override
    public void setLong(int parameterIndex, long x) throws SQLException {
        parameters.add(parameterIndex - 1, x);
    }

    @Override
    public void setFloat(int parameterIndex, float x) throws SQLException {
        parameters.add(parameterIndex - 1, x);
    }

    @Override
    public void setDouble(int parameterIndex, double x) throws SQLException {
        parameters.add(parameterIndex - 1, x);
    }

    @Override
    public void setBigDecimal(int parameterIndex, BigDecimal x) throws SQLException {
        parameters.add(parameterIndex - 1, x);
    }

    @Override
    public void setString(int parameterIndex, String x) throws SQLException {
        parameters.add(parameterIndex - 1, x);
    }

    @Override
    public void setBytes(int parameterIndex, byte[] x) throws SQLException {
        parameters.add(parameterIndex - 1, x);
    }

    @Override
    public void setDate(int parameterIndex, Date x) throws SQLException {
        parameters.add(parameterIndex - 1, x);
    }

    @Override
    public void setTime(int parameterIndex, Time x) throws SQLException {
        parameters.add(parameterIndex - 1, x);
    }

    @Override
    public void setTimestamp(int parameterIndex, Timestamp x) throws SQLException {
        parameters.add(parameterIndex - 1, x);
    }

    @Override
    public void setAsciiStream(int parameterIndex, InputStream x, int length) throws SQLException {
        parameters.add(parameterIndex - 1, x);
    }

    @Override
    public void setUnicodeStream(int parameterIndex, InputStream x, int length) throws SQLException {
        parameters.add(parameterIndex - 1, x);
    }

    @Override
    public void setBinaryStream(int parameterIndex, InputStream x, int length) throws SQLException {
        parameters.add(parameterIndex - 1, x);
    }

    @Override
    public void clearParameters() throws SQLException {
        checkOpen();

        parameters.clear();
    }

    @Override
    public void setObject(int parameterIndex, Object x, int targetSqlType) throws SQLException {
        parameters.add(parameterIndex - 1, x);
    }

    @Override
    public void setObject(int parameterIndex, Object x) throws SQLException {
        parameters.add(parameterIndex - 1, x);
    }

    @Override
    public void addBatch() throws SQLException {
        checkOpen();
    }

    @Override
    public void setCharacterStream(int parameterIndex, Reader reader, int length) throws SQLException {
        parameters.add(parameterIndex - 1, reader);
    }

    @Override
    public void setRef(int parameterIndex, Ref x) throws SQLException {
        parameters.add(parameterIndex - 1, x);
    }

    @Override
    public void setBlob(int parameterIndex, Blob x) throws SQLException {
        parameters.add(parameterIndex - 1, x);
    }

    @Override
    public void setClob(int parameterIndex, Clob x) throws SQLException {
        parameters.add(parameterIndex - 1, x);
    }

    @Override
    public void setArray(int parameterIndex, Array x) throws SQLException {
        parameters.add(parameterIndex - 1, x);
    }

    @Override
    public ResultSetMetaData getMetaData() throws SQLException {
        checkOpen();

        return resultSetMetaData;
    }

    @Override
    public void setDate(int parameterIndex, Date x, Calendar cal) throws SQLException {
        parameters.add(parameterIndex - 1, x);
    }

    @Override
    public void setTime(int parameterIndex, Time x, Calendar cal) throws SQLException {
        parameters.add(parameterIndex - 1, x);
    }

    @Override
    public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal) throws SQLException {
        parameters.add(parameterIndex - 1, x);
    }

    @Override
    public void setNull(int parameterIndex, int sqlType, String typeName) throws SQLException {
        parameters.add(parameterIndex - 1, null);
    }

    @Override
    public void setURL(int parameterIndex, URL x) throws SQLException {
        parameters.add(parameterIndex - 1, x);
    }

    @Override
    public ParameterMetaData getParameterMetaData() throws SQLException {
        checkOpen();
        return metadata;
    }

    @Override
    public void setRowId(int parameterIndex, RowId x) throws SQLException {
        parameters.add(parameterIndex - 1, x);
    }

    @Override
    public void setNString(int parameterIndex, String value) throws SQLException {
        parameters.add(parameterIndex - 1, value);
    }

    @Override
    public void setNCharacterStream(int parameterIndex, Reader value, long length) throws SQLException {
        parameters.add(parameterIndex - 1, value);
    }

    @Override
    public void setNClob(int parameterIndex, NClob value) throws SQLException {
        parameters.add(parameterIndex - 1, value);
    }

    @Override
    public void setClob(int parameterIndex, Reader value, long length) throws SQLException {
        parameters.add(parameterIndex - 1, value);
    }

    @Override
    public void setBlob(int parameterIndex, InputStream inputStream, long length) throws SQLException {
        parameters.add(parameterIndex - 1, inputStream);
    }

    @Override
    public void setNClob(int parameterIndex, Reader reader, long length) throws SQLException {
        parameters.add(parameterIndex - 1, reader);
    }

    @Override
    public void setSQLXML(int parameterIndex, SQLXML xmlObject) throws SQLException {
        parameters.add(parameterIndex - 1, xmlObject);
    }

    @Override
    public void setObject(int parameterIndex, Object x, int targetSqlType, int scaleOrLength) throws SQLException {
        parameters.add(parameterIndex - 1, x);
    }

    @Override
    public void setAsciiStream(int parameterIndex, InputStream x, long length) throws SQLException {
        parameters.add(parameterIndex - 1, x);
    }

    @Override
    public void setBinaryStream(int parameterIndex, InputStream x, long length) throws SQLException {
        parameters.add(parameterIndex - 1, x);
    }

    @Override
    public void setCharacterStream(int parameterIndex, Reader reader, long length) throws SQLException {
        parameters.add(parameterIndex - 1, reader);
    }

    @Override
    public void setAsciiStream(int parameterIndex, InputStream x) throws SQLException {
        parameters.add(parameterIndex - 1, x);
    }

    @Override
    public void setBinaryStream(int parameterIndex, InputStream x) throws SQLException {
        parameters.add(parameterIndex - 1, x);
    }

    @Override
    public void setCharacterStream(int parameterIndex, Reader reader) throws SQLException {
        parameters.add(parameterIndex - 1, reader);
    }

    @Override
    public void setNCharacterStream(int parameterIndex, Reader value) throws SQLException {
        parameters.add(parameterIndex - 1, value);
    }

    @Override
    public void setClob(int parameterIndex, Reader reader) throws SQLException {
        parameters.add(parameterIndex - 1, reader);
    }

    @Override
    public void setBlob(int parameterIndex, InputStream inputStream) throws SQLException {
        parameters.add(parameterIndex - 1, inputStream);
    }

    @Override
    public void setNClob(int parameterIndex, Reader reader) throws SQLException {
        parameters.add(parameterIndex - 1, reader);
    }

}
