package com.alibaba.druid.sharding;

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
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.h2.util.StringUtils;

import com.alibaba.druid.proxy.jdbc.JdbcParameter;
import com.alibaba.druid.proxy.jdbc.JdbcParameter.TYPE;
import com.alibaba.druid.sharding.sql.ShardingVisitor;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.util.JdbcUtils;

public class ShardingPreparedStatement extends ShardingStatement implements PreparedStatement {

    private final String                        sql;
    private PreparedStatement                   pstmt;

    private List<String>                        tables;
    protected final Map<Integer, JdbcParameter> parameters = new TreeMap<Integer, JdbcParameter>();

    public ShardingPreparedStatement(ShardingConnection conn, String sql, long id){
        super(conn, id);
        this.sql = sql;
    }

    public ShardingPreparedStatement(ShardingConnection conn, String sql, long id, int resultSetType,
                                     int resultSetConcurrency, int resultSetHoldability){
        super(conn, id, resultSetType, resultSetConcurrency, resultSetHoldability);
        this.sql = sql;
    }

    public ShardingPreparedStatement(ShardingConnection conn, String sql, long id, int resultSetType,
                                     int resultSetConcurrency){
        super(conn, id, resultSetType, resultSetConcurrency);
        this.sql = sql;
    }

    @Override
    public void setArray(int parameterIndex, Array x) throws SQLException {
        setParameter(parameterIndex, new JdbcParameter(Types.ARRAY, x));
    }

    @Override
    public void setAsciiStream(int parameterIndex, InputStream x) throws SQLException {
        setParameter(parameterIndex, new JdbcParameter(JdbcParameter.TYPE.AsciiInputStream, x));
    }

    @Override
    public void setAsciiStream(int parameterIndex, InputStream x, int length) throws SQLException {
        setParameter(parameterIndex, new JdbcParameter(JdbcParameter.TYPE.AsciiInputStream, x, length));
    }

    @Override
    public void setAsciiStream(int parameterIndex, InputStream x, long length) throws SQLException {
        setParameter(parameterIndex, new JdbcParameter(JdbcParameter.TYPE.AsciiInputStream, x, length));
    }

    @Override
    public void setBigDecimal(int parameterIndex, BigDecimal x) throws SQLException {
        setParameter(parameterIndex, new JdbcParameter(Types.DECIMAL, x));
    }

    @Override
    public void setBinaryStream(int parameterIndex, InputStream x) throws SQLException {
        setParameter(parameterIndex, new JdbcParameter(JdbcParameter.TYPE.BinaryInputStream, x));

    }

    @Override
    public void setBinaryStream(int parameterIndex, InputStream x, int length) throws SQLException {
        setParameter(parameterIndex, new JdbcParameter(JdbcParameter.TYPE.BinaryInputStream, x, length));

    }

    @Override
    public void setBinaryStream(int parameterIndex, InputStream x, long length) throws SQLException {
        setParameter(parameterIndex, new JdbcParameter(JdbcParameter.TYPE.BinaryInputStream, x, length));

    }

    @Override
    public void setBlob(int parameterIndex, Blob x) throws SQLException {
        setParameter(parameterIndex, new JdbcParameter(Types.BLOB, x));

    }

    @Override
    public void setBlob(int parameterIndex, InputStream x) throws SQLException {
        setParameter(parameterIndex, new JdbcParameter(Types.BLOB, x));

    }

    @Override
    public void setBlob(int parameterIndex, InputStream x, long length) throws SQLException {
        setParameter(parameterIndex, new JdbcParameter(Types.BLOB, x, length));
    }

    @Override
    public void setBoolean(int parameterIndex, boolean x) throws SQLException {
        setParameter(parameterIndex, new JdbcParameter(Types.BOOLEAN, x));
    }

    @Override
    public void setByte(int parameterIndex, byte x) throws SQLException {
        setParameter(parameterIndex, new JdbcParameter(Types.TINYINT, x));

    }

    @Override
    public void setBytes(int parameterIndex, byte[] x) throws SQLException {
        setParameter(parameterIndex, new JdbcParameter(TYPE.BYTES, x));

    }

    @Override
    public void setCharacterStream(int parameterIndex, Reader x) throws SQLException {
        setParameter(parameterIndex, new JdbcParameter(TYPE.CharacterInputStream, x));

    }

    @Override
    public void setCharacterStream(int parameterIndex, Reader x, int length) throws SQLException {
        setParameter(parameterIndex, new JdbcParameter(TYPE.CharacterInputStream, x, length));

    }

