package com.alibaba.druid.hbase.exec;

import java.sql.SQLException;

import com.alibaba.druid.hbase.HBasePreparedStatement;
import com.alibaba.druid.hbase.HBaseResultSet;

public interface ExecutePlan {

    HBaseResultSet executeQuery(HBasePreparedStatement statement) throws SQLException;

    boolean execute(HBasePreparedStatement statement) throws SQLException;
}
