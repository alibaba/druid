package com.alibaba.druid.mock.handler;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.alibaba.druid.mock.MockStatement;

public interface MockExecuteHandler {

    ResultSet executeQuery(MockStatement statement, String sql) throws SQLException;
}
