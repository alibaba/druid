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

import java.util.Date;

import javax.management.JMException;
import javax.management.openmbean.CompositeData;

public interface JdbcStatementStatMBean {
	long getCreateCount();

	long getPrepareCount();

	long getPrepareCallCount();

	long getCloseCount();

	long getExecuteMillisTotal();

	long getExecuteSuccessCount();

	Date getLastErrorTime();

	CompositeData getLastError() throws JMException;

	Date getExecuteLastTime();
	
	int getRunningCount();
	
	int getConcurrentMax();
	
	long getExecuteCount();
	
	long getErrorCount();
}
