/*
 * Copyright 2011 Alibaba Group.
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
package com.alibaba.druid.bvt.proxy.utils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.bvt.proxy.DruidDriverTest.PublicJdbcFilterAdapter;
import com.alibaba.druid.filter.Filter;
import com.alibaba.druid.proxy.config.AbstractDruidFilterConfig;
import com.alibaba.druid.proxy.config.DruidFilterConfig;
import com.alibaba.druid.util.DruidLoaderUtils;

/**
 * druidLoader util 测试
 * 
 * @author gang.su
 */
public class DruidLoaderUtilsTest extends TestCase {

    public void testLoadFilter() throws SQLException {

        List<Filter> filters = new ArrayList<Filter>();
        // log4j
        String filterItem = "log4j";
        DruidLoaderUtils.loadFilter(filters, filterItem);
        Filter filterConfig = filters.get(0);
        Assert.assertNotNull(filterConfig);
        Assert.assertEquals("com.alibaba.druid.filter.logging.Log4jFilter", filterConfig.getClass().getName());
        // stat
        filterItem = "stat";
        filters.clear();
        DruidLoaderUtils.loadFilter(filters, filterItem);
        filterConfig = filters.get(0);
        Assert.assertNotNull(filterConfig);
        Assert.assertEquals("com.alibaba.druid.filter.stat.StatFilter", filterConfig.getClass().getName());
        // default
        filterItem = "default";
        filters.clear();
        DruidLoaderUtils.loadFilter(filters, filterItem);
        filterConfig = filters.get(0);
        Assert.assertNotNull(filterConfig);
        Assert.assertEquals("com.alibaba.druid.filter.stat.StatFilter", filterConfig.getClass().getName());
        // counter
        filterItem = "stat";
        filters.clear();
        DruidLoaderUtils.loadFilter(filters, filterItem);
        filterConfig = filters.get(0);
        Assert.assertNotNull(filterConfig);
        Assert.assertEquals("com.alibaba.druid.filter.stat.StatFilter", filterConfig.getClass().getName());
        // commonLogging
        filterItem = "commonLogging";
        filters.clear();
        DruidLoaderUtils.loadFilter(filters, filterItem);
        filterConfig = filters.get(0);
        Assert.assertNotNull(filterConfig);
        Assert.assertEquals("com.alibaba.druid.filter.logging.CommonsLogFilter", filterConfig.getClass().getName());
        // encoding
        filterItem = "encoding";
        filters.clear();
        DruidLoaderUtils.loadFilter(filters, filterItem);
        filterConfig = filters.get(0);
        Assert.assertNotNull(filterConfig);
        Assert.assertEquals("com.alibaba.druid.filter.encoding.EncodingConvertFilter",
                            filterConfig.getClass().getName());
        // 判定重复
        filterItem = "stat";
        filters.clear();
        DruidLoaderUtils.loadFilter(filters, filterItem);
        filterItem = "default";
        DruidLoaderUtils.loadFilter(filters, filterItem);
        for (Iterator<Filter> iterator = filters.iterator(); iterator.hasNext();) {
            Filter filter = (Filter) iterator.next();
            System.out.println(filter.getClass().getName());
        }
        // default
    }

    public void testLoadFilter2() throws SQLException {

        List<Filter> filterConfigList = new ArrayList<Filter>();
        List<AbstractDruidFilterConfig> druidFilterConfigList = new ArrayList<AbstractDruidFilterConfig>();
        DruidFilterConfig druidFilterConfig = new DruidFilterConfig();
        druidFilterConfig.setClazz("com.alibaba.druid.filter.logging.Log4jFilter");
        druidFilterConfig.setName("log4j");
        druidFilterConfigList.add(druidFilterConfig);
        // log4j
        DruidLoaderUtils.loadFilter(filterConfigList, druidFilterConfigList);
        Filter filterConfig = filterConfigList.get(0);
        Assert.assertNotNull(filterConfig);
        Assert.assertEquals("com.alibaba.druid.filter.logging.Log4jFilter", filterConfig.getClass().getName());

    }

    public void twest_loadClass() throws Exception {
        Assert.assertEquals(null, DruidLoaderUtils.loadClass(null));
        Assert.assertEquals(null, DruidLoaderUtils.loadClass2(null));
        Assert.assertEquals(null, DruidLoaderUtils.loadClass("xxx"));
        Assert.assertEquals(null, DruidLoaderUtils.loadClass2("xxx"));
        Assert.assertEquals(PublicJdbcFilterAdapter.class,
                            DruidLoaderUtils.loadClass(PublicJdbcFilterAdapter.class.getName()));
        Assert.assertEquals(PublicJdbcFilterAdapter.class,
                            DruidLoaderUtils.loadClass2(PublicJdbcFilterAdapter.class.getName()));
        Assert.assertNull(DruidLoaderUtils.loadClass(null));
        Assert.assertNull(DruidLoaderUtils.loadClass(""));
    }

}
