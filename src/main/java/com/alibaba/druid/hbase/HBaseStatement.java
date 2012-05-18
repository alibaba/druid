package com.alibaba.druid.hbase;

import java.sql.SQLException;
import java.sql.Statement;

import com.alibaba.druid.common.jdbc.StatementBase;

public class HBaseStatement extends StatementBase implements Statement, HBaseStatementInterface {

    private HBaseConnection conn;

    public HBaseStatement(HBaseConnection conn){
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
