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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.alibaba.druid.pool.PoolablePreparedStatement.PreparedStatementKey;

/**
 * 
 * @author shaojin.wensj
 *
 */
public class PreparedStatementPool {
	private HashMap<PreparedStatementKey, List<PoolablePreparedStatement>> map = new HashMap<PreparedStatementKey, List<PoolablePreparedStatement>>();

	public static enum MethodType {
		M1, M2, M3, M4, M5, M6, Precall_1, Precall_2, Precall_3
	}

	public PoolablePreparedStatement get(PreparedStatementKey key) {
		List<PoolablePreparedStatement> list = map.get(key);

		if (list == null) {
			list = new ArrayList<PoolablePreparedStatement>();
			map.put(key, list);

			return null;
		}

		int size = list.size();

		if (size == 0) {
			return null;
		}

		PoolablePreparedStatement last = list.remove(size - 1);

		return last;
	}

	public void put(PoolablePreparedStatement poolableStatement) {
		PreparedStatementKey key = poolableStatement.getKey();
		List<PoolablePreparedStatement> list = map.get(key);

		if (list == null) {
			list = new ArrayList<PoolablePreparedStatement>();
			map.put(key, list);
		}
		
		list.add(poolableStatement);
	}

}
