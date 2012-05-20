package com.alibaba.druid.hbase.exec;

import java.sql.SQLException;

import com.alibaba.druid.hbase.HPreparedStatement;
import com.alibaba.druid.hbase.HResultSet;

public interface ExecutePlan {

    HResultSet executeQuery(HPreparedStatement statement) throws SQLException;

    boolean execute(HPreparedStatement statement) throws SQLException;
}
