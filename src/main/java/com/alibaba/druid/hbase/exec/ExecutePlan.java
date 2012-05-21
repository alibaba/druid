package com.alibaba.druid.hbase.exec;

import java.sql.SQLException;

import com.alibaba.druid.hbase.jdbc.HPreparedStatementImpl;
import com.alibaba.druid.hbase.jdbc.HResultSet;

public interface ExecutePlan {

    HResultSet executeQuery(HPreparedStatementImpl statement) throws SQLException;

    boolean execute(HPreparedStatementImpl statement) throws SQLException;
}
