package com.alibaba.druid.mock;

import java.util.Properties;

public interface MockConnectionFactory {

    MockConnection createConnection(MockDriver driver, String url, Properties connectProperties);
}
