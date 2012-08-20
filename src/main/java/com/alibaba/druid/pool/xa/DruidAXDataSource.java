package com.alibaba.druid.pool.xa;

import java.sql.SQLException;

import javax.sql.XAConnection;
import javax.sql.XADataSource;

import com.alibaba.druid.pool.DruidDataSource;


public class DruidAXDataSource extends DruidDataSource implements XADataSource {
    private static final long serialVersionUID = 1L;

    @Override
    public XAConnection getXAConnection() throws SQLException {
        return null;
    }

    @Override
    public XAConnection getXAConnection(String user, String password) throws SQLException {
        throw new UnsupportedOperationException("Not supported by DruidDataSource");
    }

}
