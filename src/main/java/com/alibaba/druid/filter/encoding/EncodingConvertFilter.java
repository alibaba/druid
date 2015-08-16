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
package com.alibaba.druid.filter.encoding;

import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Properties;

import com.alibaba.druid.filter.FilterAdapter;
import com.alibaba.druid.filter.FilterChain;
import com.alibaba.druid.proxy.jdbc.CallableStatementProxy;
import com.alibaba.druid.proxy.jdbc.ClobProxy;
import com.alibaba.druid.proxy.jdbc.ConnectionProxy;
import com.alibaba.druid.proxy.jdbc.PreparedStatementProxy;
import com.alibaba.druid.proxy.jdbc.ResultSetProxy;
import com.alibaba.druid.proxy.jdbc.StatementProxy;
import com.alibaba.druid.util.Utils;

/**
 * @author wenshao<szujobs@hotmail.com>
 */
public class EncodingConvertFilter extends FilterAdapter {

    public final static String ATTR_CHARSET_PARAMETER = "ali.charset.param";
    public final static String ATTR_CHARSET_CONVERTER = "ali.charset.converter";
    private String             clientEncoding;
    private String             serverEncoding;

    public ConnectionProxy connection_connect(FilterChain chain, Properties info) throws SQLException {
        ConnectionProxy conn = chain.connection_connect(info);

        CharsetParameter param = new CharsetParameter();
        param.setClientEncoding(info.getProperty(CharsetParameter.CLIENTENCODINGKEY));
        param.setServerEncoding(info.getProperty(CharsetParameter.SERVERENCODINGKEY));

        if (param.getClientEncoding() == null || "".equalsIgnoreCase(param.getClientEncoding())) {
            param.setClientEncoding(clientEncoding);
        }
        if (param.getServerEncoding() == null || "".equalsIgnoreCase(param.getServerEncoding())) {
            param.setServerEncoding(serverEncoding);
        }
        conn.putAttribute(ATTR_CHARSET_PARAMETER, param);
        conn.putAttribute(ATTR_CHARSET_CONVERTER,
                                 new CharsetConvert(param.getClientEncoding(), param.getServerEncoding()));

        return conn;
    }

    @Override
    public String resultSet_getString(FilterChain chain, ResultSetProxy result, int columnIndex) throws SQLException {
        String value = super.resultSet_getString(chain, result, columnIndex);
        return decode(result.getStatementProxy().getConnectionProxy(), value);
    }

    @Override
    public String resultSet_getString(FilterChain chain, ResultSetProxy result, String columnLabel) throws SQLException {
        String value = super.resultSet_getString(chain, result, columnLabel);
        return decode(result.getStatementProxy().getConnectionProxy(), value);
    }

    @Override
    public Object resultSet_getObject(FilterChain chain, ResultSetProxy result, int columnIndex) throws SQLException {
        ResultSet rawResultSet = result.getResultSetRaw();
        ResultSetMetaData metadata = rawResultSet.getMetaData();
        int columnType = metadata.getColumnType(columnIndex);

        Object value = null;
        switch (columnType) {
            case Types.CHAR:
                value = super.resultSet_getString(chain, result, columnIndex);
                break;
            case Types.CLOB:
                value = super.resultSet_getString(chain, result, columnIndex);
                break;
            case Types.LONGVARCHAR:
                value = super.resultSet_getString(chain, result, columnIndex);
                break;
            case Types.VARCHAR:
                value = super.resultSet_getString(chain, result, columnIndex);
                break;
            default:
                value = super.resultSet_getObject(chain, result, columnIndex);
        }

        return decodeObject(result.getStatementProxy().getConnectionProxy(), value);
    }

    @Override
    public Object resultSet_getObject(FilterChain chain, ResultSetProxy result, int columnIndex,
                                      java.util.Map<String, Class<?>> map) throws SQLException {
        ResultSet rawResultSet = result.getResultSetRaw();
        ResultSetMetaData metadata = rawResultSet.getMetaData();
        int columnType = metadata.getColumnType(columnIndex);

        Object value = null;
        switch (columnType) {
            case Types.CHAR:
                value = super.resultSet_getString(chain, result, columnIndex);
                break;
            case Types.CLOB:
                value = super.resultSet_getString(chain, result, columnIndex);
                break;
            case Types.LONGVARCHAR:
                value = super.resultSet_getString(chain, result, columnIndex);
                break;
            case Types.VARCHAR:
                value = super.resultSet_getString(chain, result, columnIndex);
                break;
            default:
                value = super.resultSet_getObject(chain, result, columnIndex, map);
        }

        return decodeObject(result.getStatementProxy().getConnectionProxy(), value);
    }

