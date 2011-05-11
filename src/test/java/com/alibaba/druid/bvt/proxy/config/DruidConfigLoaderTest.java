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
