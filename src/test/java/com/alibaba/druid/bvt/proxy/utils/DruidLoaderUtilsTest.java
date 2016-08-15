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
package com.alibaba.druid.bvt.proxy.utils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.bvt.proxy.DruidDriverTest.PublicJdbcFilterAdapter;
import com.alibaba.druid.filter.Filter;
import com.alibaba.druid.filter.FilterManager;
import com.alibaba.druid.util.Utils;

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
        FilterManager.loadFilter(filters, filterItem);
        Filter filterConfig = filters.get(0);
        Assert.assertNotNull(filterConfig);
        Assert.assertEquals("com.alibaba.druid.filter.logging.Log4jFilter", filterConfig.getClass().getName());
        // stat
        filterItem = "stat";
        filters.clear();
        FilterManager.loadFilter(filters, filterItem);
        filterConfig = filters.get(0);
        Assert.assertNotNull(filterConfig);
        Assert.assertEquals("com.alibaba.druid.filter.stat.StatFilter", filterConfig.getClass().getName());
        // default
        filterItem = "default";
        filters.clear();
        FilterManager.loadFilter(filters, filterItem);
        filterConfig = filters.get(0);
        Assert.assertNotNull(filterConfig);
        Assert.assertEquals("com.alibaba.druid.filter.stat.StatFilter", filterConfig.getClass().getName());
        // counter
        filterItem = "stat";
        filters.clear();
        FilterManager.loadFilter(filters, filterItem);
        filterConfig = filters.get(0);
        Assert.assertNotNull(filterConfig);
        Assert.assertEquals("com.alibaba.druid.filter.stat.StatFilter", filterConfig.getClass().getName());
        // commonLogging
        filterItem = "commonLogging";
        filters.clear();
        FilterManager.loadFilter(filters, filterItem);
        filterConfig = filters.get(0);
        Assert.assertNotNull(filterConfig);
        Assert.assertEquals("com.alibaba.druid.filter.logging.CommonsLogFilter", filterConfig.getClass().getName());
        // encoding
        filterItem = "encoding";
        filters.clear();
        FilterManager.loadFilter(filters, filterItem);
        filterConfig = filters.get(0);
        Assert.assertNotNull(filterConfig);
        Assert.assertEquals("com.alibaba.druid.filter.encoding.EncodingConvertFilter",
                            filterConfig.getClass().getName());
        // 判定重复
        filterItem = "stat";
        filters.clear();
        FilterManager.loadFilter(filters, filterItem);
        filterItem = "default";
        FilterManager.loadFilter(filters, filterItem);
        for (Iterator<Filter> iterator = filters.iterator(); iterator.hasNext();) {
            Filter filter = (Filter) iterator.next();
            System.out.println(filter.getClass().getName());
        }
        // default
    }

    public void twest_loadClass() throws Exception {
        Assert.assertEquals(null, Utils.loadClass(null));
        Assert.assertEquals(null, Utils.loadClass("xxx"));
        Assert.assertEquals(PublicJdbcFilterAdapter.class,
                            Utils.loadClass(PublicJdbcFilterAdapter.class.getName()));
        Assert.assertNull(Utils.loadClass(null));
        Assert.assertNull(Utils.loadClass(""));
    }

}
