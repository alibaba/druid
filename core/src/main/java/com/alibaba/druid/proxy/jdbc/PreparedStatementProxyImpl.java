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

import com.alibaba.druid.filter.FilterChainImpl;
import com.alibaba.druid.proxy.jdbc.JdbcParameter.TYPE;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * @author wenshao [szujobs@hotmail.com]
 */
public class PreparedStatementProxyImpl extends StatementProxyImpl implements PreparedStatementProxy {
    private PreparedStatement statement;
    protected final String sql;
    private JdbcParameter[] parameters;
    private int parametersSize;
    private Map<Integer, JdbcParameter> paramMap;

    public PreparedStatementProxyImpl(ConnectionProxy connection, PreparedStatement statement, String sql, long id) {
        super(connection, statement, id);
        this.statement = statement;
        this.sql = sql;

        char quote = 0;
        int paramCount = 0;
        for (int i = 0; i < sql.length(); ++i) {
            char ch = sql.charAt(i);

            if (ch == '\'') {
                if (quote == 0) {
                    quote = ch;
                } else if (quote == '\'') {
                    quote = 0;
                }
            } else if (ch == '"') {
                if (quote == 0) {
                    quote = ch;
                } else if (quote == '"') {
                    quote = 0;
                }
            }

            if (quote == 0 && ch == '?') {
                paramCount++;
            }
        }

        parameters = new JdbcParameter[paramCount];
    }

    public Map<Integer, JdbcParameter> getParameters() {
        if (paramMap == null) {
            paramMap = new HashMap<Integer, JdbcParameter>(parametersSize);
            for (int i = 0; i < parametersSize; ++i) {
                paramMap.put(i, parameters[i]);
            }
        }

        return paramMap;
    }

    protected void setStatement(PreparedStatement statement) {
        super.statement = statement;
        this.statement = statement;
    }

    public void setParameter(int jdbcIndex, JdbcParameter parameter) {
        int index = jdbcIndex - 1;

        if (jdbcIndex > parametersSize) {
            parametersSize = jdbcIndex;
        }
        if (parametersSize >= parameters.length) {
            int oldCapacity = parameters.length;
            int newCapacity = oldCapacity + (oldCapacity >> 1);
            if (newCapacity <= 4) {
                newCapacity = 4;
            }

            parameters = Arrays.copyOf(parameters, newCapacity);
        }
        parameters[index] = parameter;

        if (paramMap != null) {
            paramMap = null;
        }
    }

    public int getParametersSize() {
        return parametersSize;
    }

    public JdbcParameter getParameter(int i) {
        if (i > parametersSize) {
            return null;
        }
        return this.parameters[i];
    }

    public String getSql() {
        return this.sql;
    }

    public PreparedStatement getRawObject() {
        return this.statement;
    }

    @Override
    public void addBatch() throws SQLException {
        FilterChainImpl chain = createChain();
        chain.preparedStatement_addBatch(this);
        recycleFilterChain(chain);
    }

    @Override
    public void clearParameters() throws SQLException {
        FilterChainImpl chain = createChain();
        chain.preparedStatement_clearParameters(this);
        recycleFilterChain(chain);
    }

    @Override
    public String getBatchSql() {
        return this.sql;
    }

    @Override
    public boolean execute() throws SQLException {
        updateCount = null;
        lastExecuteSql = sql;
        lastExecuteType = StatementExecuteType.Execute;
        lastExecuteStartNano = -1L;
        lastExecuteTimeNano = -1L;

        FilterChainImpl chain = createChain();
        firstResultSet = chain.preparedStatement_execute(this);
        recycleFilterChain(chain);
        return firstResultSet;
    }

    @Override
    public ResultSet executeQuery() throws SQLException {
        firstResultSet = true;

        updateCount = null;
        lastExecuteSql = sql;
        lastExecuteType = StatementExecuteType.ExecuteQuery;
        lastExecuteStartNano = -1L;
        lastExecuteTimeNano = -1L;

        FilterChainImpl chain = createChain();
        try {
            return chain.preparedStatement_executeQuery(this);
        } finally {
            recycleFilterChain(chain);
        }
    }

