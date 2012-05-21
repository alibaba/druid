package com.alibaba.druid.hdriver;

import java.sql.SQLException;
import java.sql.Statement;

import com.alibaba.druid.hdriver.impl.jdbc.HBaseConnection;

public interface HStatement extends Statement {
    HBaseConnection getConnection() throws SQLException;
}
