package com.alibaba.druid.test.util;

import java.sql.SQLException;


public class OracleMockPreparedStatementJdbc3 extends OracleMockPreparedStatement {

    public OracleMockPreparedStatementJdbc3(OracleMockConnection conn, String sql){
        super(conn, sql);
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        throw new UnsupportedOperationException();
    }
}
