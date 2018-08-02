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
 * 这个场景，被攻击者用于测试当前SQL拥有多少字段
 * 
 * @author wenshao
 */
public class WallUnionTest4 extends TestCase {

    public void testMySql() throws Exception {
        WallConfig config = new WallConfig();
        config.setSelectUnionCheck(true);
        Assert.assertTrue(WallUtils.isValidateMySql("SELECT id, product FROM T1 t where id=1 UNION (SELECT * FROM (SELECT 1,'x') X)",
                                                    config));
        Assert.assertFalse(WallUtils.isValidateMySql("SELECT id, product FROM T1 t where id=1 UNION (SELECT * FROM (SELECT 1,'x') X) -- ",
                                                     config));
    }

    public void testORACLE() throws Exception {
        WallConfig config = new WallConfig();
        config.setSelectUnionCheck(true);
        Assert.assertTrue(WallUtils.isValidateOracle("SELECT id, product FROM T1 t where id=1 UNION (SELECT * FROM (SELECT 1,'x') X)",
                                                     config));
        Assert.assertFalse(WallUtils.isValidateOracle("SELECT id, product FROM T1 t where id=1 UNION (SELECT * FROM (SELECT 1,'x') X) -- ",
                                                      config));
    }
}
