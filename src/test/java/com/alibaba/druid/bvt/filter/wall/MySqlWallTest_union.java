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

import com.alibaba.druid.wall.WallConfig;
import com.alibaba.druid.wall.WallUtils;

public class MySqlWallTest_union extends TestCase {

    public void testUnion() throws Exception {
        WallConfig config = new WallConfig();
        config.setSelectUnionCheck(true);

        Assert.assertTrue(WallUtils.isValidateMySql("select f1, f2 from t where id=1 union select 1, 2", config)); // not end of comment
        Assert.assertFalse(WallUtils.isValidateMySql("select f1, f2 from t where id=1 union select 1, 2 --", config));

        Assert.assertTrue(WallUtils.isValidateMySql("select f1, f2 from t union select 1, 2", config)); // no where

        Assert.assertFalse(WallUtils.isValidateMySql("select f1, f2 from t where id=1 union select null, '1', 2 --", config));

        Assert.assertTrue(WallUtils.isValidateMySql("select f1, f2 from t where id=1 union select c1, c2", config)); //union select item is not const

        Assert.assertTrue(WallUtils.isValidateMySql("SELECT typeid, typename FROM (SELECT typeid, typename FROM materialtype UNION ALL SELECT ? AS typeid, ? AS typename) a ORDER BY typeid",
                                                    config)); // union select item has alias

        Assert.assertFalse(WallUtils.isValidateMySql("select f1, f2 from (select 1 as f1, 2 as f2) t union select 'u1', 'u2' --", config)); // from is subQuery

        Assert.assertTrue(WallUtils.isValidateMySql("select f1, f2 from t where id=1 union select 'u1' as u1, 'u2' as u2",  config)); // union select item has alias
    }
}
