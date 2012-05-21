package com.alibaba.druid.hbase.jdbc;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.util.Bytes;

import com.alibaba.druid.common.jdbc.ResultSetBase;
import com.alibaba.druid.common.jdbc.ResultSetMetaDataBase.ColumnMetaData;
import com.alibaba.druid.hbase.mapping.HMappingTable;
import com.alibaba.druid.util.JdbcUtils;

public class HBaseResultSet extends ResultSetBase implements HResultSet {

    private HStatement statement;
    private ResultScanner       scanner;
    private HTableInterface     htable;
    private Result              result;
    private byte[]              family = Bytes.toBytes("d");

    private HMappingTable            mapping;

    public HBaseResultSet(HStatement statement, HTableInterface htable, ResultScanner scanner){
        super(statement);
        this.statement = statement;
        this.htable = htable;
        this.scanner = scanner;
    }

    public HMappingTable getMapping() {
        return mapping;
    }

    public void setMapping(HMappingTable mapping) {
        this.mapping = mapping;
    }

    @Override
    public Statement getStatement() throws SQLException {
        return statement;
    }

    @Override
    public boolean next() throws SQLException {
        try {
            result = scanner.next();
            return result != null && !result.isEmpty();
        } catch (IOException ex) {
            throw new SQLException("read next error", ex);
        }
    }

    @Override
    public HResultSetMetaData getMetaData() throws SQLException {
        return (HResultSetMetaData) metaData;
    }

    public void setMetaData(HResultSetMetaData metaData) {
        this.metaData = metaData;
    }

    @Override
    public boolean previous() throws SQLException {
        return false;
    }

    @Override
    public void updateObject(int columnIndex, Object x) throws SQLException {
    }

    @Override
    public Object getObjectInternal(int columnIndex) throws SQLException {
        ColumnMetaData column = this.getMetaData().getColumn(columnIndex);
        return getObjectInternal(column.getColumnName(), column.getColumnType());
    }

    public Object getObjectInternal(String columnName, int type) throws SQLException {
        byte[] bytes = getBytes(columnName);
        switch (type) {
            case Types.TINYINT:
                return bytes[0];
            case Types.SMALLINT:
                return Bytes.toShort(bytes);
            case Types.INTEGER:
                return Bytes.toInt(bytes);
            case Types.BIGINT:
                return Bytes.toLong(bytes);
            case Types.DECIMAL:
                return Bytes.toBigDecimal(bytes);
            case Types.BOOLEAN:
                return Bytes.toBoolean(bytes);
            case Types.VARCHAR:
            case Types.CHAR:
            case Types.NCHAR:
            case Types.NVARCHAR:
                return Bytes.toString(bytes);
            case Types.DATE:
                return new java.sql.Date(Bytes.toLong(bytes));
            case Types.TIMESTAMP:
                return new java.sql.Timestamp(Bytes.toLong(bytes));
            case Types.NULL:
            case Types.VARBINARY:
            case Types.BINARY:
            case Types.ROWID:
            default:
                return bytes;
        }
    }

    @Override
    public String getString(int columnIndex) throws SQLException {
        byte[] bytes = getBytes(columnIndex);
        return Bytes.toString(bytes);
    }

    @Override
    public String getString(String columnName) throws SQLException {
        byte[] bytes = getBytes(columnName);
        return Bytes.toString(bytes);
    }

    @Override
    public byte[] getBytes(int columnIndex) throws SQLException {
        ColumnMetaData column = this.getMetaData().getColumn(columnIndex);
        return getBytes(column.getColumnName());
    }

    public byte[] getBytes(String columnName) throws SQLException {
        if ("@ROW".equals(columnName)) {
            return result.getRow();
        }

        if ("id".equals(columnName)) { // TODO
            return result.getRow();
        }

        byte[] qualifier = Bytes.toBytes(columnName);
        byte[] value = result.getValue(family, qualifier);

        this.wasNull = value == null;

        return value;
    }

    @Override
    public int getInt(String columnName) throws SQLException {
        byte[] bytes = getBytes(columnName);

        if (bytes == null) {
            return 0;
        }

        return Bytes.toInt(bytes);
    }

    @Override
    public int getInt(int columnIndex) throws SQLException {
        byte[] bytes = getBytes(columnIndex);

        if (bytes == null) {
            return 0;
        }

        return Bytes.toInt(bytes);
    }

    @Override
    public BigDecimal getBigDecimal(String columnName) throws SQLException {
        byte[] bytes = getBytes(columnName);

        if (bytes == null) {
            return null;
        }

        return Bytes.toBigDecimal(bytes);
    }

    @Override
    public BigDecimal getBigDecimal(int columnIndex) throws SQLException {
        byte[] bytes = getBytes(columnIndex);

        if (bytes == null) {
            return null;
        }

        return Bytes.toBigDecimal(bytes);
    }

    @Override
    public void close() throws SQLException {
        JdbcUtils.close(scanner);
        try {
            htable.close();
        } catch (Exception ex) {
            throw new SQLException("close error", ex);
        }
        super.close();
    }

}
