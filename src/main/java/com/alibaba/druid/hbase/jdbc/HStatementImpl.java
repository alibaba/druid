package com.alibaba.druid.hbase.jdbc;

import java.sql.SQLException;
import java.sql.Statement;

import com.alibaba.druid.common.jdbc.StatementBase;

public class HStatementImpl extends StatementBase implements Statement, HStatement {

    private HBaseConnection conn;

    public HStatementImpl(HBaseConnection conn){
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
