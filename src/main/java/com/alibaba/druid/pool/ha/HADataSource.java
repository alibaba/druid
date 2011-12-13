package com.alibaba.druid.pool.ha;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import javax.sql.DataSource;

import com.alibaba.druid.logging.Log;
import com.alibaba.druid.logging.LogFactory;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.ManagedDataSource;

public class HADataSource extends MultiDataSource implements HADataSourceMBean, ManagedDataSource, DataSource {

    private final static Log  LOG                = LogFactory.getLog(HADataSource.class);

    private final AtomicLong  masterConnectCount = new AtomicLong();
    private final AtomicLong  slaveConnectCount  = new AtomicLong();

    protected DruidDataSource master;
    protected DruidDataSource slave;

    public HADataSource(){

    }
    
    public void resetStat() {
        super.resetStat();
        
        masterConnectCount.set(0);
        slaveConnectCount.set(0);
    }

    public long getMasterConnectCount() {
        return masterConnectCount.get();
    }

    public long getSlaveConnectCount() {
        return slaveConnectCount.get();
    }

    public DruidDataSource getMaster() {
        return master;
    }

    public void setMaster(DruidDataSource master) {
        this.getDataSources().put("master", master);
        this.master = master;
    }

    public DruidDataSource getSlave() {
        return slave;
    }

    public void setSlave(DruidDataSource slave) {
        this.getDataSources().put("slave", slave);
        this.slave = slave;
    }

    public boolean isMasterEnable() {
        if (master == null) {
            return false;
        }

        return master.isEnable();
    }

    public void setMasterEnable(boolean value) {
        if (master == null) {
            throw new IllegalStateException("slave is null");
        }

        master.setEnable(value);
    }
    
    public String getMasterUrl() {
        if (master == null) {
            return null;
        }
        
        return master.getUrl();
    }

    public String getSlaveUrl() {
        if (slave == null) {
            return null;
        }
        
        return slave.getUrl();
    }
    
    public boolean isSlaveEnable() {
        if (slave == null) {
            return false;
        }

        return slave.isEnable();
    }

    public void setSlaveEnable(boolean value) {
        if (slave == null) {
            throw new IllegalStateException("slave is null");
        }

        slave.setEnable(value);
    }
    
    public void switchMasterSlave() {
    	DruidDataSource tmp = this.getMaster();
    	this.setMaster(this.getSlave());
    	this.setSlave(tmp);
    }

    public synchronized void setDataSources(List<DruidDataSource> dataSources) {
        throw new UnsupportedOperationException();
    }

    public Connection getConnectionInternal(MultiDataSourceConnection connection, String sql) throws SQLException {
        Connection conn = null;
        if (master.isEnable()) {
            conn = master.getConnection();
            masterConnectCount.incrementAndGet();
        }

        if (conn == null && slave.isEnable()) {
            conn = slave.getConnection();
            slaveConnectCount.incrementAndGet();
        }

        if (conn == null) {
            throw new SQLException("get HAConnection error");
        }

        return conn;
    }
    
    public void close() {
        super.close();
        if (LOG.isDebugEnabled()) {
            LOG.debug("HADataSource closed");
        }
    }
}
