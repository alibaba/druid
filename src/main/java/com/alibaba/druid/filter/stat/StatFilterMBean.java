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
package com.alibaba.druid.filter.stat;

import java.util.Date;

import javax.management.JMException;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.TabularData;

/**
 * 
 * @author shaojin.wensj
 * 
 */
public interface StatFilterMBean {

	void reset();

	boolean isConnectionStackTraceEnable();

	void setConnectionStackTraceEnable(boolean connectionStackTraceEnable);

	String getConnectionUrl();

	long getConnectionConnectCount();

	long getConnectionCloseCount();

	long getConnectionActiveCount();

	long getConnectionActiveCountMax();

	long getConnectionCommitCount();

	long getConnectionRollbackCount();

	long getConnectionConnectMillis();

	long getConnectionConnectAliveMillis();

	long getConnectionConnectErrorCount();

	Date getConnectionConnectLastTime();

	long getStatementCreateCount();

	long getStatementPrepareCount();

	long getStatementPrepareCallCount();

	long getStatementCloseCount();

	long getStatementExecuteMillisTotal();

	long getStatementExecuteSuccessCount();

	long getStatementExecuteErrorCount();

	Date getStatementExecuteErrorLastTime();

	CompositeData getStatementExecuteLastError() throws JMException;

	Date getStatementExecuteLastTime();

	long getResultSetHoldMillisTotal();

	long getResultSetFetchRowCount();

	long getResultSetOpenCount();

	long getResultSetCloseCount();

	TabularData getSqlList() throws JMException;

	TabularData getConnectionList() throws JMException;
}