    @Override
    public int executeUpdate() throws SQLException {
        firstResultSet = false;

        updateCount = null;
        lastExecuteSql = sql;
        lastExecuteType = StatementExecuteType.ExecuteUpdate;
        lastExecuteStartNano = -1L;
        lastExecuteTimeNano = -1L;

        FilterChainImpl chain = createChain();
        updateCount = chain.preparedStatement_executeUpdate(this);
        recycleFilterChain(chain);
        return updateCount;
    }

    @Override
    public ResultSetMetaData getMetaData() throws SQLException {
        FilterChainImpl chain = createChain();
        try {
            return chain.preparedStatement_getMetaData(this);
        } finally {
            recycleFilterChain(chain);
        }
    }

    @Override
    public ParameterMetaData getParameterMetaData() throws SQLException {
        FilterChainImpl chain = createChain();
        try {
            return chain.preparedStatement_getParameterMetaData(this);
        } finally {
            recycleFilterChain(chain);
        }
    }

    @Override
    public void setArray(int parameterIndex, Array x) throws SQLException {
        setParameter(parameterIndex, createParameter(Types.ARRAY, x));

        FilterChainImpl chain = createChain();
        chain.preparedStatement_setArray(this, parameterIndex, x);
        recycleFilterChain(chain);
    }

    @Override
    public void setAsciiStream(int parameterIndex, InputStream x) throws SQLException {
        setParameter(parameterIndex, createParameter(JdbcParameter.TYPE.AsciiInputStream, x));

        FilterChainImpl chain = createChain();
        chain.preparedStatement_setAsciiStream(this, parameterIndex, x);
        recycleFilterChain(chain);
    }

    @Override
    public void setAsciiStream(int parameterIndex, InputStream x, int length) throws SQLException {
        setParameter(parameterIndex, createParameter(JdbcParameter.TYPE.AsciiInputStream, x, length));

        FilterChainImpl chain = createChain();
        chain.preparedStatement_setAsciiStream(this, parameterIndex, x, length);
        recycleFilterChain(chain);
    }

    @Override
    public void setAsciiStream(int parameterIndex, InputStream x, long length) throws SQLException {
        setParameter(parameterIndex, createParameter(JdbcParameter.TYPE.AsciiInputStream, x, length));

        FilterChainImpl chain = createChain();
        chain.preparedStatement_setAsciiStream(this, parameterIndex, x, length);
        recycleFilterChain(chain);
    }

    @Override
    public void setBigDecimal(int parameterIndex, BigDecimal x) throws SQLException {
        setParameter(parameterIndex, createParameter(x));

        FilterChainImpl chain = createChain();
        chain.preparedStatement_setBigDecimal(this, parameterIndex, x);
        recycleFilterChain(chain);
    }

    @Override
    public void setBinaryStream(int parameterIndex, InputStream x) throws SQLException {
        setParameter(parameterIndex, createParameter(JdbcParameter.TYPE.BinaryInputStream, x));

        FilterChainImpl chain = createChain();
        chain.preparedStatement_setBinaryStream(this, parameterIndex, x);
        recycleFilterChain(chain);
    }

    @Override
    public void setBinaryStream(int parameterIndex, InputStream x, int length) throws SQLException {
        setParameter(parameterIndex, createParameter(JdbcParameter.TYPE.BinaryInputStream, x, length));

        FilterChainImpl chain = createChain();
        chain.preparedStatement_setBinaryStream(this, parameterIndex, x, length);
        recycleFilterChain(chain);
    }

