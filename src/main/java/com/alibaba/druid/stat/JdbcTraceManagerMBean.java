package com.alibaba.druid.stat;

public interface JdbcTraceManagerMBean {
	long getEventFiredCount();
	
	long getEventSkipCount();
	
	int getEventListenerSize();
	
	boolean isTraceEnable();

	void setTraceEnable(boolean traceEnable);
}
