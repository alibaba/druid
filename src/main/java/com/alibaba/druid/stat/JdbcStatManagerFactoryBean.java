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

import org.springframework.beans.factory.FactoryBean;


public class JdbcStatManagerFactoryBean implements FactoryBean {

	@Override
	public Object getObject() throws Exception {
		return JdbcStatManager.getInstance();
	}

	@Override
	public Class<?> getObjectType() {
		return JdbcStatManager.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

}
