/**
 * Project: druid
 * 
 * File Created at 2010-12-2
 * $Id$
 * 
 * Copyright 1999-2100 Alibaba.com Corporation Limited.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Alibaba Company. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Alibaba.com.
 */
package com.alibaba.druid.stat;

import javax.management.JMException;
import javax.management.openmbean.TabularData;

public interface JdbcDataSourceStatMBean {
	void reset();

	TabularData getSqlList() throws JMException;

	TabularData getConnectionList() throws JMException;

	String getConnectionUrl();
	
	long getConnectionActiveCount();
	
	long getConnectionConnectAliveMillis();
}
