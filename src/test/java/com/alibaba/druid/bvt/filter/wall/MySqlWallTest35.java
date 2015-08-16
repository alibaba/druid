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
package com.alibaba.druid.bvt.filter.wall;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.support.json.JSONUtils;
import com.alibaba.druid.wall.WallProvider;
import com.alibaba.druid.wall.spi.MySqlWallProvider;

/**
 * SQLServerWallTest
 * 
 * @author RaymondXiu
 * @version 1.0, 2012-3-18
 * @see
 */
public class MySqlWallTest35 extends TestCase {

    public void test_true() throws Exception {
        WallProvider provider = new MySqlWallProvider();

        Assert.assertTrue(provider.checkValid(//
        "REPLACE INTO `test2tb` VALUES " + //
                "('51', '6714105741', '亦美珊不掉正品隐形文胸安全硅胶文胸 小胸聚拢比基尼<span class=H>游泳</span>衣泳装', 'http://img03.taobaocdn.com/bao/uploaded/i3/16011019585534199/T1cnlaXA8bXXXXXXXX_!!0-item_pic.jpg', 'liqiang198163', 'c', '25.00', '20.00', '1000.00', '76', '529', '4.86', '4.91', '4.90', '0.00', '4.80', '2878', '77', '12', '15');")); //

        Assert.assertEquals(1, provider.getTableStat("test2tb").getReplaceCount());
        System.out.println(JSONUtils.toJSONString(provider.getStatsMap()));
    }
}
