package com.alibaba.druid.pool.ha;

public interface HADataSourceMBean extends MultiDataSourceMBean {
	long getMasterConnectCount();

	long getSlaveConnectCount();

	boolean isMasterEnable();

	void setMasterEnable(boolean value);

	boolean isSlaveEnable();

	void setSlaveEnable(boolean value);
	
	String getMasterUrl();
	
	String getSlaveUrl();

	void switchMasterSlave();

	void resetStat();
	
	void restartMaster();
	
	void restartSlave();
}
