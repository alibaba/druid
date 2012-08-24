/*
 * Copyright 1999-2011 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.druid.pool.ha;

import javax.sql.DataSource;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.ManagedDataSource;
import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;

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

    public boolean isMasterWritable() {
        return master != null && master.isWritable();
    }

    public boolean isSlaveWritable() {
        return slave != null && slave.isWritable();
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

    public DataSourceHolder setMaster(DruidDataSource master) {
        DataSourceHolder holder = new DataSourceHolder(this, master);
        this.setMaster(holder);
        return holder;
    }

    public void setMaster(DataSourceHolder master) {
        this.addDataSource("master", master);
        this.master = master;
    }

    public DataSourceHolder getSlave() {
        return slave;
    }

    public DataSourceHolder setSlave(DruidDataSource slave) {
        DataSourceHolder holder = new DataSourceHolder(this, slave);
        this.setSlave(holder);
        return holder;
    }

    public void setSlave(DataSourceHolder slave) {
        this.addDataSource("slave", slave);
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

    public int getMasterWeight() {
        return master.getWeight();
    }

    public int getSlaveWeight() {
        return slave.getWeight();
    }

    public int getMasterWeightRegionBegin() {
        return master.getWeightRegionBegin();
    }

    public int getMasterWeightRegionEnd() {
        return master.getWeightRegionEnd();
    }

    public int getSlaveWeightRegionBegin() {
        return slave.getWeightRegionBegin();
    }

    public int getSlaveWeightRegionEnd() {
        return slave.getWeightRegionEnd();
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
