package com.alibaba.druid.hbase.exec;

import java.sql.SQLException;

import com.alibaba.druid.hbase.HBasePreparedStatement;
import com.alibaba.druid.hbase.HBaseResultSet;

public interface ExecutePlan {
    HBaseResultSet executeScan(HBasePreparedStatement statement) throws SQLException;
}
