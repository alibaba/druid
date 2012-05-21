package com.alibaba.druid.hbase.jdbc;

import java.sql.SQLException;
import java.sql.Statement;

public interface HStatementInterface extends Statement {
    HBaseConnection getConnection() throws SQLException;
}
