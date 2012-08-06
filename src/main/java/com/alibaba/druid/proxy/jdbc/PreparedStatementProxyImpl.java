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

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Calendar;
import java.util.Map;
import java.util.TreeMap;

import com.alibaba.druid.proxy.jdbc.JdbcParameter.TYPE;
import com.alibaba.druid.stat.JdbcSqlStat;

/**
 * @author wenshao<szujobs@hotmail.com>
 */
public class PreparedStatementProxyImpl extends StatementProxyImpl implements PreparedStatementProxy {

    protected final PreparedStatement           statement;
    protected final String                      sql;
    protected final Map<Integer, JdbcParameter> parameters = new TreeMap<Integer, JdbcParameter>();

    public PreparedStatementProxyImpl(ConnectionProxy connection, PreparedStatement statement, String sql, long id){
        super(connection, statement, id);
        this.statement = statement;
        this.sql = sql;
    }

    public Map<Integer, JdbcParameter> getParameters() {
        return parameters;
    }

    public String getSql() {
        return this.sql;
    }

    public PreparedStatement getRawObject() {
        return this.statement;
    }

    @Override
    public void addBatch() throws SQLException {
        createChain().preparedStatement_addBatch(this);
    }

    @Override
    public void clearParameters() throws SQLException {
        createChain().preparedStatement_clearParameters(this);
    }

    @Override
    public String getBatchSql() {
        return this.sql;
    }

    @Override
    public boolean execute() throws SQLException {
        updateCount = null;
        lastExecuteType = StatementExecuteType.Execute;
        firstResultSet = createChain().preparedStatement_execute(this);
        return firstResultSet;
    }

    @Override
    public ResultSet executeQuery() throws SQLException {
        updateCount = null;
        lastExecuteType = StatementExecuteType.ExecuteQuery;
        return createChain().preparedStatement_executeQuery(this);
    }

    @Override
    public int executeUpdate() throws SQLException {
        lastExecuteType = StatementExecuteType.ExecuteUpdate;
        updateCount = createChain().preparedStatement_executeUpdate(this);
        return updateCount;
    }

    @Override
    public ResultSetMetaData getMetaData() throws SQLException {
        return createChain().preparedStatement_getMetaData(this);
    }

    @Override
    public ParameterMetaData getParameterMetaData() throws SQLException {
        return createChain().preparedStatement_getParameterMetaData(this);
    }

    @Override
    public void setArray(int parameterIndex, Array x) throws SQLException {
        setParameter(parameterIndex, new JdbcParameter(Types.ARRAY, x));

        createChain().preparedStatement_setArray(this, parameterIndex, x);
    }

    @Override
    public void setAsciiStream(int parameterIndex, InputStream x) throws SQLException {
        setParameter(parameterIndex, new JdbcParameter(JdbcParameter.TYPE.AsciiInputStream, x));

        createChain().preparedStatement_setAsciiStream(this, parameterIndex, x);
    }

    @Override
    public void setAsciiStream(int parameterIndex, InputStream x, int length) throws SQLException {
        setParameter(parameterIndex, new JdbcParameter(JdbcParameter.TYPE.AsciiInputStream, x, length));

        createChain().preparedStatement_setAsciiStream(this, parameterIndex, x, length);
    }

    @Override
    public void setAsciiStream(int parameterIndex, InputStream x, long length) throws SQLException {
        setParameter(parameterIndex, new JdbcParameter(JdbcParameter.TYPE.AsciiInputStream, x, length));

        createChain().preparedStatement_setAsciiStream(this, parameterIndex, x, length);
    }

    @Override
    public void setBigDecimal(int parameterIndex, BigDecimal x) throws SQLException {
        setParameter(parameterIndex, new JdbcParameter(Types.DECIMAL, x));
        createChain().preparedStatement_setBigDecimal(this, parameterIndex, x);
    }

    @Override
    public void setBinaryStream(int parameterIndex, InputStream x) throws SQLException {
        setParameter(parameterIndex, new JdbcParameter(JdbcParameter.TYPE.BinaryInputStream, x));

        createChain().preparedStatement_setBinaryStream(this, parameterIndex, x);
    }

