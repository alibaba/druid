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
package com.alibaba.druid.bvt.filter.wall;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.wall.WallConfig;
import com.alibaba.druid.wall.WallUtils;

/**
 * @author wenshao
 */
public class WallUpdateWhereTest1 extends TestCase {

    private String sql  = "update wx_shop set shop_view = shop_view + 1 where id = 118 OR 69=69 LIMIT 100 --";
    private String sql1 = "update wx_shop set shop_view = shop_view + 1 where id = 118 OR 69=69 LIMIT 100 #and c=1";

    public void test_check_true() throws Exception {
        WallConfig config = new WallConfig();
        config.setUpdateWhereAlayTrueCheck(true);
        config.setConditionAndAlwayTrueAllow(true);
        config.setCommentAllow(true);

        Assert.assertFalse(WallUtils.isValidateMySql(sql, config));
        Assert.assertFalse(WallUtils.isValidateMySql(sql1, config));
    }

    public void test_check_false() throws Exception {
        WallConfig config = new WallConfig();
        config.setUpdateWhereAlayTrueCheck(false);
        config.setConditionAndAlwayTrueAllow(true);
        config.setCommentAllow(true);

        Assert.assertTrue(WallUtils.isValidateMySql(sql, config));
        Assert.assertTrue(WallUtils.isValidateMySql(sql1, config));
    }
}
