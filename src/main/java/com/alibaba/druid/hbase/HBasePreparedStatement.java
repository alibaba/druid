package com.alibaba.druid.hbase;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.alibaba.druid.common.jdbc.PreparedStatementBase;

public class HBasePreparedStatement extends PreparedStatementBase implements PreparedStatement {

    private final String sql;
    private String[]     columnNames;

    public HBasePreparedStatement(HBaseConnection conn, String sql){
        super(conn);
        this.sql = sql;
    }

    public String[] getColumnNames() {
        return columnNames;
    }

    public void setColumnNames(String[] columnNames) {
        this.columnNames = columnNames;
    }

    public String getSql() {
        return sql;
    }

    @Override
    public ResultSet executeQuery() throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int executeUpdate() throws SQLException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public boolean execute() throws SQLException {
        checkOpen();

        return false;
    }

}