    @Override
    public Object resultSet_getObject(FilterChain chain, ResultSetProxy result, String columnLabel) throws SQLException {
        ResultSet rawResultSet = result.getResultSetRaw();
        ResultSetMetaData metadata = rawResultSet.getMetaData();
        int columnIndex = rawResultSet.findColumn(columnLabel);
        int columnType = metadata.getColumnType(columnIndex);

        Object value = null;
        switch (columnType) {
            case Types.CHAR:
                value = super.resultSet_getString(chain, result, columnLabel);
                break;
            case Types.CLOB:
                value = super.resultSet_getString(chain, result, columnLabel);
                break;
            case Types.LONGVARCHAR:
                value = super.resultSet_getString(chain, result, columnLabel);
                break;
            case Types.VARCHAR:
                value = super.resultSet_getString(chain, result, columnLabel);
                break;
            default:
                value = super.resultSet_getObject(chain, result, columnLabel);
        }
        return decodeObject(result.getStatementProxy().getConnectionProxy(), value);
    }

    @Override
    public Object resultSet_getObject(FilterChain chain, ResultSetProxy result, String columnLabel,
                                      java.util.Map<String, Class<?>> map) throws SQLException {
        ResultSet rawResultSet = result.getResultSetRaw();
        ResultSetMetaData metadata = rawResultSet.getMetaData();
        int columnIndex = rawResultSet.findColumn(columnLabel);
        int columnType = metadata.getColumnType(columnIndex);

        Object value = null;
        switch (columnType) {
            case Types.CHAR:
                value = super.resultSet_getString(chain, result, columnLabel);
                break;
            case Types.CLOB:
                value = super.resultSet_getString(chain, result, columnLabel);
                break;
            case Types.LONGVARCHAR:
                value = super.resultSet_getString(chain, result, columnLabel);
                break;
            case Types.VARCHAR:
                value = super.resultSet_getString(chain, result, columnLabel);
                break;
            default:
                value = super.resultSet_getObject(chain, result, columnLabel, map);
        }

        return decodeObject(result.getStatementProxy().getConnectionProxy(), value);
    }

    // ///////////

    public Object decodeObject(ConnectionProxy connection, Object object) throws SQLException {
        if (object instanceof String) {
            return decode(connection, (String) object);
        }

        if (object instanceof Reader) {
            Reader reader = (Reader) object;
            String text = Utils.read(reader);
            return new StringReader(decode(connection, text));
        }

        return object;
    }

    public Object decodeObject(CallableStatementProxy stmt, Object object) throws SQLException {
        if (object instanceof String) {
            return decode(stmt.getConnectionProxy(), (String) object);
        }

        if (object instanceof Reader) {
            Reader reader = (Reader) object;
            String text = Utils.read(reader);
            return new StringReader(decode(stmt.getConnectionProxy(), text));
        }

        return object;
    }

    public String encode(ConnectionProxy connection, String s) throws SQLException {
        try {
            CharsetConvert charsetConvert = (CharsetConvert) connection.getAttribute(ATTR_CHARSET_CONVERTER);

            return charsetConvert.encode(s);
        } catch (UnsupportedEncodingException e) {
            throw new SQLException(e.getMessage(), e);
        }
    }

    public String decode(ConnectionProxy connection, String s) throws SQLException {
        try {
            CharsetConvert charsetConvert = (CharsetConvert) connection.getAttribute(ATTR_CHARSET_CONVERTER);
            return charsetConvert.decode(s);
        } catch (UnsupportedEncodingException e) {
            throw new SQLException(e.getMessage(), e);
        }
    }

    // //////////////// Connection

    @Override
    public PreparedStatementProxy connection_prepareStatement(FilterChain chain, ConnectionProxy connection, String sql)
                                                                                                                        throws SQLException {
        return super.connection_prepareStatement(chain, connection, encode(connection, sql));
    }

