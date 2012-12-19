package com.alibaba.druid.test.util;

import java.sql.SQLException;
import java.util.Properties;

import com.alibaba.druid.mock.MockDriver;


public class OracleMockConnectionJdbc3 extends OracleMockConnection {

    public OracleMockConnectionJdbc3(MockDriver driver, String url, Properties connectProperties){
        super(driver, url, connectProperties);
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