    @Override
    public void setBinaryStream(int parameterIndex, InputStream x, long length) throws SQLException {
        setParameter(parameterIndex, createParameter(JdbcParameter.TYPE.BinaryInputStream, x, length));

        FilterChainImpl chain = createChain();
        chain.preparedStatement_setBinaryStream(this, parameterIndex, x, length);
        recycleFilterChain(chain);
    }

    @Override
    public void setBlob(int parameterIndex, Blob x) throws SQLException {
        setParameter(parameterIndex, createParameter(Types.BLOB, x));

        FilterChainImpl chain = createChain();
        chain.preparedStatement_setBlob(this, parameterIndex, x);
        recycleFilterChain(chain);
    }

    @Override
    public void setBlob(int parameterIndex, InputStream x) throws SQLException {
        setParameter(parameterIndex, createParameter(Types.BLOB, x));

        FilterChainImpl chain = createChain();
        chain.preparedStatement_setBlob(this, parameterIndex, x);
        recycleFilterChain(chain);
    }

    @Override
    public void setBlob(int parameterIndex, InputStream x, long length) throws SQLException {
        setParameter(parameterIndex, createParameter(Types.BLOB, x, length));
        FilterChainImpl chain = createChain();
        chain.preparedStatement_setBlob(this, parameterIndex, x, length);
        recycleFilterChain(chain);
    }

    @Override
    public void setBoolean(int parameterIndex, boolean x) throws SQLException {
        setParameter(parameterIndex, createParameter(Types.BOOLEAN, x));
        FilterChainImpl chain = createChain();
        chain.preparedStatement_setBoolean(this, parameterIndex, x);
        recycleFilterChain(chain);
    }

    @Override
    public void setByte(int parameterIndex, byte x) throws SQLException {
        setParameter(parameterIndex, createParameter(Types.TINYINT, x));

        FilterChainImpl chain = createChain();
        chain.preparedStatement_setByte(this, parameterIndex, x);
        recycleFilterChain(chain);
    }

    @Override
    public void setBytes(int parameterIndex, byte[] x) throws SQLException {
        setParameter(parameterIndex, createParameter(TYPE.BYTES, x));

        FilterChainImpl chain = createChain();
        chain.preparedStatement_setBytes(this, parameterIndex, x);
        recycleFilterChain(chain);
    }

    @Override
    public void setCharacterStream(int parameterIndex, Reader x) throws SQLException {
        setParameter(parameterIndex, createParameter(TYPE.CharacterInputStream, x));

        FilterChainImpl chain = createChain();
        chain.preparedStatement_setCharacterStream(this, parameterIndex, x);
        recycleFilterChain(chain);
    }

    @Override
    public void setCharacterStream(int parameterIndex, Reader x, int length) throws SQLException {
        setParameter(parameterIndex, createParameter(TYPE.CharacterInputStream, x, length));

        FilterChainImpl chain = createChain();
        chain.preparedStatement_setCharacterStream(this, parameterIndex, x, length);
        recycleFilterChain(chain);
    }

    @Override
    public void setCharacterStream(int parameterIndex, Reader x, long length) throws SQLException {
        setParameter(parameterIndex, createParameter(TYPE.CharacterInputStream, x, length));

        FilterChainImpl chain = createChain();
        chain.preparedStatement_setCharacterStream(this, parameterIndex, x, length);
        recycleFilterChain(chain);
    }

    @Override
    public void setClob(int parameterIndex, Clob x) throws SQLException {
        setParameter(parameterIndex, createParameter(Types.CLOB, x));

        FilterChainImpl chain = createChain();
        chain.preparedStatement_setClob(this, parameterIndex, x);
        recycleFilterChain(chain);
    }

    @Override
    public void setClob(int parameterIndex, Reader x) throws SQLException {
        setParameter(parameterIndex, createParameter(Types.CLOB, x));

        FilterChainImpl chain = createChain();
        chain.preparedStatement_setClob(this, parameterIndex, x);
        recycleFilterChain(chain);
    }

