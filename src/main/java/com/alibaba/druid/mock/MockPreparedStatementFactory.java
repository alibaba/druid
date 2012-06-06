package com.alibaba.druid.mock;


public interface MockPreparedStatementFactory {
    MockPreparedStatement createMockPreparedStatement(MockConnection conn, String sql);
}