    @Override
    public PreparedStatementProxy connection_prepareStatement(FilterChain chain, ConnectionProxy connection,
                                                              String sql, int autoGeneratedKeys) throws SQLException {
        return super.connection_prepareStatement(chain, connection, encode(connection, sql), autoGeneratedKeys);
    }

    @Override
    public PreparedStatementProxy connection_prepareStatement(FilterChain chain, ConnectionProxy connection,
                                                              String sql, int resultSetType, int resultSetConcurrency)
                                                                                                                      throws SQLException {
        return super.connection_prepareStatement(chain, connection, encode(connection, sql), resultSetType,
                                                 resultSetConcurrency);
    }

    @Override
    public PreparedStatementProxy connection_prepareStatement(FilterChain chain, ConnectionProxy connection,
                                                              String sql, int resultSetType, int resultSetConcurrency,
                                                              int resultSetHoldability) throws SQLException {
        return super.connection_prepareStatement(chain, connection, encode(connection, sql), resultSetType,
                                                 resultSetConcurrency, resultSetHoldability);
    }

    @Override
    public PreparedStatementProxy connection_prepareStatement(FilterChain chain, ConnectionProxy connection,
                                                              String sql, int[] columnIndexes) throws SQLException {
        return super.connection_prepareStatement(chain, connection, encode(connection, sql), columnIndexes);
    }

    @Override
    public PreparedStatementProxy connection_prepareStatement(FilterChain chain, ConnectionProxy connection,
                                                              String sql, String[] columnNames) throws SQLException {
        return super.connection_prepareStatement(chain, connection, encode(connection, sql), columnNames);
    }

    // / precall

    @Override
    public CallableStatementProxy connection_prepareCall(FilterChain chain, ConnectionProxy connection, String sql)
                                                                                                                   throws SQLException {
        return super.connection_prepareCall(chain, connection, encode(connection, sql));
    }

    @Override
    public CallableStatementProxy connection_prepareCall(FilterChain chain, ConnectionProxy connection, String sql,
                                                         int resultSetType, int resultSetConcurrency)
                                                                                                     throws SQLException {
        return super.connection_prepareCall(chain, connection, encode(connection, sql), resultSetType,
                                            resultSetConcurrency);
    }

    @Override
    public CallableStatementProxy connection_prepareCall(FilterChain chain, ConnectionProxy connection, String sql,
                                                         int resultSetType, int resultSetConcurrency,
                                                         int resultSetHoldability) throws SQLException {
        return super.connection_prepareCall(chain, connection, encode(connection, sql), resultSetType,
                                            resultSetConcurrency, resultSetHoldability);
    }

    // nativeSQL

    @Override
    public String connection_nativeSQL(FilterChain chain, ConnectionProxy connection, String sql) throws SQLException {
        String encodedSql = encode(connection, sql);
        return super.connection_nativeSQL(chain, connection, encodedSql);
    }

    // ////////////// statement

    @Override
    public void statement_addBatch(FilterChain chain, StatementProxy statement, String sql) throws SQLException {
        super.statement_addBatch(chain, statement, encode(statement.getConnectionProxy(), sql));
    }

    @Override
    public boolean statement_execute(FilterChain chain, StatementProxy statement, String sql) throws SQLException {
        return super.statement_execute(chain, statement, encode(statement.getConnectionProxy(), sql));
    }

    @Override
    public boolean statement_execute(FilterChain chain, StatementProxy statement, String sql, int autoGeneratedKeys)
                                                                                                                    throws SQLException {
        return super.statement_execute(chain, statement, encode(statement.getConnectionProxy(), sql), autoGeneratedKeys);
    }

    @Override
    public boolean statement_execute(FilterChain chain, StatementProxy statement, String sql, int columnIndexes[])
                                                                                                                  throws SQLException {
        return super.statement_execute(chain, statement, encode(statement.getConnectionProxy(), sql), columnIndexes);
    }

    @Override
    public boolean statement_execute(FilterChain chain, StatementProxy statement, String sql, String columnNames[])
                                                                                                                   throws SQLException {
        return super.statement_execute(chain, statement, encode(statement.getConnectionProxy(), sql), columnNames);
    }

