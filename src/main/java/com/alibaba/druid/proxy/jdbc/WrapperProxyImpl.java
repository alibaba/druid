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
