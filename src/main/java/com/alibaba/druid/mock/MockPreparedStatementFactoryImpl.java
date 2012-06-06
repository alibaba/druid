package com.alibaba.druid.mock;


public class MockPreparedStatementFactoryImpl implements MockPreparedStatementFactory {

    @Override
    public MockPreparedStatement createMockPreparedStatement(MockConnection conn, String sql) {
        return new MockPreparedStatement(conn, sql);
    }

}