    @Override
    public void setBinaryStream(int parameterIndex, InputStream x, int length) throws SQLException {
        setParameter(parameterIndex, new JdbcParameter(JdbcParameter.TYPE.BinaryInputStream, x, length));

        createChain().preparedStatement_setBinaryStream(this, parameterIndex, x, length);
    }

    @Override
    public void setBinaryStream(int parameterIndex, InputStream x, long length) throws SQLException {
        setParameter(parameterIndex, new JdbcParameter(JdbcParameter.TYPE.BinaryInputStream, x, length));

        createChain().preparedStatement_setBinaryStream(this, parameterIndex, x, length);
    }

    @Override
    public void setBlob(int parameterIndex, Blob x) throws SQLException {
        setParameter(parameterIndex, new JdbcParameter(Types.BLOB, x));

        createChain().preparedStatement_setBlob(this, parameterIndex, x);
    }

    @Override
    public void setBlob(int parameterIndex, InputStream x) throws SQLException {
        setParameter(parameterIndex, new JdbcParameter(Types.BLOB, x));

        createChain().preparedStatement_setBlob(this, parameterIndex, x);
    }

    @Override
    public void setBlob(int parameterIndex, InputStream x, long length) throws SQLException {
        setParameter(parameterIndex, new JdbcParameter(Types.BLOB, x, length));
        createChain().preparedStatement_setBlob(this, parameterIndex, x, length);
    }

    @Override
    public void setBoolean(int parameterIndex, boolean x) throws SQLException {
        setParameter(parameterIndex, new JdbcParameter(Types.BOOLEAN, x));
        createChain().preparedStatement_setBoolean(this, parameterIndex, x);
    }

    @Override
    public void setByte(int parameterIndex, byte x) throws SQLException {
        setParameter(parameterIndex, new JdbcParameter(Types.TINYINT, x));

        createChain().preparedStatement_setByte(this, parameterIndex, x);
    }

    @Override
    public void setBytes(int parameterIndex, byte[] x) throws SQLException {
        setParameter(parameterIndex, new JdbcParameter(TYPE.BYTES, x));

        createChain().preparedStatement_setBytes(this, parameterIndex, x);
    }

    @Override
    public void setCharacterStream(int parameterIndex, Reader x) throws SQLException {
        setParameter(parameterIndex, new JdbcParameter(TYPE.CharacterInputStream, x));

        createChain().preparedStatement_setCharacterStream(this, parameterIndex, x);
    }

    @Override
    public void setCharacterStream(int parameterIndex, Reader x, int length) throws SQLException {
        setParameter(parameterIndex, new JdbcParameter(TYPE.CharacterInputStream, x, length));

        createChain().preparedStatement_setCharacterStream(this, parameterIndex, x, length);
    }

    @Override
    public void setCharacterStream(int parameterIndex, Reader x, long length) throws SQLException {
        setParameter(parameterIndex, new JdbcParameter(TYPE.CharacterInputStream, x, length));

        createChain().preparedStatement_setCharacterStream(this, parameterIndex, x, length);
    }

    @Override
    public void setClob(int parameterIndex, Clob x) throws SQLException {
        setParameter(parameterIndex, new JdbcParameter(Types.CLOB, x));

        createChain().preparedStatement_setClob(this, parameterIndex, x);
    }

    @Override
    public void setClob(int parameterIndex, Reader x) throws SQLException {
        setParameter(parameterIndex, new JdbcParameter(Types.CLOB, x));

        createChain().preparedStatement_setClob(this, parameterIndex, x);
    }

    @Override
    public void setClob(int parameterIndex, Reader x, long length) throws SQLException {
        setParameter(parameterIndex, new JdbcParameter(Types.CLOB, x, length));

        createChain().preparedStatement_setClob(this, parameterIndex, x, length);
    }

