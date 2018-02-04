/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.bvt.filter.wall.mysql;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.wall.WallProvider;
import com.alibaba.druid.wall.spi.MySqlWallProvider;

/**
 * SQLServerWallTest
 * 
 * @author RaymondXiu
 * @version 1.0, 2012-3-18
 * @see
 */
public class MySqlWallTest43 extends TestCase {

    public void test_false() throws Exception {
        WallProvider provider = new MySqlWallProvider();

        Assert.assertFalse(provider.checkValid(//
        "SELECT COUNT(1) AS count FROM `team` " + //
                "WHERE `team_type` = 'normal' AND 1 = 1 AND `city_id` IN (0,10)"));

        Assert.assertEquals(1, provider.getTableStats().size());
    }

    public void test_true() throws Exception {
        WallProvider provider = new MySqlWallProvider();
        provider.getConfig().setConditionAndAlwayTrueAllow(true);

        Assert.assertTrue(provider.checkValid(//
        "SELECT COUNT(1) AS count FROM `team` " + //
                "WHERE `team_type` = 'normal' AND 1 = 1 AND `city_id` IN (0,10)"));

        Assert.assertEquals(1, provider.getTableStats().size());
    }

    public void test_false2() throws Exception {
        WallProvider provider = new MySqlWallProvider();

        Assert.assertFalse(provider.checkValid(//
        "SELECT COUNT(1) AS count FROM `team` " + //
                "WHERE `team_type` = 'normal' AND 1 = 2 AND `city_id` IN (0,10)"));

        Assert.assertEquals(1, provider.getTableStats().size());
    }

    public void test_true2() throws Exception {
        WallProvider provider = new MySqlWallProvider();
        provider.getConfig().setConditionAndAlwayFalseAllow(true);

        Assert.assertTrue(provider.checkValid(//
        "SELECT COUNT(1) AS count FROM `team` " + //
                "WHERE `team_type` = 'normal' AND 1 = 2 AND `city_id` IN (0,10)"));

        Assert.assertEquals(1, provider.getTableStats().size());
    }
}
