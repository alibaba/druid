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
package com.alibaba.druid.bvt.bug;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.wall.WallConfig;
import com.alibaba.druid.wall.WallUtils;

public class Issue_728 extends TestCase {

    public void test1() throws Exception {
        String sql = "select * from city_list where city_id = 3-1";

        WallConfig config = new WallConfig();
        config.setConstArithmeticAllow(false);

        Assert.assertFalse(WallUtils.isValidateMySql(sql, config));
    }

    public void test2() throws Exception {
        String sql = "SELECT * from city_list where 2 = case when 2=1 then 1 else 2 END";

        WallConfig config = new WallConfig();
        config.setCaseConditionConstAllow(false);

        Assert.assertFalse(WallUtils.isValidateMySql(sql, config));
    }

    public void test3() throws Exception {
        String sql = "SELECT * from city_list where city_id = 1 & 2";

        WallConfig config = new WallConfig();
        config.setConditionOpBitwseAllow(false);

        Assert.assertFalse(WallUtils.isValidateMySql(sql, config));
    }
}