    @Override
    public ResultSetProxy statement_executeQuery(FilterChain chain, StatementProxy statement, String sql)
                                                                                                         throws SQLException {
        return super.statement_executeQuery(chain, statement, encode(statement.getConnectionProxy(), sql));
    }

    @Override
    public int statement_executeUpdate(FilterChain chain, StatementProxy statement, String sql) throws SQLException {
        return super.statement_executeUpdate(chain, statement, encode(statement.getConnectionProxy(), sql));
    }

    @Override
    public int statement_executeUpdate(FilterChain chain, StatementProxy statement, String sql, int autoGeneratedKeys)
                                                                                                                      throws SQLException {
        return super.statement_executeUpdate(chain, statement, encode(statement.getConnectionProxy(), sql),
                                             autoGeneratedKeys);
    }

    @Override
    public int statement_executeUpdate(FilterChain chain, StatementProxy statement, String sql, int columnIndexes[])
                                                                                                                    throws SQLException {
        return super.statement_executeUpdate(chain, statement, encode(statement.getConnectionProxy(), sql),
                                             columnIndexes);
    }

    @Override
    public int statement_executeUpdate(FilterChain chain, StatementProxy statement, String sql, String columnNames[])
                                                                                                                     throws SQLException {
        return super.statement_executeUpdate(chain, statement, encode(statement.getConnectionProxy(), sql), columnNames);
    }

    // ========== preparedStatement

    @Override
    public void preparedStatement_setString(FilterChain chain, PreparedStatementProxy statement, int parameterIndex,
                                            String x) throws SQLException {
        super.preparedStatement_setString(chain, statement, parameterIndex, encode(statement.getConnectionProxy(), x));
    }

    @Override
    public void preparedStatement_setCharacterStream(FilterChain chain, PreparedStatementProxy statement,
                                                     int parameterIndex, java.io.Reader reader) throws SQLException {
        String text = Utils.read(reader);
        String encodedText = encode(statement.getConnectionProxy(), text);
        super.preparedStatement_setCharacterStream(chain, statement, parameterIndex, new StringReader(encodedText));
    }

    @Override
    public void preparedStatement_setCharacterStream(FilterChain chain, PreparedStatementProxy statement,
                                                     int parameterIndex, java.io.Reader reader, int length)
                                                                                                           throws SQLException {
        String text = Utils.read(reader, length);
        String encodedText = encode(statement.getConnectionProxy(), text);
        super.preparedStatement_setCharacterStream(chain, statement, parameterIndex, new StringReader(encodedText),
                                                   encodedText.length());
    }

    @Override
    public void preparedStatement_setCharacterStream(FilterChain chain, PreparedStatementProxy statement,
                                                     int parameterIndex, java.io.Reader reader, long length)
                                                                                                            throws SQLException {
        String text = Utils.read(reader, (int) length);
        String encodedText = encode(statement.getConnectionProxy(), text);
        super.preparedStatement_setCharacterStream(chain, statement, parameterIndex, new StringReader(encodedText),
                                                   encodedText.length());
    }

    @Override
    public void preparedStatement_setObject(FilterChain chain, PreparedStatementProxy statement, int parameterIndex,
                                            Object x) throws SQLException {
        if (x instanceof String) {
            String encodedText = encode(statement.getConnectionProxy(), (String) x);
            super.preparedStatement_setObject(chain, statement, parameterIndex, encodedText);
        } else if (x instanceof Reader) {
            String text = Utils.read((Reader) x);
            String encodedText = encode(statement.getConnectionProxy(), text);
            super.preparedStatement_setObject(chain, statement, parameterIndex, new StringReader(encodedText));
        } else {
            super.preparedStatement_setObject(chain, statement, parameterIndex, x);
        }
    }

    @Override
    public void preparedStatement_setObject(FilterChain chain, PreparedStatementProxy statement, int parameterIndex,
                                            Object x, int targetSqlType) throws SQLException {
        if (x instanceof String) {
            String encodedText = encode(statement.getConnectionProxy(), (String) x);
            super.preparedStatement_setObject(chain, statement, parameterIndex, encodedText, targetSqlType);
        } else if (x instanceof Reader) {
            String text = Utils.read((Reader) x);
            String encodedText = encode(statement.getConnectionProxy(), text);
            super.preparedStatement_setObject(chain, statement, parameterIndex, new StringReader(encodedText),
                                              targetSqlType);
        } else {
            super.preparedStatement_setObject(chain, statement, parameterIndex, x, targetSqlType);
        }
    }