    @Override
    public void setClob(int parameterIndex, Reader x, long length) throws SQLException {
        setParameter(parameterIndex, createParameter(Types.CLOB, x, length));

        FilterChainImpl chain = createChain();
        chain.preparedStatement_setClob(this, parameterIndex, x, length);
        recycleFilterChain(chain);
    }

    @Override
    public void setDate(int parameterIndex, Date x) throws SQLException {
        setParameter(
                parameterIndex,
                createParameter(x)
        );

        FilterChainImpl chain = createChain();
        chain.preparedStatement_setDate(this, parameterIndex, x);
        recycleFilterChain(chain);
    }

    @Override
    public void setDate(int parameterIndex, Date x, Calendar cal) throws SQLException {
        setParameter(
                parameterIndex,
                createParameter(Types.DATE, x, cal)
        );

        FilterChainImpl chain = createChain();
        chain.preparedStatement_setDate(this, parameterIndex, x, cal);
        recycleFilterChain(chain);
    }

    @Override
    public void setDouble(int parameterIndex, double x) throws SQLException {
        setParameter(
                parameterIndex,
                createParameter(Types.DOUBLE, x)
        );

        FilterChainImpl chain = createChain();
        chain.preparedStatement_setDouble(this, parameterIndex, x);
        recycleFilterChain(chain);
    }

    @Override
    public void setFloat(int parameterIndex, float x) throws SQLException {
        setParameter(
                parameterIndex,
                createParameter(Types.FLOAT, x)
        );

        FilterChainImpl chain = createChain();
        chain.preparedStatement_setFloat(this, parameterIndex, x);
        recycleFilterChain(chain);
    }

    @Override
    public void setInt(int parameterIndex, int x) throws SQLException {
        setParameter(
                parameterIndex,
                createParemeter(x)
        );

        FilterChainImpl chain = createChain();
        chain.preparedStatement_setInt(this, parameterIndex, x);
        recycleFilterChain(chain);
    }

    @Override
    public void setLong(int parameterIndex, long x) throws SQLException {
        setParameter(
                parameterIndex,
                createParameter(x)
        );

        FilterChainImpl chain = createChain();
        chain.preparedStatement_setLong(this, parameterIndex, x);
        recycleFilterChain(chain);
    }

    @Override
    public void setNCharacterStream(int parameterIndex, Reader x) throws SQLException {
        setParameter(
                parameterIndex,
                createParameter(TYPE.NCharacterInputStream, x)
        );

        FilterChainImpl chain = createChain();
        chain.preparedStatement_setNCharacterStream(this, parameterIndex, x);
        recycleFilterChain(chain);
    }

    @Override
    public void setNCharacterStream(int parameterIndex, Reader x, long length) throws SQLException {
        setParameter(parameterIndex, createParameter(TYPE.NCharacterInputStream, x, length));

        FilterChainImpl chain = createChain();
        chain.preparedStatement_setNCharacterStream(this, parameterIndex, x, length);
        recycleFilterChain(chain);
    }

    @Override
    public void setNClob(int parameterIndex, NClob x) throws SQLException {
        setParameter(parameterIndex, createParameter(Types.NCLOB, x));

        FilterChainImpl chain = createChain();
        chain.preparedStatement_setNClob(this, parameterIndex, x);
        recycleFilterChain(chain);
    }

    @Override
    public void setNClob(int parameterIndex, Reader x) throws SQLException {
        setParameter(parameterIndex, createParameter(Types.NCLOB, x));

        FilterChainImpl chain = createChain();
        chain.preparedStatement_setNClob(this, parameterIndex, x);
        recycleFilterChain(chain);
    }

    @Override
    public void setNClob(int parameterIndex, Reader x, long length) throws SQLException {
        setParameter(parameterIndex, createParameter(Types.NCLOB, x, length));

        FilterChainImpl chain = createChain();
        chain.preparedStatement_setNClob(this, parameterIndex, x, length);
        recycleFilterChain(chain);
    }

