package com.alibaba.druid.pool.xa;

import java.sql.SQLException;

import javax.sql.XAConnection;
import javax.transaction.xa.XAResource;

import com.alibaba.druid.pool.DruidConnectionHolder;
import com.alibaba.druid.pool.DruidPooledConnection;

public class DruidXAPooledConnection extends DruidPooledConnection implements XAConnection {

    protected XAResource xaResource = null;

    public DruidXAPooledConnection(DruidConnectionHolder holder){
        super(holder);
        // TODO Auto-generated constructor stub
    }

    @Override
    public XAResource getXAResource() throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

}
