package com.alibaba.druid.hbase;

import java.io.IOException;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.util.Bytes;

import com.alibaba.druid.common.jdbc.ResultSetBase;
import com.alibaba.druid.util.JdbcUtils;

public class HBaseResultSet extends ResultSetBase {

    private HBaseStatementInterface statement;
    private ResultScanner           scanner;
    private HTableInterface         htable;
    private Result                  result;
    private byte[]                  family = Bytes.toBytes("d");

    public HBaseResultSet(HBaseStatementInterface statement, HTableInterface htable, ResultScanner scanner){
        super(statement);
        this.statement = statement;
        this.htable = htable;
        this.scanner = scanner;
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
    public ResultSetMetaData getMetaData() throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean previous() throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void updateObject(int columnIndex, Object x) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public Object getObjectInternal(int columnIndex) {
        return null;
    }

    @Override
    public String getString(String columnName) throws SQLException {
        byte[] bytes = getBytes(columnName);
        return Bytes.toString(bytes);
    }

    public byte[] getBytes(String columnName) throws SQLException {
        if ("@ROW".equals(columnName)) {
            return result.getRow();
        }

        byte[] qualifier = Bytes.toBytes(columnName);
        byte[] value = result.getValue(family, qualifier);
        return value;
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
