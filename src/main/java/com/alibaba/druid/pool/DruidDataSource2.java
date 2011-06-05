package com.alibaba.druid.pool;

import java.sql.Connection;
import java.sql.SQLException;

public class DruidDataSource2 extends DruidAbstractDataSource {

    private static final long serialVersionUID = 1L;

    @Override
    public Connection getConnection() throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    void incrementCreateCount() {
        // TODO Auto-generated method stub
        
    }

    @Override
    protected void recycle(PoolableConnection pooledConnection) throws SQLException {
        // TODO Auto-generated method stub
        
    }


}
