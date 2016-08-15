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

public class MySqlWallTest139 extends TestCase {

    public void test_false() throws Exception {
        WallProvider provider = new MySqlWallProvider();

        String sql = "select count(1) as total "//
                     + " from (" //
                     + "    select '' buyer_nick from dual " //
                     + "    where 1=0  "//
                     + "    union " //
                     + "    select distinct buyer_nick "//
                     + "    from sys_info.orders "//
                     + "    where 1=1  and receiver_district in ('平谷区')" //
                     + ") a " //
                     + "inner join (" //
                     + "    select buyer_nick from ("//
                     + "        select distinct buyer_nick "//
                     + "        from sys_info.orders " //
                     + "        where 1=1  and created > '2013-07-28' "//
                     + "    ) recent_days " //
                     + "inner join (" //
                     + "    select distinct buyer_nick " //
                     + "    from sys_info.orders " //
                     + "    where 1=1  and seller_nick in ('创维官方旗舰店') " //
                     + "    ) seller_nick using(buyer_nick) "//
                     + ") b using(buyer_nick)";
        Assert.assertTrue(provider.checkValid(sql));
    }
}