    @Override
    public void setCharacterStream(int parameterIndex, Reader x, long length) throws SQLException {
        setParameter(parameterIndex, new JdbcParameter(TYPE.CharacterInputStream, x, length));

    }

    @Override
    public void setClob(int parameterIndex, Clob x) throws SQLException {
        setParameter(parameterIndex, new JdbcParameter(Types.CLOB, x));

    }

    @Override
    public void setClob(int parameterIndex, Reader x) throws SQLException {
        setParameter(parameterIndex, new JdbcParameter(Types.CLOB, x));

    }

    @Override
    public void setClob(int parameterIndex, Reader x, long length) throws SQLException {
        setParameter(parameterIndex, new JdbcParameter(Types.CLOB, x, length));

    }

    @Override
    public void setDate(int parameterIndex, Date x) throws SQLException {
        setParameter(parameterIndex, new JdbcParameter(Types.DATE, x));

    }

    @Override
    public void setDate(int parameterIndex, Date x, Calendar cal) throws SQLException {
        setParameter(parameterIndex, new JdbcParameter(Types.DATE, x, cal));

    }

    @Override
    public void setDouble(int parameterIndex, double x) throws SQLException {
        setParameter(parameterIndex, new JdbcParameter(Types.DOUBLE, x));

    }

    @Override
    public void setFloat(int parameterIndex, float x) throws SQLException {
        setParameter(parameterIndex, new JdbcParameter(Types.FLOAT, x));

    }

    @Override
    public void setInt(int parameterIndex, int x) throws SQLException {
        setParameter(parameterIndex, new JdbcParameter(Types.INTEGER, x));

    }

    @Override
    public void setLong(int parameterIndex, long x) throws SQLException {
        setParameter(parameterIndex, new JdbcParameter(Types.BIGINT, x));

    }

    @Override
    public void setNCharacterStream(int parameterIndex, Reader x) throws SQLException {
        setParameter(parameterIndex, new JdbcParameter(TYPE.NCharacterInputStream, x));

    }

    @Override
    public void setNCharacterStream(int parameterIndex, Reader x, long length) throws SQLException {
        setParameter(parameterIndex, new JdbcParameter(TYPE.NCharacterInputStream, x, length));

    }

    @Override
    public void setNClob(int parameterIndex, NClob x) throws SQLException {
        setParameter(parameterIndex, new JdbcParameter(Types.NCLOB, x));

    }

    @Override
    public void setNClob(int parameterIndex, Reader x) throws SQLException {
        setParameter(parameterIndex, new JdbcParameter(Types.NCLOB, x));

    }

    @Override
    public void setNClob(int parameterIndex, Reader x, long length) throws SQLException {
        setParameter(parameterIndex, new JdbcParameter(Types.NCLOB, x, length));

    }

    @Override
    public void setNString(int parameterIndex, String x) throws SQLException {
        setParameter(parameterIndex, new JdbcParameter(Types.NVARCHAR, x));

    }

    @Override
    public void setNull(int parameterIndex, int sqlType) throws SQLException {
        setParameter(parameterIndex, new JdbcParameter(sqlType, null));

    }

    @Override
    public void setNull(int parameterIndex, int sqlType, String typeName) throws SQLException {
        setParameter(parameterIndex, new JdbcParameter(sqlType, null));

    }

    @Override
    public void setObject(int parameterIndex, Object x) throws SQLException {
        setParameter(parameterIndex, new JdbcParameter(Types.OTHER, null));

    }

    @Override
    public void setObject(int parameterIndex, Object x, int targetSqlType) throws SQLException {
        setParameter(parameterIndex, new JdbcParameter(targetSqlType, x));

    }

    @Override
    public void setObject(int parameterIndex, Object x, int targetSqlType, int scaleOrLength) throws SQLException {
        setParameter(parameterIndex, new JdbcParameter(targetSqlType, x, -1, null, scaleOrLength));
    }

    @Override
    public void setRef(int parameterIndex, Ref x) throws SQLException {
        setParameter(parameterIndex, new JdbcParameter(Types.REF, x));

    }

    @Override
    public void setRowId(int parameterIndex, RowId x) throws SQLException {
        setParameter(parameterIndex, new JdbcParameter(Types.ROWID, x));

    }

    @Override
    public void setSQLXML(int parameterIndex, SQLXML x) throws SQLException {
        setParameter(parameterIndex, new JdbcParameter(Types.SQLXML, x));

    }

