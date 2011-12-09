package com.alibaba.druid.pool.ha.config;

public class HADataSourceConfig {

    private DruidDataSourceConfig master;
    private DruidDataSourceConfig slave;

    public DruidDataSourceConfig getMaster() {
        return master;
    }

    public void setMaster(DruidDataSourceConfig master) {
        this.master = master;
    }

    public DruidDataSourceConfig getSlave() {
        return slave;
    }

    public void setSlave(DruidDataSourceConfig slave) {
        this.slave = slave;
    }

}
