package com.yashandb.jdbc;

import com.alibaba.druid.mock.MockConnection;
import com.alibaba.druid.mock.MockDriver;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class Driver extends MockDriver {
    @Override
    public boolean acceptsURL(String url) throws SQLException {
        return url != null && url.startsWith("jdbc:yasdb:");
    }

    @Override
    public Connection connect(String url, Properties info) throws SQLException {
        if (!acceptsURL(url)) {
            return null;
        }
        MockConnection conn = createMockConnection(this, url, info);
        return conn;
    }

    @Override
    public int getMajorVersion() {
        return 1;
    }
}
