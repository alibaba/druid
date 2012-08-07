package com.alibaba.druid.mock;

import java.sql.SQLException;
import java.sql.Statement;


public interface MockStatementBase extends Statement {
    MockConnection getConnection() throws SQLException;
}
