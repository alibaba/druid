package com.alibaba.druid.test.util;

import java.sql.SQLException;
import java.sql.Statement;


public class OracleMockResultSetJdbc3 extends OracleMockResultSet {

    public OracleMockResultSetJdbc3(Statement statement){
        super(statement);
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
