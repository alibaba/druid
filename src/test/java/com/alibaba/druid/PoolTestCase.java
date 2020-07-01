package com.alibaba.druid;

import com.alibaba.druid.stat.DruidDataSourceStatManager;
import junit.framework.TestCase;
import org.junit.Assert;

import javax.management.openmbean.CompositeData;

public class PoolTestCase extends TestCase {
    protected void setUp() throws Exception {
        DruidDataSourceStatManager.clear();
    }

    protected void tearDown() throws Exception {
        int size = DruidDataSourceStatManager.getInstance().getDataSourceList().size();
        String errorInfo = null;
        if (size > 0) {
            CompositeData compositeData = (CompositeData) DruidDataSourceStatManager.getInstance().getDataSourceList().values().iterator().next();
            String name = (String) compositeData.get("Name");
            String url = (String) compositeData.get("URL");

            errorInfo = "Name " + name + ", URL " + url;
        }
        Assert.assertEquals(errorInfo, 0, size);
    }
}
