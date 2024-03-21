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
public class WallDeleteWhereTest extends TestCase {
    private String sql = "DELETE FROM T WHERE 1 = 1";
    private String sql2 = "DELETE FROM T WHERE id = 0 and 1 = 1";

    public void testMySql() throws Exception {
        Assert.assertFalse(WallUtils.isValidateMySql(sql));
        Assert.assertTrue(WallUtils.isValidateMySql(sql2));

        final WallConfig wallConfig = new WallConfig();
        wallConfig.setDeleteWhereAlwayTrueCheck(false);
        Assert.assertTrue(WallUtils.isValidateMySql(sql, wallConfig));
        wallConfig.setDeleteWhereAlwayTrueCheck(true);
        Assert.assertFalse(WallUtils.isValidateMySql(sql, wallConfig));
    }

    public void testORACLE() throws Exception {
        Assert.assertFalse(WallUtils.isValidateOracle(sql));
        Assert.assertTrue(WallUtils.isValidateOracle(sql2));

        final WallConfig wallConfig = new WallConfig();
        wallConfig.setDeleteWhereAlwayTrueCheck(false);
        Assert.assertTrue(WallUtils.isValidateOracle(sql, wallConfig));
        wallConfig.setDeleteWhereAlwayTrueCheck(true);
        Assert.assertFalse(WallUtils.isValidateOracle(sql, wallConfig));
    }
}
