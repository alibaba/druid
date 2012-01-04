package com.alibaba.druid.bvt.util;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import com.alibaba.druid.mock.MockDriver;


public class OracleMockDriver extends MockDriver {
    public Connection connect(String url, Properties info) throws SQLException {
        return new OracleMockConnection(this, url, info);
    }
}
