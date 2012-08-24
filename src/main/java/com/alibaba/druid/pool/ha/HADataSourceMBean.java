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
	
	boolean isMasterFail();
	
	boolean isSlaveFail();
	
	int getMasterWeight();
	
	int getSlaveWeight();
	
	int getMasterWeightRegionBegin();
	
	int getMasterWeightRegionEnd();
	
	int getSlaveWeightRegionBegin();
	
	int getSlaveWeightRegionEnd();
}
