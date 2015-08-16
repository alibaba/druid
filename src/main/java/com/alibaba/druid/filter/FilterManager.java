/*
 * Copyright 1999-2101 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.filter;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;
import com.alibaba.druid.util.JdbcUtils;
import com.alibaba.druid.util.Utils;

public class FilterManager {

    private final static Log                               LOG      = LogFactory.getLog(FilterManager.class);

    private static final ConcurrentHashMap<String, String> aliasMap = new ConcurrentHashMap<String, String>(16, 0.75f, 1);

    static {
        try {
            Properties filterProperties = loadFilterConfig();
            for (Map.Entry<Object, Object> entry : filterProperties.entrySet()) {
                String key = (String) entry.getKey();
                if (key.startsWith("druid.filters.")) {
                    String name = key.substring("druid.filters.".length());
                    aliasMap.put(name, (String) entry.getValue());
                }
            }
        } catch (Exception e) {
            LOG.error("load filter config error", e);
        }
    }

    public static final String getFilter(String alias) {
        return aliasMap.get(alias);
    }

    public static Properties loadFilterConfig() throws IOException {
        Properties filterProperties = new Properties();

        loadFilterConfig(filterProperties, ClassLoader.getSystemClassLoader());
        loadFilterConfig(filterProperties, FilterManager.class.getClassLoader());
        loadFilterConfig(filterProperties, Thread.currentThread().getContextClassLoader());
        loadFilterConfig(filterProperties, FilterManager.class.getClassLoader());

        return filterProperties;
    }

    private static void loadFilterConfig(Properties filterProperties, ClassLoader classLoader) throws IOException {
        if (classLoader == null) {
            return;
        }
        
        for (Enumeration<URL> e = classLoader.getResources("META-INF/druid-filter.properties"); e.hasMoreElements();) {
            URL url = e.nextElement();

            Properties property = new Properties();

            InputStream is = null;
            try {
                is = url.openStream();
                property.load(is);
            } finally {
                JdbcUtils.close(is);
            }

            filterProperties.putAll(property);
        }
    }

    public static void loadFilter(List<Filter> filters, String filterName) throws SQLException {
        if (filterName.length() == 0) {
            return;
        }

        String filterClassNames = getFilter(filterName);

        if (filterClassNames != null) {
            for (String filterClassName : filterClassNames.split(",")) {
                if (existsFilter(filters, filterClassName)) {
                    continue;
                }

                Class<?> filterClass = Utils.loadClass(filterClassName);

                if (filterClass == null) {
                    LOG.error("load filter error, filter not found : " + filterClassName);
                    continue;
                }

                Filter filter;

                try {
                    filter = (Filter) filterClass.newInstance();
                } catch (InstantiationException e) {
                    throw new SQLException("load managed jdbc driver event listener error. " + filterName, e);
                } catch (IllegalAccessException e) {
                    throw new SQLException("load managed jdbc driver event listener error. " + filterName, e);
                }

                filters.add(filter);
            }

            return;
        }

        if (existsFilter(filters, filterName)) {
            return;
        }

        Class<?> filterClass = Utils.loadClass(filterName);
        if (filterClass == null) {
            LOG.error("load filter error, filter not found : " + filterName);
            return;
        }

        try {
            Filter filter = (Filter) filterClass.newInstance();
            filters.add(filter);
        } catch (Exception e) {
            throw new SQLException("load managed jdbc driver event listener error. " + filterName, e);
        }
    }

    private static boolean existsFilter(List<Filter> filterList, String filterClassName) {
        for (Filter filter : filterList) {
            String itemFilterClassName = filter.getClass().getName();
            if (itemFilterClassName.equalsIgnoreCase(filterClassName)) {
                return true;
            }
        }
        return false;
    }
}