    @Override
    public void setNString(int parameterIndex, String x) throws SQLException {
        setParameter(parameterIndex, createParameter(Types.NVARCHAR, x));

        FilterChainImpl chain = createChain();
        chain.preparedStatement_setNString(this, parameterIndex, x);
        recycleFilterChain(chain);
    }

    @Override
    public void setNull(int parameterIndex, int sqlType) throws SQLException {
        setParameter(parameterIndex, createParameterNull(sqlType));

        FilterChainImpl chain = createChain();
        chain.preparedStatement_setNull(this, parameterIndex, sqlType);
        recycleFilterChain(chain);
    }

    @Override
    public void setNull(int parameterIndex, int sqlType, String typeName) throws SQLException {
        setParameter(parameterIndex, createParameterNull(sqlType));

        FilterChainImpl chain = createChain();
        chain.preparedStatement_setNull(this, parameterIndex, sqlType, typeName);
        recycleFilterChain(chain);
    }

    @Override
    public void setObject(int parameterIndex, Object x) throws SQLException {
        setObjectParameter(parameterIndex, x);

        FilterChainImpl chain = createChain();
        chain.preparedStatement_setObject(this, parameterIndex, x);
        recycleFilterChain(chain);
    }

    private void setObjectParameter(int parameterIndex, Object x) {
        if (x == null) {
            setParameter(parameterIndex, createParameterNull(Types.OTHER));
            return;
        }

        Class<?> clazz = x.getClass();
        if (clazz == Byte.class) {
            setParameter(parameterIndex, createParameter(Types.TINYINT, x));
            return;
        }

        if (clazz == Short.class) {
            setParameter(parameterIndex, createParameter(Types.SMALLINT, x));
            return;
        }

        if (clazz == Integer.class) {
            setParameter(parameterIndex, createParemeter((Integer) x));
            return;
        }

        if (clazz == Long.class) {
            setParameter(parameterIndex, createParameter((Long) x));
            return;
        }

        if (clazz == String.class) {
            setParameter(parameterIndex, createParameter((String) x));
            return;
        }

        if (clazz == BigDecimal.class) {
            setParameter(parameterIndex, createParameter((BigDecimal) x));
            return;
        }

        if (clazz == Float.class) {
            setParameter(parameterIndex, new JdbcParameterImpl(Types.FLOAT, x));
            return;
        }

        if (clazz == Double.class) {
            setParameter(parameterIndex, new JdbcParameterImpl(Types.DOUBLE, x));
            return;
        }

        if (clazz == java.sql.Date.class || clazz == java.util.Date.class) {
            setParameter(parameterIndex, createParameter((java.util.Date) x));
            return;
        }

        if (clazz == java.sql.Timestamp.class) {
            setParameter(parameterIndex, createParameter((java.sql.Timestamp) x));
            return;
        }

        if (clazz == java.sql.Time.class) {
            setParameter(parameterIndex, new JdbcParameterImpl(Types.TIME, x));
            return;
        }

        if (clazz == Boolean.class) {
            setParameter(parameterIndex, new JdbcParameterImpl(Types.BOOLEAN, x));
            return;
        }

        if (clazz == byte[].class) {
            setParameter(parameterIndex, new JdbcParameterImpl(TYPE.BYTES, x));
            return;
        }

        if (x instanceof InputStream) {
            setParameter(parameterIndex, new JdbcParameterImpl(JdbcParameter.TYPE.BinaryInputStream, x));
            return;
        }

        if (x instanceof Reader) {
            setParameter(parameterIndex, new JdbcParameterImpl(JdbcParameter.TYPE.CharacterInputStream, x));
            return;
        }

        if (x instanceof NClob) {
            setParameter(parameterIndex, new JdbcParameterImpl(Types.NCLOB, x));
            return;
        }

        if (x instanceof Clob) {
            setParameter(parameterIndex, new JdbcParameterImpl(Types.CLOB, x));
            return;
        }

        if (x instanceof Blob) {
            setParameter(parameterIndex, new JdbcParameterImpl(Types.BLOB, x));
            return;
        }

        String className = x.getClass().getName();

        if (className.equals("java.time.LocalTime")) {
            setParameter(parameterIndex, new JdbcParameterImpl(Types.TIME, x));
            return;
        }

        if (className.equals("java.time.LocalDate")) {
            setParameter(parameterIndex, new JdbcParameterImpl(Types.DATE, x));
            return;
        }

        if (className.equals("java.time.LocalDateTime")) {
            setParameter(parameterIndex, new JdbcParameterImpl(Types.TIMESTAMP, x));
            return;
        }

        if (className.equals("java.time.ZonedDateTime")) {
            setParameter(parameterIndex, new JdbcParameterImpl(Types.TIMESTAMP, x));
            return;
        }

        setParameter(parameterIndex, createParameter(Types.OTHER, null));
    }

