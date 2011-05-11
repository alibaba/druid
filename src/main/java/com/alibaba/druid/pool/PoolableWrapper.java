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

import java.sql.SQLException;
import java.sql.Wrapper;

/**
 * 
 * @author shaojin.wensj
 *
 */
public class PoolableWrapper implements Wrapper {

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		if (iface == null) {
			return false;
		}
		
		if (iface.isInstance(this)) {
			return true;
		}
		
		return false;
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
		
		return null;
	}

}
