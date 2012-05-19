package com.alibaba.druid.hbase.exec;

import java.sql.SQLException;

import com.alibaba.druid.hbase.HPreparedStatement;
import com.alibaba.druid.hbase.HBaseResultSet;

public class ExecutePlanAdapter implements ExecutePlan {

    @Override
    public HBaseResultSet executeQuery(HPreparedStatement statement) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean execute(HPreparedStatement statement) throws SQLException {
        throw new UnsupportedOperationException();
    }

}
