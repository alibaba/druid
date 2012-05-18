package com.alibaba.druid.hbase;

import java.sql.SQLException;
import java.sql.Statement;

public interface HBaseStatementInterface extends Statement {
    HBaseConnection getConnection() throws SQLException;
}
