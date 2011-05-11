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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

import javax.management.JMException;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.TabularData;
import javax.management.openmbean.TabularDataSupport;
import javax.management.openmbean.TabularType;

import com.alibaba.druid.filter.Filter;
import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.proxy.DruidDriver;
import com.alibaba.druid.proxy.jdbc.DataSourceProxyImpl;

public class JdbcStatManager implements JdbcStatManagerMBean {

	private final AtomicLong sqlIdSeed = new AtomicLong(1000);

	private final static JdbcStatManager instance = new JdbcStatManager();

	private final JdbcConnectionStat connectionStat = new JdbcConnectionStat();
	private final JdbcResultSetStat resultSetStat = new JdbcResultSetStat();
	private final JdbcStatementStat statementStat = new JdbcStatementStat();

	private final ConcurrentMap<String, JdbcDataSourceStat> dataSources = new ConcurrentHashMap<String, JdbcDataSourceStat>();

	private final AtomicLong resetCount = new AtomicLong();

	public final ThreadLocal<JdbcStatContext> contextLocal = new ThreadLocal<JdbcStatContext>();

	private JdbcStatManager() {

	}

	public ConcurrentMap<String, JdbcDataSourceStat> getDataSources() {
		return dataSources;
	}

	public JdbcStatContext getStatContext() {
		return contextLocal.get();
	}

	public void setStatContext(JdbcStatContext context) {
		contextLocal.set(context);
	}

	public JdbcStatContext createStatContext() {
		JdbcStatContext context = new JdbcStatContext();

		context.setTraceEnable(JdbcTraceManager.getInstance().isTraceEnable());

		return context;
	}

	public long generateSqlId() {
		return sqlIdSeed.incrementAndGet();
	}

	public static final JdbcStatManager getInstance() {
		return instance;
	}

	public JdbcStatementStat getStatementStat() {
		return statementStat;
	}

	public JdbcResultSetStat getResultSetStat() {
		return resultSetStat;
	}

	public JdbcConnectionStat getConnectionstat() {
		return connectionStat;
	}

	@Override
	public TabularData getDataSourceList() throws JMException {
		CompositeType rowType = DataSourceProxyImpl.getCompositeType();
		String[] indexNames = rowType.keySet().toArray(new String[rowType.keySet().size()]);

		TabularType tabularType = new TabularType("DataSourceStat", "DataSourceStat", rowType, indexNames);
		TabularData data = new TabularDataSupport(tabularType);

		final ConcurrentMap<String, DataSourceProxyImpl> dataSources = DruidDriver.getDataSources();
		for (DataSourceProxyImpl dataSource : dataSources.values()) {
			data.put(dataSource.getCompositeData());
		}

		return data;
	}

	@Override
	public TabularData getSqlList() throws JMException {
		CompositeType rowType = JdbcSqlStat.getCompositeType();
		String[] indexNames = rowType.keySet().toArray(new String[rowType.keySet().size()]);

		TabularType tabularType = new TabularType("SqlListStatistic", "SqlListStatistic", rowType, indexNames);
		TabularData data = new TabularDataSupport(tabularType);

		for (JdbcDataSourceStat dataSource : dataSources.values()) {
			ConcurrentMap<String, JdbcSqlStat> statMap = dataSource.getSqlStatisticMap();
			for (Map.Entry<String, JdbcSqlStat> entry : statMap.entrySet()) {
				data.put(entry.getValue().getCompositeData());
			}
		}

		return data;
	}

	public TabularData getConnectionList() throws JMException {
		CompositeType rowType = JdbcConnectionStat.Entry.getCompositeType();
		String[] indexNames = rowType.keySet().toArray(new String[rowType.keySet().size()]);

		TabularType tabularType = new TabularType("ConnectionList", "ConnectionList", rowType, indexNames);
		TabularData data = new TabularDataSupport(tabularType);

		final ConcurrentMap<String, DataSourceProxyImpl> dataSources = DruidDriver.getDataSources();
		for (DataSourceProxyImpl dataSource : dataSources.values()) {
			for (Filter filter : dataSource.getConfig().getFilters()) {
				if (filter instanceof StatFilter) {
					StatFilter countFilter = (StatFilter) filter;

					ConcurrentMap<Long, JdbcConnectionStat.Entry> connections = countFilter.getConnections();
					for (Map.Entry<Long, JdbcConnectionStat.Entry> entry : connections.entrySet()) {
						data.put(entry.getValue().getCompositeData());
					}
				}
			}
		}

		return data;
	}

	@Override
	public void reset() {
		resetCount.incrementAndGet();

		connectionStat.reset();
		statementStat.reset();
		resultSetStat.reset();

		final ConcurrentMap<String, DataSourceProxyImpl> dataSources = DruidDriver.getDataSources();
		for (DataSourceProxyImpl dataSource : dataSources.values()) {
			for (Filter filter : dataSource.getConfig().getFilters()) {
				if (filter instanceof StatFilter) {
					StatFilter countFilter = (StatFilter) filter;
					countFilter.reset();
				}
			}
		}
	}

	@Override
	public long getResetCount() {
		return resetCount.get();
	}
}
