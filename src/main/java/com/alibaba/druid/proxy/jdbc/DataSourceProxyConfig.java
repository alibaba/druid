/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
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

import java.util.ArrayList;
import java.util.List;

import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;

import com.alibaba.druid.filter.Filter;

/**
 * @author wenshao [szujobs@hotmail.com]
 */
public class DataSourceProxyConfig {

    private String             rawUrl;
    private String             url;
    private String             rawDriverClassName;
    private String             name;
    private boolean            jmx;

    private PasswordCallback   passwordCallback;
    private NameCallback       userCallback;
    private final List<Filter> filters = new ArrayList<Filter>();

    public DataSourceProxyConfig(){
    }

    public boolean isJmxOption() {
        return jmx;
    }

    public void setJmxOption(boolean jmx) {
        this.jmx = jmx;
    }

    public void setJmxOption(String jmx) {
        this.jmx = Boolean.parseBoolean(jmx);
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
