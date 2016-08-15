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
public class MySqlWallTest74 extends TestCase {

    public void test_false() throws Exception {
        WallProvider provider = new MySqlWallProvider();
        
        provider.getConfig().setCommentAllow(true);

        Assert.assertTrue(provider.checkValid(//
        "select _t0.`ownUser` as _c0, _t0.`showTime` as _c1, _t0.`showType` as _c2, " + //
        "   _t0.`itemId` as _c3, _t0.`queueId` as _c4 " + //
        "from `itemshow_queue` as _t0 " + //
        "where ( _t0.`isShowed` = 'F' and _t0.`showTime` <= ? ) " + //
        "   and _t0.`ownUser` in ( " + //
        "       select _t0.`userId` as _c0 from `users_top` as _t0 " + //
        "       where ( 1 = 1 ) " + //
        "       ) " + //
        "order by _t0.`showTime` asc " + //
        "limit 1000 offset 8000"));

        Assert.assertEquals(2, provider.getTableStats().size());
    }

}
