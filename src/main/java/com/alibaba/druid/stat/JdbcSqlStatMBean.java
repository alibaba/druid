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

/**
 * 
 * @author shaojin.wensj
 *
 */
public interface JdbcSqlStatMBean {
	String getSql();

	Date getExecuteLastStartTime();

	Date getExecuteNanoSpanMaxOccurTime();

	Date getExecuteErrorLastTime();

	long getExecuteBatchSizeTotal();

	long getExecuteBatchSizeMax();

	long getExecuteSuccessCount();

	long getExecuteMillisTotal();

	long getExecuteMillisMax();

	long getErrorCount();
	
	long getConcurrentMax();
	
	long getRunningCount();
	
	String getName();
	
	String getFile();

	void reset();
	
	long getFetchRowCount();
	
	long getUpdateCount();
	
	long getExecuteCount();
	
	long getId();
}