    @Override
    public void setObject(int parameterIndex, Object x, int targetSqlType) throws SQLException {
        setParameter(parameterIndex, createParameter(targetSqlType, x));

        FilterChainImpl chain = createChain();
        chain.preparedStatement_setObject(this, parameterIndex, x, targetSqlType);
        recycleFilterChain(chain);
    }

    @Override
    public void setObject(int parameterIndex, Object x, int targetSqlType, int scaleOrLength) throws SQLException {
        setParameter(parameterIndex, createParameter(x, targetSqlType, scaleOrLength));

        FilterChainImpl chain = createChain();
        chain.preparedStatement_setObject(this, parameterIndex, x, targetSqlType, scaleOrLength);
        recycleFilterChain(chain);
    }

    @Override
    public void setRef(int parameterIndex, Ref x) throws SQLException {
        setParameter(parameterIndex, createParameter(Types.REF, x));

        FilterChainImpl chain = createChain();
        chain.preparedStatement_setRef(this, parameterIndex, x);
        recycleFilterChain(chain);
    }

    @Override
    public void setRowId(int parameterIndex, RowId x) throws SQLException {
        setParameter(parameterIndex, createParameter(Types.ROWID, x));

        FilterChainImpl chain = createChain();
        chain.preparedStatement_setRowId(this, parameterIndex, x);
        recycleFilterChain(chain);
    }

    @Override
    public void setSQLXML(int parameterIndex, SQLXML x) throws SQLException {
        setParameter(parameterIndex, createParameter(Types.SQLXML, x));

        FilterChainImpl chain = createChain();
        chain.preparedStatement_setSQLXML(this, parameterIndex, x);
        recycleFilterChain(chain);
    }

    @Override
    public void setShort(int parameterIndex, short x) throws SQLException {
        setParameter(parameterIndex, createParameter(Types.SMALLINT, x));

        FilterChainImpl chain = createChain();
        chain.preparedStatement_setShort(this, parameterIndex, x);
        recycleFilterChain(chain);
    }

    @Override
    public void setString(int parameterIndex, String x) throws SQLException {
        setParameter(
                parameterIndex,
                createParameter(x)
        );

        FilterChainImpl chain = createChain();
        chain.preparedStatement_setString(this, parameterIndex, x);
        recycleFilterChain(chain);
    }

    @Override
    public void setTime(int parameterIndex, Time x) throws SQLException {
        setParameter(parameterIndex, createParameter(Types.TIME, x));

        FilterChainImpl chain = createChain();
        chain.preparedStatement_setTime(this, parameterIndex, x);
        recycleFilterChain(chain);
    }

    @Override
    public void setTime(int parameterIndex, Time x, Calendar cal) throws SQLException {
        setParameter(parameterIndex, createParameter(Types.TIME, x, cal));

        FilterChainImpl chain = createChain();
        chain.preparedStatement_setTime(this, parameterIndex, x, cal);
        recycleFilterChain(chain);
    }

