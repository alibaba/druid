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

import java.util.ArrayList;
import java.util.List;

import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;

import com.alibaba.druid.filter.Filter;

/**
 * 
 * @author shaojin.wensj
 * 
 */
public class DataSourceProxyConfig {
	private String rawUrl;
	private String url;
	private String rawDriverClassName;
	private String name;

	private PasswordCallback passwordCallback;
	private NameCallback userCallback;
	private final List<Filter> filters = new ArrayList<Filter>();

	public DataSourceProxyConfig() {
	}

	public PasswordCallback getPasswordCallback() {
		return passwordCallback;
	}

	public void setPasswordCallback(PasswordCallback passwordCallback) {
		this.passwordCallback = passwordCallback;
	}

	public NameCallback getUserCallback() {
		return userCallback;
	}

	public void setUserCallback(NameCallback userCallback) {
		this.userCallback = userCallback;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Filter> getFilters() {
		return filters;
	}

	public String getRawUrl() {
		return rawUrl;
	}

	public void setRawUrl(String rawUrl) {
		this.rawUrl = rawUrl;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getRawDriverClassName() {
		return rawDriverClassName;
	}

	public void setRawDriverClassName(String driverClassName) {
		this.rawDriverClassName = driverClassName;
	}
}
