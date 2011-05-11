/**
 * Project: druid
 * 
 * File Created at 2011-2-24
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
package com.alibaba.druid.pool;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.sql.ConnectionEventListener;
import javax.sql.StatementEventListener;

/**
 * 
 * @author shaojin.wensj
 *
 */
public final class ConnectionHolder {
	private final DruidDataSource dataSource;
	private final Connection conn;
	private final List<ConnectionEventListener> connectionEventListeners = new CopyOnWriteArrayList<ConnectionEventListener>();
	private final List<StatementEventListener> statementEventListeners = new CopyOnWriteArrayList<StatementEventListener>();
	private final long timeMillis = System.currentTimeMillis();
	private long useCount = 0;

	private final boolean poolPreparedStatements;
	private final PreparedStatementPool statementPool;
	
	private final List<PoolableStatement> statementTrace = new ArrayList<PoolableStatement>();

	public ConnectionHolder(DruidDataSource dataSource, Connection conn) {
		this.dataSource = dataSource;
		this.conn = conn;
		this.poolPreparedStatements = dataSource.isPoolPreparedStatements();

		if (this.poolPreparedStatements) {
			statementPool = new PreparedStatementPool();
		} else {
			statementPool = null;
		}
	}
	
	public void addTrace(PoolableStatement stmt) {
		statementTrace.add(stmt);
	}
	
	public void removeTrace(PoolableStatement stmt) {
		statementTrace.remove(stmt);
	}
	
	public List<ConnectionEventListener> getConnectionEventListeners() {
		return connectionEventListeners;
	}

	public List<StatementEventListener> getStatementEventListeners() {
		return statementEventListeners;
	}
	
	public PreparedStatementPool getStatementPool() {
		return statementPool;
	}

	public DruidDataSource getDataSource() {
		return dataSource;
	}

	public boolean isPoolPreparedStatements() {
		return poolPreparedStatements;
	}

	public Connection getConnection() {
		return conn;
	}

	public long getTimeMillis() {
		return timeMillis;
	}

	public long getUseCount() {
		return useCount;
	}

	public void incrementUseCount() {
		useCount++;
	}
	
	public void reset() {
		connectionEventListeners.clear();
		statementEventListeners.clear();
		
		for (Statement stmt : statementTrace) {
			try {
				if (!stmt.isClosed()) {
					stmt.close();
				}
			} catch (SQLException ex) {
				ex.printStackTrace(); //
			}
		}
		statementTrace.clear();
	}
}