    @Override
    public void setTimestamp(int parameterIndex, Timestamp x) throws SQLException {
        setParameter(parameterIndex, createParameter(x));

        FilterChainImpl chain = createChain();
        chain.preparedStatement_setTimestamp(this, parameterIndex, x);
        recycleFilterChain(chain);
    }

    @Override
    public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal) throws SQLException {
        setParameter(parameterIndex, createParameter(Types.TIMESTAMP, x));

        FilterChainImpl chain = createChain();
        chain.preparedStatement_setTimestamp(this, parameterIndex, x, cal);
        recycleFilterChain(chain);
    }

    @Override
    public void setURL(int parameterIndex, URL x) throws SQLException {
        setParameter(parameterIndex, createParameter(TYPE.URL, x));

        FilterChainImpl chain = createChain();
        chain.preparedStatement_setURL(this, parameterIndex, x);
        recycleFilterChain(chain);
    }

    @Override
    public void setUnicodeStream(int parameterIndex, InputStream x, int length) throws SQLException {
        setParameter(parameterIndex, createParameter(TYPE.UnicodeStream, x, length));

        FilterChainImpl chain = createChain();
        chain.preparedStatement_setUnicodeStream(this, parameterIndex, x, length);
        recycleFilterChain(chain);
    }

    @Override
    public String getLastExecuteSql() {
        return this.sql;
    }

    @SuppressWarnings("unchecked")
    public <T> T unwrap(Class<T> iface) throws SQLException {
        if (iface == PreparedStatementProxy.class) {
            return (T) this;
        }

        return super.unwrap(iface);
    }

    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        if (iface == PreparedStatementProxy.class) {
            return true;
        }

        return super.isWrapperFor(iface);
    }

    private JdbcParameter createParemeter(int x) {
        return JdbcParameterInt.valueOf(x);
    }

    private JdbcParameter createParameter(long x) {
        return JdbcParameterLong.valueOf(x);
    }

    private JdbcParameter createParameterNull(int sqlType) {
        return JdbcParameterNull.valueOf(sqlType);

    }

    private JdbcParameter createParameter(java.util.Date x) {
        if (x == null) {
            return JdbcParameterNull.DATE;
        }

        return new JdbcParameterDate(x);
    }

    private JdbcParameter createParameter(BigDecimal x) {
        if (x == null) {
            return JdbcParameterNull.DECIMAL;
        }

        return JdbcParameterDecimal.valueOf(x);
    }

    private JdbcParameter createParameter(String x) {
        if (x == null) {
            return JdbcParameterNull.VARCHAR;
        }

        if (x.length() == 0) {
            return JdbcParameterString.empty;
        }

        return new JdbcParameterString(x);
    }

    private JdbcParameter createParameter(Timestamp x) {
        if (x == null) {
            return JdbcParameterNull.TIMESTAMP;
        }

        return new JdbcParameterTimestamp(x);
    }

    private JdbcParameter createParameter(Object x, int sqlType, int scaleOrLength) {
        if (x == null) {
            return JdbcParameterNull.valueOf(sqlType);
        }

        return new JdbcParameterImpl(sqlType, x, -1, null, scaleOrLength);
    }

    private JdbcParameter createParameter(int sqlType, Object value, long length) {
        if (value == null) {
            return JdbcParameterNull.valueOf(sqlType);
        }

        return new JdbcParameterImpl(sqlType, value, length);
    }

    private JdbcParameter createParameter(int sqlType, Object value) {
        if (value == null) {
            return JdbcParameterNull.valueOf(sqlType);
        }

        return new JdbcParameterImpl(sqlType, value);
    }

    public JdbcParameter createParameter(int sqlType, Object value, Calendar calendar) {
        if (value == null) {
            return JdbcParameterNull.valueOf(sqlType);
        }

        return new JdbcParameterImpl(sqlType, value, calendar);
    }
}
