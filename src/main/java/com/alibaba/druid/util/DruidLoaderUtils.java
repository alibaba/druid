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
import java.util.Iterator;
import java.util.List;

import com.alibaba.druid.filter.Filter;
import com.alibaba.druid.filter.FilterManager;
import com.alibaba.druid.proxy.config.AbstractDruidFilterConfig;

/**
 * druid loader utils
 * 
 * @author gang.su
 */
public class DruidLoaderUtils {

    public static void loadFilter(List<Filter> filterList, List<AbstractDruidFilterConfig> druidFilterConfigList) throws SQLException {
        for (Iterator<AbstractDruidFilterConfig> iterator = druidFilterConfigList.iterator(); iterator.hasNext();) {
            AbstractDruidFilterConfig druidFilterConfig = iterator.next();
            String filterItem = druidFilterConfig.getClazz();

            Class<?> filterClass = loadClass(druidFilterConfig.getClazz());

            if (filterClass == null) {
                return;
            }

            Filter jdbcFilter;
            try {
                jdbcFilter = (Filter) filterClass.newInstance();
                jdbcFilter.loadConfig(druidFilterConfig);
            } catch (InstantiationException e) {
                throw new SQLException("load managed jdbc driver event listener error. " + filterItem, e);
            } catch (IllegalAccessException e) {
                throw new SQLException("load managed jdbc driver event listener error. " + filterItem, e);
            }
            filterList.add(jdbcFilter);
        }

    }

    public static void loadFilter(List<Filter> filters, String filterName) throws SQLException {
        if (filterName.length() == 0) {
            return;
        }

        String filterClassNames = FilterManager.getFilter(filterName);
        if (filterClassNames != null) {
            filterClassNames = filterClassNames.trim();
        }

        if (filterClassNames != null) {
            for (String filterClassName : filterClassNames.split(",")) {

                if (!isExist(filters, filterClassName)) {
                    Class<?> filterClass = loadClass(filterClassName);

                    if (filterClass != null) {
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
                }
            }
        } else {
            if (!isExist(filters, filterName)) {
                Class<?> filterClass = loadClass(filterName);

                if (filterClass != null) {
                    try {
                        Filter filter = (Filter) filterClass.newInstance();
                        filters.add(filter);
                    } catch (InstantiationException e) {
                        throw new SQLException("load managed jdbc driver event listener error. " + filterName, e);
                    } catch (IllegalAccessException e) {
                        throw new SQLException("load managed jdbc driver event listener error. " + filterName, e);
                    }
                }
            }
        }
    }

    /**
     * 是否存在load
     * 
     * @param filterConfigList
     * @param filterClassName
     * @return
     */
    private static boolean isExist(List<Filter> filterConfigList, String filterClassName) {
        for (Iterator<Filter> iterator = filterConfigList.iterator(); iterator.hasNext();) {
            Filter filter = (Filter) iterator.next();
            if (filter.getClass().getName().equalsIgnoreCase(filterClassName)) {
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
            e.printStackTrace();
        }

        if (clazz == null) {
            loadClass2(className);
        }

        return clazz;
    }

    public static Class<?> loadClass2(String className) {
        if (className == null) {
            return null;
        }

        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

}