    @Override
    public void preparedStatement_setObject(FilterChain chain, PreparedStatementProxy statement, int parameterIndex,
                                            Object x, int targetSqlType, int scaleOrLength) throws SQLException {
        if (x instanceof String) {
            String encodedText = encode(statement.getConnectionProxy(), (String) x);
            super.preparedStatement_setObject(chain, statement, parameterIndex, encodedText, targetSqlType,
                                              scaleOrLength);
        } else if (x instanceof Reader) {
            String text = Utils.read((Reader) x);
            String encodedText = encode(statement.getConnectionProxy(), text);
            super.preparedStatement_setObject(chain, statement, parameterIndex, new StringReader(encodedText),
                                              targetSqlType, scaleOrLength);
        } else {
            super.preparedStatement_setObject(chain, statement, parameterIndex, x, targetSqlType, scaleOrLength);
        }
    }

    // //////////

    @Override
    public long clob_position(FilterChain chain, ClobProxy wrapper, String searchstr, long start) throws SQLException {
        return chain.clob_position(wrapper, encode(wrapper.getConnectionWrapper(), searchstr), start);
    }

    @Override
    public String clob_getSubString(FilterChain chain, ClobProxy wrapper, long pos, int length) throws SQLException {
        String text = super.clob_getSubString(chain, wrapper, pos, length);
        return decode(wrapper.getConnectionWrapper(), text);
    }

    @Override
    public java.io.Reader clob_getCharacterStream(FilterChain chain, ClobProxy wrapper) throws SQLException {
        Reader reader = super.clob_getCharacterStream(chain, wrapper);
        String text = Utils.read(reader);
        return new StringReader(decode(wrapper.getConnectionWrapper(), text));
    }

    @Override
    public Reader clob_getCharacterStream(FilterChain chain, ClobProxy wrapper, long pos, long length)
                                                                                                      throws SQLException {
        Reader reader = super.clob_getCharacterStream(chain, wrapper, pos, length);
        String text = Utils.read(reader);
        return new StringReader(decode(wrapper.getConnectionWrapper(), text));
    }

    @Override
    public int clob_setString(FilterChain chain, ClobProxy wrapper, long pos, String str) throws SQLException {
        return chain.clob_setString(wrapper, pos, encode(wrapper.getConnectionWrapper(), str));
    }

    @Override
    public int clob_setString(FilterChain chain, ClobProxy wrapper, long pos, String str, int offset, int len)
                                                                                                              throws SQLException {
        return chain.clob_setString(wrapper, pos, encode(wrapper.getConnectionWrapper(), str), offset, len);
    }

    // ///////////// callableStatement_

    @Override
    public void callableStatement_setCharacterStream(FilterChain chain, CallableStatementProxy statement,
                                                     String parameterName, java.io.Reader reader) throws SQLException {
        String text = Utils.read(reader);
        Reader encodeReader = new StringReader(encode(statement.getConnectionProxy(), text));
        super.callableStatement_setCharacterStream(chain, statement, parameterName, encodeReader);
    }

    @Override
    public void callableStatement_setCharacterStream(FilterChain chain, CallableStatementProxy statement,
                                                     String parameterName, java.io.Reader reader, int length)
                                                                                                             throws SQLException {
        String text = Utils.read(reader, length);
        String encodeText = encode(statement.getConnectionProxy(), text);
        Reader encodeReader = new StringReader(encodeText);
        super.callableStatement_setCharacterStream(chain, statement, parameterName, encodeReader, encodeText.length());
    }

    @Override
    public void callableStatement_setCharacterStream(FilterChain chain, CallableStatementProxy statement,
                                                     String parameterName, java.io.Reader reader, long length)
                                                                                                              throws SQLException {
        String text = Utils.read(reader, (int) length);
        String encodeText = encode(statement.getConnectionProxy(), text);
        Reader encodeReader = new StringReader(encodeText);
        super.callableStatement_setCharacterStream(chain, statement, parameterName, encodeReader,
                                                   (long) encodeText.length());
    }

