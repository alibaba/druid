/*
 * Copyright 1999-2011 Alibaba Group Holding Ltd.
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

import java.sql.Driver;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.management.JMException;
import javax.management.openmbean.ArrayType;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenType;
import javax.management.openmbean.SimpleType;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;

import com.alibaba.druid.filter.Filter;
import com.alibaba.druid.filter.FilterChain;
import com.alibaba.druid.filter.FilterChainImpl;

/**
 * @author wenshao<szujobs@hotmail.com>
 */
public class DataSourceProxyImpl implements DataSourceProxy, DataSourceProxyImplMBean {

    private final Driver                rawDriver;

    private final DataSourceProxyConfig config;

    private long                        id;

    private final long                  createdTimeMillis = System.currentTimeMillis();

    private Properties                  properties;

    public DataSourceProxyImpl(Driver rawDriver, DataSourceProxyConfig config){
        super();
        this.rawDriver = rawDriver;
        this.config = config;
    }

    public Driver getRawDriver() {
        return this.rawDriver;
    }

    public String getRawUrl() {
        return config.getRawUrl();
    }

    public ConnectionProxy connect(Properties info) throws SQLException {
        this.properties = info;

        PasswordCallback passwordCallback = this.config.getPasswordCallback();

        if (passwordCallback != null) {
            char[] chars = passwordCallback.getPassword();
            String password = new String(chars);
            info.put("password", password);
        }

        NameCallback userCallback = this.config.getUserCallback();
        if (userCallback != null) {
            String user = userCallback.getName();
            info.put("user", user);
        }

        FilterChain chain = new FilterChainImpl(this);
        return chain.connection_connect(info);
    }

    public DataSourceProxyConfig getConfig() {
        return config;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return this.config.getName();
    }

    @Override
    public String getUrl() {
        return config.getUrl();
    }

    public List<Filter> getFilters() {
        return config.getFilters();
    }

    @Override
    public String[] getFilterClasses() {
        List<Filter> filterConfigList = config.getFilters();

        List<String> classes = new ArrayList<String>();
        for (Filter filter : filterConfigList) {
            classes.add(filter.getClass().getName());
        }

        return classes.toArray(new String[classes.size()]);
    }

    @Override
    public String getRawDriverClassName() {
        return config.getRawDriverClassName();
    }

    @Override
    public Date getCreatedTime() {
        return new Date(createdTimeMillis);
    }

    @Override
    public int getRawDriverMajorVersion() {
        return rawDriver.getMajorVersion();
    }

    @Override
    public int getRawDriverMinorVersion() {
        return rawDriver.getMinorVersion();
    }

    public String getDataSourceMBeanDomain() {
        String name = this.config.getName();
        if (name != null && name.length() != 0) {
            return name;
        }

        return "java.sql.dataSource_" + System.identityHashCode(this);
    }

    public String getProperties() {
        if (properties == null) {
            return null;
        }

        return properties.toString();
    }

    private static CompositeType COMPOSITE_TYPE = null;

    public static CompositeType getCompositeType() throws JMException {

        if (COMPOSITE_TYPE != null) {
            return COMPOSITE_TYPE;
        }

        OpenType<?>[] indexTypes = new OpenType<?>[] { SimpleType.LONG, SimpleType.STRING, SimpleType.STRING,
                new ArrayType<SimpleType<String>>(SimpleType.STRING, false), SimpleType.DATE,
                //
                SimpleType.STRING, SimpleType.STRING, SimpleType.INTEGER, SimpleType.INTEGER, SimpleType.STRING };

        String[] indexNames = { "ID", "URL", "Name", "FilterClasses", "CreatedTime", "RawUrl", "RawDriverClassName",
                "RawDriverMajorVersion", "RawDriverMinorVersion", "Properties" };
        String[] indexDescriptions = indexNames;
        COMPOSITE_TYPE = new CompositeType("SqlStatistic", "Sql Statistic", indexNames, indexDescriptions, indexTypes);

        return COMPOSITE_TYPE;
    }

    public CompositeDataSupport getCompositeData() throws JMException {
        Map<String, Object> map = new HashMap<String, Object>();

        map.put("ID", id);
        map.put("URL", this.getUrl());
        map.put("Name", this.getName());
        map.put("FilterClasses", getFilterClasses());
        map.put("CreatedTime", getCreatedTime());

        map.put("RawDriverClassName", getRawDriverClassName());
        map.put("RawUrl", getRawUrl());
        map.put("RawDriverMajorVersion", getRawDriverMajorVersion());
        map.put("RawDriverMinorVersion", getRawDriverMinorVersion());
        map.put("Properties", getProperties());

        return new CompositeDataSupport(getCompositeType(), map);
    }

    @Override
    public String getRawJdbcUrl() {
        return config.getRawUrl();
    }

}
