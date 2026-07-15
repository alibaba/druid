package com.yashandb.jdbc;

import com.alibaba.druid.mock.MockDriver;

import java.sql.SQLException;

public class Driver extends MockDriver {
    @Override
    public boolean acceptsURL(String url) throws SQLException {
        return url != null && url.startsWith("jdbc:yasdb:");
    }

    @Override
    public int getMajorVersion() {
        return 1;
    }
}