    @Override
    public void callableStatement_setString(FilterChain chain, CallableStatementProxy statement, String parameterName,
                                            String x) throws SQLException {
        super.callableStatement_setString(chain, statement, parameterName, encode(statement.getConnectionProxy(), x));
    }

    @Override
    public void callableStatement_setObject(FilterChain chain, CallableStatementProxy statement, String parameterName,
                                            Object x) throws SQLException {
        if (x instanceof String) {
            String encodedText = encode(statement.getConnectionProxy(), (String) x);
            super.callableStatement_setObject(chain, statement, parameterName, encodedText);
        } else if (x instanceof Reader) {
            String text = Utils.read((Reader) x);
            String encodedText = encode(statement.getConnectionProxy(), text);
            super.callableStatement_setObject(chain, statement, parameterName, new StringReader(encodedText));
        } else {
            super.callableStatement_setObject(chain, statement, parameterName, x);
        }
    }

    @Override
    public void callableStatement_setObject(FilterChain chain, CallableStatementProxy statement, String parameterName,
                                            Object x, int targetSqlType) throws SQLException {
        if (x instanceof String) {
            String encodedText = encode(statement.getConnectionProxy(), (String) x);
            super.callableStatement_setObject(chain, statement, parameterName, encodedText, targetSqlType);
        } else if (x instanceof Reader) {
            String text = Utils.read((Reader) x);
            String encodedText = encode(statement.getConnectionProxy(), text);
            super.callableStatement_setObject(chain, statement, parameterName, new StringReader(encodedText),
                                              targetSqlType);
        } else {
            super.callableStatement_setObject(chain, statement, parameterName, x, targetSqlType);
        }
    }

    @Override
    public void callableStatement_setObject(FilterChain chain, CallableStatementProxy statement, String parameterName,
                                            Object x, int targetSqlType, int scale) throws SQLException {
        if (x instanceof String) {
            String encodedText = encode(statement.getConnectionProxy(), (String) x);
            super.callableStatement_setObject(chain, statement, parameterName, encodedText, targetSqlType, scale);
        } else if (x instanceof Reader) {
            String text = Utils.read((Reader) x);
            String encodedText = encode(statement.getConnectionProxy(), text);
            super.callableStatement_setObject(chain, statement, parameterName, new StringReader(encodedText),
                                              targetSqlType, scale);
        } else {
            super.callableStatement_setObject(chain, statement, parameterName, x, targetSqlType, scale);
        }
    }

    @Override
    public String callableStatement_getString(FilterChain chain, CallableStatementProxy statement, int parameterIndex)
                                                                                                                      throws SQLException {
        String value = super.callableStatement_getString(chain, statement, parameterIndex);
        return decode(statement.getConnectionProxy(), value);
    }

    @Override
    public String callableStatement_getString(FilterChain chain, CallableStatementProxy statement, String parameterName)
                                                                                                                        throws SQLException {
        String value = super.callableStatement_getString(chain, statement, parameterName);
        return decode(statement.getConnectionProxy(), value);
    }

    @Override
    public Object callableStatement_getObject(FilterChain chain, CallableStatementProxy statement, int parameterIndex)
                                                                                                                      throws SQLException {
        Object value = chain.callableStatement_getObject(statement, parameterIndex);
        return decodeObject(statement, value);
    }

    @Override
    public Object callableStatement_getObject(FilterChain chain, CallableStatementProxy statement, int parameterIndex,
                                              java.util.Map<String, Class<?>> map) throws SQLException {
        Object value = chain.callableStatement_getObject(statement, parameterIndex, map);
        return decodeObject(statement, value);
    }

    @Override
    public Object callableStatement_getObject(FilterChain chain, CallableStatementProxy statement, String parameterName)
                                                                                                                        throws SQLException {
        Object value = chain.callableStatement_getObject(statement, parameterName);
        return decodeObject(statement, value);
    }

    @Override
    public Object callableStatement_getObject(FilterChain chain, CallableStatementProxy statement,
                                              String parameterName, java.util.Map<String, Class<?>> map)
                                                                                                        throws SQLException {
        Object value = chain.callableStatement_getObject(statement, parameterName, map);
        return decodeObject(statement, value);
    }

}
