package com.alibaba.druid.hbase;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.alibaba.druid.common.jdbc.PreparedStatementBase;

public class HBasePreparedStatement extends PreparedStatementBase implements PreparedStatement {

    public HBasePreparedStatement(HBaseConnection conn){
        super(conn);
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
