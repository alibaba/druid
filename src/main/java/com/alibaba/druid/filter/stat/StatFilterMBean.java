/*
 * Copyright 2011 Alibaba Group.
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
package com.alibaba.druid.filter.stat;

import java.util.Date;

import javax.management.JMException;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.TabularData;

/**
 * 
 * @author wenshao<szujobs@hotmail.com>
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
