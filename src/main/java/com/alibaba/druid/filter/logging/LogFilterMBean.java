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
package com.alibaba.druid.filter.logging;

/**
 * 
 * @author shaojin.wensj
 *
 */
public interface LogFilterMBean {
	String getDataSourceLoggerName();

	void setDataSourceLoggerName(String loggerName);
	
	boolean isDataSourceLogEnabled();

	void setDataSourceLogEnabled(boolean dataSourceLogEnabled);

	// //////////////

	String getConnectionLoggerName();

	void setConnectionLoggerName(String loggerName);
	
	boolean isConnectionLogEnabled();

	void setConnectionLogEnabled(boolean connectionLogEnabled);

	boolean isConnectionLogErrorEnabled();

	void setConnectionLogErrorEnabled(boolean connectionLogErrorEnabled);

	boolean isConnectionConnectBeforeLogEnabled();

	void setConnectionConnectBeforeLogEnabled(boolean beforeConnectionConnectLogEnable);
	
	boolean isConnectionConnectAfterLogEnabled();

	void setConnectionConnectAfterLogEnabled(boolean afterConnectionConnectLogEnable);

	boolean isConnectionCloseAfterLogEnabled();

	void setConnectionCloseAfterLogEnabled(boolean afterConnectionCloseLogEnable);

	boolean isConnectionCommitAfterLogEnabled();

	void setConnectionCommitAfterLogEnabled(boolean afterConnectionCommitLogEnable);



	// ////////////

	String getStatementLoggerName();

	void setStatementLoggerName(String loggerName);

	boolean isStatementLogEnabled();

	void setStatementLogEnabled(boolean statementLogEnabled);

	boolean isStatementCloseAfterLogEnabled();

	void setStatementCloseAfterLogEnabled(boolean afterStatementCloseLogEnable);

	boolean isStatementCreateAfterLogEnabled();

	void setStatementCreateAfterLogEnabled(boolean afterStatementCreateLogEnable);

	boolean isStatementExecuteBatchAfterLogEnabled();

	void setStatementExecuteBatchAfterLogEnabled(boolean afterStatementExecuteBatchLogEnable);

	boolean isStatementExecuteAfterLogEnabled();

	void setStatementExecuteAfterLogEnabled(boolean afterStatementExecuteLogEnable);

	boolean isStatementExecuteQueryAfterLogEnabled();

	void setStatementExecuteQueryAfterLogEnabled(boolean afterStatementExecuteQueryLogEnable);

	boolean isStatementExecuteUpdateAfterLogEnabled();

	void setStatementExecuteUpdateAfterLogEnabled(boolean afterStatementExecuteUpdateLogEnable);

	boolean isStatementPrepareCallAfterLogEnabled();

	void setStatementPrepareCallAfterLogEnabled(boolean afterStatementPrepareCallLogEnable);

	boolean isStatementPrepareAfterLogEnabled();

	void setStatementPrepareAfterLogEnabled(boolean afterStatementPrepareLogEnable);

	boolean isStatementLogErrorEnabled();

	void setStatementLogErrorEnabled(boolean statementLogErrorEnabled);

	boolean isStatementParameterSetLogEnabled();

	void setStatementParameterSetLogEnabled(boolean statementParameterSetLogEnable);

	// //////////////

	String getResultSetLoggerName();

	void setResultSetLoggerName(String loggerName);
	
	boolean isResultSetLogEnabled();

	void setResultSetLogEnabled(boolean resultSetLogEnabled);

	boolean isResultSetNextAfterLogEnabled();

	void setResultSetNextAfterLogEnabled(boolean afterResultSetNextLogEnable);

	boolean isResultSetOpenAfterLogEnabled();

	void setResultSetOpenAfterLogEnabled(boolean afterResultSetOpenLogEnable);

	boolean isResultSetLogErrorEnabled();

	void setResultSetLogErrorEnabled(boolean resultSetLogErrorEnabled);

	boolean isResultSetCloseAfterLogEnabled();

	void setResultSetCloseAfterLogEnabled(boolean resultSetCloseAfterLogEnable);
}
