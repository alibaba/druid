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
package com.alibaba.druid.proxy.jdbc;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.sql.Clob;
import java.sql.SQLException;

import com.alibaba.druid.filter.FilterChain;
import com.alibaba.druid.filter.FilterChainImpl;

/**
 * 
 * @author shaojin.wensj
 *
 */
public class ClobProxyImpl implements ClobProxy {
	private final Clob clob;
	private final ConnectionProxy connection;
	
	private final DataSourceProxy dataSource;

	public ClobProxyImpl(DataSourceProxy dataSource, ConnectionProxy connection, Clob clob) {
		this.dataSource = dataSource;
		this.connection = connection;
		this.clob = clob;
	}
	
	public FilterChain createChain() {
		return new FilterChainImpl(dataSource);
	}
	
	public ConnectionProxy getConnectionWrapper() {
		return this.connection;
	}

	@Override
	public Clob getRawClob() {
		return clob;
	}

	@Override
	public void free() throws SQLException {
		createChain().clob_free(this);
	}

	@Override
	public InputStream getAsciiStream() throws SQLException {
		return createChain().clob_getAsciiStream(this);
	}

	@Override
	public Reader getCharacterStream() throws SQLException {
		return createChain().clob_getCharacterStream(this);
	}

	@Override
	public Reader getCharacterStream(long pos, long length) throws SQLException {
		return createChain().clob_getCharacterStream(this, pos, length);
	}

	@Override
	public String getSubString(long pos, int length) throws SQLException {
		return createChain().clob_getSubString(this, pos, length);
	}

	@Override
	public long length() throws SQLException {
		return createChain().clob_length(this);
	}

	@Override
	public long position(String searchstr, long start) throws SQLException {
		return createChain().clob_position(this, searchstr, start);
	}

	@Override
	public long position(Clob searchstr, long start) throws SQLException {
		return createChain().clob_position(this, searchstr, start);
	}

	@Override
	public OutputStream setAsciiStream(long pos) throws SQLException {
		return createChain().clob_setAsciiStream(this, pos);
	}

	@Override
	public Writer setCharacterStream(long pos) throws SQLException {
		return createChain().clob_setCharacterStream(this, pos);
	}

	@Override
	public int setString(long pos, String str) throws SQLException {
		return createChain().clob_setString(this, pos, str);
	}

	@Override
	public int setString(long pos, String str, int offset, int len)
			throws SQLException {
		return createChain().clob_setString(this, pos, str, offset, len);
	}

	@Override
	public void truncate(long len) throws SQLException {
		createChain().clob_truncate(this, len);
	}

}
