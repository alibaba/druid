package com.alibaba.druid.hdriver.impl;

import java.sql.SQLException;
import java.sql.Statement;

import com.alibaba.druid.hdriver.HResultSet;
import com.alibaba.druid.hdriver.HStatement;
import com.alibaba.druid.util.jdbc.StatementBase;

public class HStatementImpl extends StatementBase implements Statement, HStatement {

    private HBaseConnectionImpl conn;

    public HStatementImpl(HBaseConnectionImpl conn){
        super(conn);
        this.conn = conn;
    }

    @Override
    public HBaseConnectionImpl getConnection() throws SQLException {
        return conn;
    }

    public void checkOpen() {

    }
    
    public HResultSet getResultSet() throws SQLException {
        return (HResultSet) super.getResultSet();
    }
}
