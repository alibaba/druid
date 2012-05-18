package com.alibaba.druid.hbase;

import java.io.IOException;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.util.Bytes;

import com.alibaba.druid.common.jdbc.ResultSetBase;

public class HBaseResultSet extends ResultSetBase {

    private HBaseStatementInterface statement;
    private ResultScanner           scanner;
    private Result                  result;

    public HBaseResultSet(HBaseStatementInterface statement, ResultScanner scanner){
        super(statement);
        this.statement = statement;
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
        byte[] qualifier = Bytes.toBytes(columnName);
        byte[] family = Bytes.toBytes("d");
        byte[] value = result.getValue(family, qualifier);
        return Bytes.toString(value);
    }

    @Override
    public void close() throws SQLException {
        try {
            scanner.close();
        } catch (Exception ex) {
            throw new SQLException("close error", ex);
        }
        super.close();
    }

}