    @Override
    public void setDate(int parameterIndex, Date x) throws SQLException {
        setParameter(parameterIndex, new JdbcParameter(Types.DATE, x));

        createChain().preparedStatement_setDate(this, parameterIndex, x);
    }

    @Override
    public void setDate(int parameterIndex, Date x, Calendar cal) throws SQLException {
        setParameter(parameterIndex, new JdbcParameter(Types.DATE, x, cal));

        createChain().preparedStatement_setDate(this, parameterIndex, x, cal);
    }

    @Override
    public void setDouble(int parameterIndex, double x) throws SQLException {
        setParameter(parameterIndex, new JdbcParameter(Types.DOUBLE, x));

        createChain().preparedStatement_setDouble(this, parameterIndex, x);
    }

    @Override
    public void setFloat(int parameterIndex, float x) throws SQLException {
        setParameter(parameterIndex, new JdbcParameter(Types.FLOAT, x));

        createChain().preparedStatement_setFloat(this, parameterIndex, x);
    }

    @Override
    public void setInt(int parameterIndex, int x) throws SQLException {
        setParameter(parameterIndex, new JdbcParameter(Types.INTEGER, x));

        createChain().preparedStatement_setInt(this, parameterIndex, x);
    }

    @Override
    public void setLong(int parameterIndex, long x) throws SQLException {
        setParameter(parameterIndex, new JdbcParameter(Types.BIGINT, x));

        createChain().preparedStatement_setLong(this, parameterIndex, x);
    }

    @Override
    public void setNCharacterStream(int parameterIndex, Reader x) throws SQLException {
        setParameter(parameterIndex, new JdbcParameter(TYPE.NCharacterInputStream, x));

        createChain().preparedStatement_setNCharacterStream(this, parameterIndex, x);
    }

    @Override
    public void setNCharacterStream(int parameterIndex, Reader x, long length) throws SQLException {
        setParameter(parameterIndex, new JdbcParameter(TYPE.NCharacterInputStream, x, length));

        createChain().preparedStatement_setNCharacterStream(this, parameterIndex, x, length);
    }

    @Override
    public void setNClob(int parameterIndex, NClob x) throws SQLException {
        setParameter(parameterIndex, new JdbcParameter(Types.NCLOB, x));

        createChain().preparedStatement_setNClob(this, parameterIndex, x);
    }

    @Override
    public void setNClob(int parameterIndex, Reader x) throws SQLException {
        setParameter(parameterIndex, new JdbcParameter(Types.NCLOB, x));

        createChain().preparedStatement_setNClob(this, parameterIndex, x);
    }

    @Override
    public void setNClob(int parameterIndex, Reader x, long length) throws SQLException {
        setParameter(parameterIndex, new JdbcParameter(Types.NCLOB, x, length));

        createChain().preparedStatement_setNClob(this, parameterIndex, x, length);
    }

    @Override
    public void setNString(int parameterIndex, String x) throws SQLException {
        setParameter(parameterIndex, new JdbcParameter(Types.NVARCHAR, x));

        createChain().preparedStatement_setNString(this, parameterIndex, x);
    }

    @Override
    public void setNull(int parameterIndex, int sqlType) throws SQLException {
        setParameter(parameterIndex, new JdbcParameter(sqlType, null));

        createChain().preparedStatement_setNull(this, parameterIndex, sqlType);
    }

    @Override
    public void setNull(int parameterIndex, int sqlType, String typeName) throws SQLException {
        setParameter(parameterIndex, new JdbcParameter(sqlType, null));

        createChain().preparedStatement_setNull(this, parameterIndex, sqlType, typeName);
    }

    @Override
    public void setObject(int parameterIndex, Object x) throws SQLException {
        setParameter(parameterIndex, new JdbcParameter(Types.OTHER, null));

        createChain().preparedStatement_setObject(this, parameterIndex, x);
    }

    @Override
    public void setObject(int parameterIndex, Object x, int targetSqlType) throws SQLException {
        setParameter(parameterIndex, new JdbcParameter(targetSqlType, x));

        createChain().preparedStatement_setObject(this, parameterIndex, x, targetSqlType);
    }