    @Override
    public void setShort(int parameterIndex, short x) throws SQLException {
        setParameter(parameterIndex, new JdbcParameter(Types.SMALLINT, x));

    }

    @Override
    public void setString(int parameterIndex, String x) throws SQLException {
        setParameter(parameterIndex, new JdbcParameter(Types.VARCHAR, x));

    }

    @Override
    public void setTime(int parameterIndex, Time x) throws SQLException {
        setParameter(parameterIndex, new JdbcParameter(Types.TIME, x));

    }

    @Override
    public void setTime(int parameterIndex, Time x, Calendar cal) throws SQLException {
        setParameter(parameterIndex, new JdbcParameter(Types.TIME, x, cal));

    }

    @Override
    public void setTimestamp(int parameterIndex, Timestamp x) throws SQLException {
        setParameter(parameterIndex, new JdbcParameter(Types.TIMESTAMP, x));

    }

    @Override
    public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal) throws SQLException {
        setParameter(parameterIndex, new JdbcParameter(Types.TIMESTAMP, x));

    }

    @Override
    public void setURL(int parameterIndex, URL x) throws SQLException {
        setParameter(parameterIndex, new JdbcParameter(TYPE.URL, x));

    }

    @Override
    public void setUnicodeStream(int parameterIndex, InputStream x, int length) throws SQLException {
        setParameter(parameterIndex, new JdbcParameter(TYPE.UnicodeStream, x, length));

    }

    public void setParameter(int parameterIndex, JdbcParameter parameter) {
        this.getParameters().put(parameterIndex, parameter);
    }

    public Map<Integer, JdbcParameter> getParameters() {
        return parameters;
    }

    @Override
    public void clearParameters() throws SQLException {
        this.parameters.clear();
    }

    @Override
    public ResultSetMetaData getMetaData() throws SQLException {
        if (pstmt != null) {
            return pstmt.getMetaData();
        }
        return null;
    }

    @Override
    public ParameterMetaData getParameterMetaData() throws SQLException {
        if (pstmt != null) {
            return pstmt.getParameterMetaData();
        }
        return null;
    }

    @Override
    public ResultSet executeQuery() throws SQLException {
        List<Object> values = getParameterValues();

        ShardingDataSource dataSource = conn.getDataSource();
        ShardingVisitor visitor = dataSource.createShardingVisitor(values);
        SQLStatement sqlStmt = dataSource.parseStatement(sql);

        sqlStmt.accept(visitor);

        List<SQLStatement> resultSqlStmtList = visitor.getResult();

        checkParameterValues(values, resultSqlStmtList);

        if (resultSqlStmtList.size() > 1) {
            throw new SQLException("executeQuery not support multi-statement");
        }

        SQLStatement resultSqlStmt = resultSqlStmtList.get(0);
        String resultSql = SQLUtils.toSQLString(resultSqlStmt, dataSource.getDbType());

        String database = (String) resultSqlStmt.getAttribute(ShardingVisitor.ATTR_DB);

        checkPreparedStatement(resultSqlStmt, resultSql, database);
        
        return pstmt.executeQuery();
    }

    @SuppressWarnings("unchecked")
    private void checkPreparedStatement(SQLStatement resultSqlStmt, String resultSql, String database)
                                                                                                      throws SQLException {
        if (conn.getRealConnection() != null //
            && StringUtils.equals(database, conn.getDatabase()) //
        ) {
            if (pstmt == null) {
                pstmt = createRealPreparedStatement(conn.getRealConnection(), resultSql);
                tables = (List<String>) resultSqlStmt.getAttribute(ShardingVisitor.ATTR_TABLES);
            } else if (tables != null //
                       && tables.equals(resultSqlStmt.getAttribute(ShardingVisitor.ATTR_TABLES))) {
                if (pstmt != null) {
                    JdbcUtils.close(pstmt);
                    pstmt = null;
                    tables = null;
                }
                pstmt = createRealPreparedStatement(conn.getRealConnection(), resultSql);
                tables = (List<String>) resultSqlStmt.getAttribute(ShardingVisitor.ATTR_TABLES);
            }
        } else {
            conn.closeRealConnection();
            if (pstmt != null) {
                JdbcUtils.close(pstmt);
                pstmt = null;
                tables = null;
            }
            conn.createRealConnectionByDb(database);
            pstmt = createRealPreparedStatement(conn.getRealConnection(), resultSql);
        }
    }

    @Override
    public int executeUpdate() throws SQLException {
        List<Object> values = getParameterValues();

        ShardingDataSource dataSource = conn.getDataSource();
        ShardingVisitor visitor = dataSource.createShardingVisitor(values);
        SQLStatement sqlStmt = dataSource.parseStatement(sql);

        sqlStmt.accept(visitor);

        List<SQLStatement> resultSqlStmtList = visitor.getResult();

        checkParameterValues(values, resultSqlStmtList);

        int updateCount = 0;

        for (SQLStatement resultSqlStmt : resultSqlStmtList) {
            String resultSql = SQLUtils.toSQLString(resultSqlStmt, dataSource.getDbType());

            String database = (String) resultSqlStmt.getAttribute(ShardingVisitor.ATTR_DB);

            checkPreparedStatement(resultSqlStmt, resultSql, database);
            updateCount += pstmt.executeUpdate();
        }

        return updateCount;
    }

    private void checkParameterValues(List<Object> values, List<SQLStatement> resultSqlStmtList) throws SQLException {
        if (resultSqlStmtList.size() > 1) {
            for (Object value : values) {
                if (value instanceof InputStream //
                    || value instanceof Reader //
                    || value instanceof Blob //
                    || value instanceof Clob //
                    || value instanceof NClob //
                ) {
                    throw new SQLException(value.getClass().getName() + " not support multi-statement");
                }
            }
        }
    }

    private List<Object> getParameterValues() {
        List<Object> values = new ArrayList<Object>();
        for (Map.Entry<Integer, JdbcParameter> entry : this.parameters.entrySet()) {
            values.add(entry.getValue().getValue());
        }
        return values;
    }

    @Override
    public boolean execute() throws SQLException {
        List<Object> values = getParameterValues();

        ShardingDataSource dataSource = conn.getDataSource();
        ShardingVisitor visitor = dataSource.createShardingVisitor(values);
        SQLStatement sqlStmt = dataSource.parseStatement(sql);

        sqlStmt.accept(visitor);

        List<SQLStatement> resultSqlStmtList = visitor.getResult();

        checkParameterValues(values, resultSqlStmtList);

        boolean isSelelct = sqlStmt instanceof SQLSelectStatement;

        if (resultSqlStmtList.size() > 1 && isSelelct) {
            throw new SQLException("select not support multi-statement");
        }

        for (SQLStatement resultSqlStmt : resultSqlStmtList) {
            String resultSql = SQLUtils.toSQLString(resultSqlStmt, dataSource.getDbType());

            String database = (String) resultSqlStmt.getAttribute(ShardingVisitor.ATTR_DB);

            checkPreparedStatement(resultSqlStmt, resultSql, database);
            pstmt.execute();

            if (!isSelelct) {
                if (updateCount == null) {
                    updateCount = pstmt.getUpdateCount();
                } else {
                    updateCount = updateCount.intValue() + pstmt.getUpdateCount();
                }
            }
        }

        return isSelelct;
    }

    @Override
    public void addBatch() throws SQLException {
        throw new UnsupportedOperationException();
    }

    @SuppressWarnings("deprecation")
    private PreparedStatement createRealPreparedStatement(Connection conn, String sql) throws SQLException {
        PreparedStatement stmt;
        if (resultSetType != null && resultSetConcurrency != null && resultSetHoldability != null) {
            stmt = conn.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
        } else if (resultSetType != null && resultSetConcurrency != null) {
            stmt = conn.prepareStatement(sql, resultSetType, resultSetConcurrency);
        } else {
            stmt = conn.prepareStatement(sql);
        }

        initStatement(stmt);

        for (Map.Entry<Integer, JdbcParameter> entry : this.parameters.entrySet()) {
            int index = entry.getKey();
            JdbcParameter param = entry.getValue();
            Object value = param.getValue();

            if (param.getScaleOrLength() != -1) {
                stmt.setObject(index, value, param.getSqlType(), param.getScaleOrLength());
            } else if (value == null) {
                stmt.setNull(index, param.getSqlType());
            } else {
                switch (param.getSqlType()) {
                    case Types.ARRAY:
                        stmt.setArray(index, (Array) value);
                        break;
                    case TYPE.AsciiInputStream:
                        if (param.getLength() >= 0) {
                            stmt.setAsciiStream(index, (InputStream) value, param.getLength());
                        } else {
                            stmt.setAsciiStream(index, (InputStream) value);
                        }
                        break;
                    case Types.DECIMAL:
                        stmt.setBigDecimal(index, (BigDecimal) value);
                        break;
                    case TYPE.BinaryInputStream:
                        if (param.getLength() >= 0) {
                            stmt.setBinaryStream(index, (InputStream) value, param.getLength());
                        } else {
                            stmt.setBinaryStream(index, (InputStream) value);
                        }
                        break;
                    case Types.BLOB:
                        if (value instanceof Blob) {
                            stmt.setBlob(index, (Blob) value);
                        } else {
                            if (param.getLength() >= 0) {
                                stmt.setBlob(index, (InputStream) value, param.getLength());
                            } else {
                                stmt.setBlob(index, (InputStream) value);
                            }
                        }
                        break;
                    case Types.BOOLEAN:
                        stmt.setBoolean(index, (Boolean) value);
                        break;
                    case Types.TINYINT:
                        stmt.setByte(index, (Byte) value);
                        break;
                    case TYPE.BYTES:
                        stmt.setBytes(index, (byte[]) value);
                        break;
                    case TYPE.CharacterInputStream:
                        if (param.getLength() >= 0) {
                            stmt.setCharacterStream(index, (Reader) value, param.getLength());
                        } else {
                            stmt.setCharacterStream(index, (Reader) value);
                        }
                        break;
                    case Types.CLOB:
                        if (value instanceof Clob) {
                            stmt.setClob(index, (Clob) value);
                        } else {
                            if (param.getLength() >= 0) {
                                stmt.setClob(index, (Reader) value, param.getLength());
                            } else {
                                stmt.setClob(index, (Reader) value);
                            }
                        }
                        break;
                    case Types.DATE:
                        if (param.getCalendar() != null) {
                            stmt.setDate(index, (Date) value, param.getCalendar());
                        } else {
                            stmt.setDate(index, (Date) value);
                        }
                        break;
                    case Types.DOUBLE:
                        stmt.setDouble(index, (Double) value);
                        break;
                    case Types.FLOAT:
                        stmt.setFloat(index, (Float) value);
                        break;
                    case Types.INTEGER:
                        stmt.setInt(index, (Integer) value);
                        break;
                    case Types.BIGINT:
                        stmt.setLong(index, (Long) value);
                        break;
                    case TYPE.NCharacterInputStream:
                        if (param.getLength() >= 0) {
                            stmt.setNCharacterStream(index, (Reader) value, param.getLength());
                        } else {
                            stmt.setNCharacterStream(index, (Reader) value);
                        }
                        break;
                    case Types.NCLOB:
                        if (value instanceof Clob) {
                            stmt.setNClob(index, (NClob) value);
                        } else {
                            if (param.getLength() >= 0) {
                                stmt.setNClob(index, (Reader) value, param.getLength());
                            } else {
                                stmt.setNClob(index, (Reader) value);
                            }
                        }
                        break;
                    case Types.NCHAR:
                    case Types.NVARCHAR:
                        stmt.setNString(index, (String) value);
                        break;
                    case Types.OTHER:
                        stmt.setObject(index, value);
                        break;
                    case Types.REF:
                        stmt.setRef(index, (Ref) value);
                        break;
                    case Types.ROWID:
                        stmt.setRowId(index, (RowId) value);
                        break;
                    case Types.SQLXML:
                        stmt.setSQLXML(index, (SQLXML) value);
                        break;
                    case Types.SMALLINT:
                        stmt.setShort(index, (Short) value);
                        break;
                    case Types.VARCHAR:
                    case Types.CHAR:
                        stmt.setString(index, (String) value);
                        break;
                    case Types.TIME:
                        if (param.getCalendar() != null) {
                            stmt.setTime(index, (Time) value, param.getCalendar());
                        } else {
                            stmt.setTime(index, (Time) value);
                        }
                        break;
                    case Types.TIMESTAMP:
                        if (param.getCalendar() != null) {
                            stmt.setTimestamp(index, (Timestamp) value, param.getCalendar());
                        } else {
                            stmt.setTimestamp(index, (Timestamp) value);
                        }
                        break;
                    case TYPE.URL:
                        stmt.setURL(index, (URL) value);
                        break;
                    case TYPE.UnicodeStream:
                        stmt.setUnicodeStream(index, (InputStream) value, (int) param.getLength());
                        break;
                    default:
                        stmt.setObject(index, value);
                        break;
                }
            }
        }

        return stmt;
    }

}
