package com.alibaba.druid.hbase.exec;

import java.sql.SQLException;

import com.alibaba.druid.hbase.HBasePreparedStatement;
import com.alibaba.druid.hbase.HBaseResultSet;

public class ExecutePlanAdapter implements ExecutePlan {

    @Override
    public HBaseResultSet executeQuery(HBasePreparedStatement statement) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void execute(HBasePreparedStatement statement) throws SQLException {
        throw new UnsupportedOperationException();
    }

}
