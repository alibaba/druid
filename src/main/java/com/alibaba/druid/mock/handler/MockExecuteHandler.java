package com.alibaba.druid.mock.handler;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.alibaba.druid.mock.MockStatementBase;

public interface MockExecuteHandler {

    ResultSet executeQuery(MockStatementBase statement, String sql) throws SQLException;
}
