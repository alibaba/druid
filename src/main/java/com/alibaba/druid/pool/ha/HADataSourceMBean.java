package com.alibaba.druid.pool.ha;

public interface HADataSourceMBean {
	long getMasterConnectCount();

	long getSlaveConnectCount();

	boolean isMasterEnable();

	void setMasterEnable(boolean value);

	boolean isSlaveEnable();

	void setSlaveEnable(boolean value);

	void switchMasterSlave();

	void resetStat();
}
