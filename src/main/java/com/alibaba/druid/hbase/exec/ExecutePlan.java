package com.alibaba.druid.hbase.exec;

import java.sql.SQLException;

import com.alibaba.druid.hbase.HPreparedStatement;
import com.alibaba.druid.hbase.HBaseResultSet;

public interface ExecutePlan {

    HBaseResultSet executeQuery(HPreparedStatement statement) throws SQLException;

    boolean execute(HPreparedStatement statement) throws SQLException;
}
