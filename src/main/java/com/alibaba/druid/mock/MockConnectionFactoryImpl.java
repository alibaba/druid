package com.alibaba.druid.mock;

import java.util.Properties;

public class MockConnectionFactoryImpl implements MockConnectionFactory {

    @Override
    public MockConnection createConnection(MockDriver driver, String url, Properties connectProperties) {
        return new MockConnection(driver, url, connectProperties);
    }

}
