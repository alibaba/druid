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

import com.alibaba.druid.wall.WallProvider;
import com.alibaba.druid.wall.spi.MySqlWallProvider;

/**
 * SQLServerWallTest
 * 
 * @author RaymondXiu
 * @version 1.0, 2012-3-18
 * @see
 */
public class MySqlWallTest42 extends TestCase {

    public void test_false() throws Exception {
        WallProvider provider = new MySqlWallProvider();

        Assert.assertTrue(provider.checkValid(//
        "SELECT COUNT(1) AS count FROM `team` " + //
        "WHERE `team_type` = 'normal'  AND `city_id` IN (0,10) " + //
        "AND (begin_time <= '1364832000')AND (end_time > '1364873430')AND (( title like '%%' ))AND (( `team_price` > '151' AND `team_price` <= '200'))AND ((now_number >= min_number)OR (end_time > '1364832000'))AND (group_id = 9 OR sub_id = 9)")); //

        Assert.assertEquals(1, provider.getTableStats().size());
    }
}
