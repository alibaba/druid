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

import java.sql.SQLException;
import java.sql.Wrapper;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.druid.filter.FilterChain;

/**
 * 
 * @author shaojin.wensj
 *
 */
public abstract class WrapperProxyImpl implements WrapperProxy {
	private final Wrapper raw;

	private final long id;

	private final Map<String, Object> attributes = new HashMap<String, Object>(4); // 不需要线程安全

	public WrapperProxyImpl(Wrapper wrapper, long id) {
		this.raw = wrapper;
		this.id = id;
	}

	public long getId() {
		return this.id;
	}

	public Object getRawObject() {
		return raw;
	}

	public abstract FilterChain createChain();

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		if (iface == null) {
			return false;
		}
		
		if (iface.isInstance(this)) {
			return true;
		}
		
		return createChain().isWrapperFor(raw, iface);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		if (iface == null) {
			return null;
		}
		
		if (iface.isInstance(this)) {
			return (T) this;
		}
		
		return createChain().unwrap(raw, iface);
	}

	public Map<String, Object> getAttributes() {
		return this.attributes;
	}

}
