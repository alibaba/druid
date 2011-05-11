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

import java.sql.PreparedStatement;

/**
 * 
 * @author shaojin.wensj
 *
 */
public final class PreparedStatementHolder<T extends PreparedStatement> {
	private final T statement;

	public PreparedStatementHolder(T stmt) {
		super();
		this.statement = stmt;
	}

	public T getStatement() {
		return statement;
	}

}
