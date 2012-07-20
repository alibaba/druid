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
package com.alibaba.druid.util;

import java.sql.SQLException;
import java.util.List;

import com.alibaba.druid.filter.Filter;
import com.alibaba.druid.filter.FilterManager;
import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;

/**
 * druid loader utils
 * 
 * @author gang.su
 */
public class DruidFilterUtils {

    private final static Log LOG = LogFactory.getLog(DruidFilterUtils.class);

    public static void loadFilter(List<Filter> filters, String filterName) throws SQLException {
        if (filterName.length() == 0) {
            return;
        }

        String filterClassNames = FilterManager.getFilter(filterName);

        if (filterClassNames != null) {
            for (String filterClassName : filterClassNames.split(",")) {
                if (existsFilter(filters, filterClassName)) {
                    continue;
                }

                Class<?> filterClass = loadClass(filterClassName);

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

        Class<?> filterClass = loadClass(filterName);
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

    /**
     * 是否存在load
     * 
     * @param filterList
     * @param filterClassName
     * @return
     */
    private static boolean existsFilter(List<Filter> filterList, String filterClassName) {
        for (Filter filter : filterList) {
            String itemFilterClassName = filter.getClass().getName();
            if (itemFilterClassName.equalsIgnoreCase(filterClassName)) {
                return true;
            }
        }
        return false;
    }

    public static Class<?> loadClass(String className) {
        Class<?> clazz = null;

        if (className == null) {
            return null;
        }

        try {
            clazz = Thread.currentThread().getContextClassLoader().loadClass(className);
        } catch (ClassNotFoundException e) {

        }

        if (clazz != null) {
            return clazz;
        }

        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }
}