    @Override
    public void setObject(int parameterIndex, Object x, int targetSqlType, int scaleOrLength) throws SQLException {
        setParameter(parameterIndex, new JdbcParameter(targetSqlType, x));

        createChain().preparedStatement_setObject(this, parameterIndex, x, targetSqlType, scaleOrLength);
    }

    @Override
    public void setRef(int parameterIndex, Ref x) throws SQLException {
        setParameter(parameterIndex, new JdbcParameter(Types.REF, x));

        createChain().preparedStatement_setRef(this, parameterIndex, x);
    }

    @Override
    public void setRowId(int parameterIndex, RowId x) throws SQLException {
        setParameter(parameterIndex, new JdbcParameter(Types.ROWID, x));

        createChain().preparedStatement_setRowId(this, parameterIndex, x);
    }

    @Override
    public void setSQLXML(int parameterIndex, SQLXML x) throws SQLException {
        setParameter(parameterIndex, new JdbcParameter(Types.SQLXML, x));

        createChain().preparedStatement_setSQLXML(this, parameterIndex, x);
    }

    @Override
    public void setShort(int parameterIndex, short x) throws SQLException {
        setParameter(parameterIndex, new JdbcParameter(Types.SMALLINT, x));

        createChain().preparedStatement_setShort(this, parameterIndex, x);
    }

    @Override
    public void setString(int parameterIndex, String x) throws SQLException {
        setParameter(parameterIndex, new JdbcParameter(Types.VARCHAR, x));

        createChain().preparedStatement_setString(this, parameterIndex, x);
    }

    @Override
    public void setTime(int parameterIndex, Time x) throws SQLException {
        setParameter(parameterIndex, new JdbcParameter(Types.TIME, x));

        createChain().preparedStatement_setTime(this, parameterIndex, x);
    }

    @Override
    public void setTime(int parameterIndex, Time x, Calendar cal) throws SQLException {
        setParameter(parameterIndex, new JdbcParameter(Types.TIME, x, cal));

        createChain().preparedStatement_setTime(this, parameterIndex, x, cal);
    }

    @Override
    public void setTimestamp(int parameterIndex, Timestamp x) throws SQLException {
        setParameter(parameterIndex, new JdbcParameter(Types.TIMESTAMP, x));

        createChain().preparedStatement_setTimestamp(this, parameterIndex, x);
    }

    @Override
    public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal) throws SQLException {
        setParameter(parameterIndex, new JdbcParameter(Types.TIMESTAMP, x));

        createChain().preparedStatement_setTimestamp(this, parameterIndex, x, cal);
    }

    @Override
    public void setURL(int parameterIndex, URL x) throws SQLException {
        setParameter(parameterIndex, new JdbcParameter(TYPE.URL, x));

        createChain().preparedStatement_setURL(this, parameterIndex, x);
    }

    @Override
    public void setUnicodeStream(int parameterIndex, InputStream x, int length) throws SQLException {
        setParameter(parameterIndex, new JdbcParameter(TYPE.UnicodeStream, x, length));

        createChain().preparedStatement_setUnicodeStream(this, parameterIndex, x, length);
    }

    public void setParameter(int parameterIndex, JdbcParameter parameter) {
        this.getParameters().put(parameterIndex, parameter);
    }

    @Override
    public String getLastExecuteSql() {
        return this.sql;
    }

    @SuppressWarnings("unchecked")
    public <T> T unwrap(Class<T> iface) throws SQLException {
        if (iface == PreparedStatement.class) {
            return (T) statement;
        }

        return super.unwrap(iface);
    }

    public JdbcSqlStat getSqlStat() {
        if (sqlStat != null && sqlStat.isRemoved()) {
            JdbcSqlStat sqlStat = this.getConnectionProxy().getDirectDataSource().getDataSourceStat().getSqlStat(sql);
            sqlStat.setDbType(this.sqlStat.getDbType());
            this.sqlStat = sqlStat;
        }
        return sqlStat;
    }
}
