package com.alibaba.druid.hbase.exec;

import java.sql.SQLException;

import com.alibaba.druid.hbase.jdbc.HPreparedStatementImpl;
import com.alibaba.druid.hbase.jdbc.HResultSet;

public class ExecutePlanAdapter implements ExecutePlan {

    @Override
    public HResultSet executeQuery(HPreparedStatementImpl statement) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean execute(HPreparedStatementImpl statement) throws SQLException {
        throw new UnsupportedOperationException();
    }

}
