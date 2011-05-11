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
 * @author wenshao<szujobs@hotmail.com>
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
