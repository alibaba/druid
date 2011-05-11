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

import java.util.Date;

/**
 * 
 * @author shaojin.wensj
 *
 */
public interface DataSourceProxyImplMBean {
	String getName();
	
	String getUrl();

	String getRawUrl();

	Date getCreatedTime();

	String getRawDriverClassName();

	String[] getFilterClasses();
	
	int getRawDriverMajorVersion();
	
	int getRawDriverMinorVersion();
	
	String getProperties();
}
