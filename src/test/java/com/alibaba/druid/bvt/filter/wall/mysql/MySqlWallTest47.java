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
public class MySqlWallTest47 extends TestCase {

    public void test_true() throws Exception {
        WallProvider provider = new MySqlWallProvider();

        Assert.assertTrue(provider.checkValid(//
        "update Fans," + //
                " (select 361659 as ToID, 5 as Score " + //
                "   union all select 382885 as ToID, 2 as Score" + //
                "   union all select 407537 as ToID, 6 as Score) temp  " + //
                "set Fans.score = Fans.score+temp.Score " + //
                "where Fans.FansID = 382885 and Fans.UserID = temp.ToID"));

        Assert.assertEquals(1, provider.getTableStats().size());
    }
}
