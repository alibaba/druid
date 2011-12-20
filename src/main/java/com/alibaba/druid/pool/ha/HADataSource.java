package com.alibaba.druid.pool.ha;

import javax.sql.DataSource;

import com.alibaba.druid.logging.Log;
import com.alibaba.druid.logging.LogFactory;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.ManagedDataSource;

public class HADataSource extends MultiDataSource implements HADataSourceMBean, ManagedDataSource, DataSource {

    private final static Log   LOG = LogFactory.getLog(HADataSource.class);

    protected DataSourceHolder master;
    protected DataSourceHolder slave;

    public HADataSource(){

    }

    public void resetStat() {
        super.resetStat();

        master.resetState();
        slave.resetState();
    }

    public long getMasterConnectCount() {
        return master.getConnectCount();
    }

    public long getSlaveConnectCount() {
        return slave.getConnectCount();
    }

    public DataSourceHolder getMaster() {
        return master;
    }
    
    public void restartMaster() {
        this.restartDataSource("master");
    }
    
    public void restartSlave() {
        this.restartDataSource("slave");
    }

    public void setMaster(DruidDataSource master) {
        this.setMaster(new DataSourceHolder(master));
    }

    public void setMaster(DataSourceHolder master) {
        this.getDataSources().put("master", master);
        this.master = master;
    }

    public DataSourceHolder getSlave() {
        return slave;
    }

    public void setSlave(DruidDataSource slave) {
        this.setSlave(new DataSourceHolder(slave));
    }

    public void setSlave(DataSourceHolder slave) {
        this.getDataSources().put("slave", slave);
        this.slave = slave;
    }

    public boolean isMasterEnable() {
        if (master == null) {
            return false;
        }

        return master.isEnable();
    }
    
    public boolean isMasterFail() {
        if (master == null) {
            return false;
        }
        
        return master.isFail();
    }
    
    public boolean isSlaveFail() {
        if (slave == null) {
            return false;
        }
        
        return slave.isFail();
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
        DataSourceHolder tmp = this.getMaster();
        this.setMaster(this.getSlave());
        this.setSlave(tmp);
    }

    public void close() {
        super.close();
        if (LOG.isDebugEnabled()) {
            LOG.debug("HADataSource closed");
        }
    }
}
