/**
 * Copyright 1999-2011 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.alibaba.druid.bvt.proxy.config;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.proxy.config.AbstractDruidFilterConfig;
import com.alibaba.druid.proxy.config.EncodingDruidFilterConfig;
import com.alibaba.druid.proxy.config.DruidFilterConfigLoader;

/**
 * @author gang.su
 */
public class DruidConfigLoaderTest extends TestCase {

    public void testLoadConfig() throws SQLException {
        List<AbstractDruidFilterConfig> druidFilterConfigList = new ArrayList<AbstractDruidFilterConfig>();
        String config = "DruidDriverConfig.xml";
        DruidFilterConfigLoader.loadConfig(config, druidFilterConfigList);
        Assert.assertNotNull(druidFilterConfigList);
        Assert.assertEquals(4, druidFilterConfigList.size());
        for (Iterator<AbstractDruidFilterConfig> iterator = druidFilterConfigList.iterator(); iterator.hasNext();) {
            AbstractDruidFilterConfig abstractDruidFilterConfig = iterator.next();
            if ("encoding".equalsIgnoreCase(abstractDruidFilterConfig.getName())) {
                EncodingDruidFilterConfig druidConfig = (EncodingDruidFilterConfig) abstractDruidFilterConfig;
                Assert.assertEquals("gbk", druidConfig.getClientEncoding());
                Assert.assertEquals("utf8", druidConfig.getServerEncoding());
            }else if("stat".equalsIgnoreCase(abstractDruidFilterConfig.getName())){
                Assert.assertEquals("com.alibaba.druid.proxy.filter.stat.StatFilter", abstractDruidFilterConfig.getClazz());
            }
        }

    }
}
