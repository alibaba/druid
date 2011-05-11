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

/**
 * 
 * @author shaojin.wensj
 *
 */
public class JdbcParameter {
	private final int sqlType;
	private final Object[] values;

	public JdbcParameter(int sqlType, Object[] values) {
		this.sqlType = sqlType;
		this.values = values;
	}

	public int getSqlType() {
		return sqlType;
	}

	public Object[] getValues() {
		return values;
	}

	public static interface TYPE {
		public static final int BinaryInputStream = 10001;
		public static final int AsciiInputStream = 10002;
		public static final int CharacterInputStream = 10003;
		public static final int NCharacterInputStream = 10004;
		public static final int URL = 10005;

	}
}