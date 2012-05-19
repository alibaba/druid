package com.alibaba.druid.hbase;

import java.sql.SQLException;
import java.sql.Statement;

import com.alibaba.druid.common.jdbc.StatementBase;

public class HStatement extends StatementBase implements Statement, HStatementInterface {

    private HBaseConnection conn;

    public HStatement(HBaseConnection conn){
        super(conn);
        this.conn = conn;
    }

    @Override
    public HBaseConnection getConnection() throws SQLException {
        return conn;
    }

    public void checkOpen() {

    }
}
