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
 * 这个场景，检测可疑的Having条件
 * @author wenshao
 *
 */
public class WallReadOnlyTest extends TestCase {
    private WallConfig config = new WallConfig();
    
    protected void setUp() throws Exception {
        config.addReadOnlyTable("members");
    }
    private String sql = "SELECT F1, F2 members";
    private String insert_sql = "INSERT INTO members (FID, FNAME) VALUES (?, ?)";
    private String update_sql = "UPDATE members SET FNAME = ? WHERe FID = ?";
    private String delete_sql = "DELETE members WHERE FID = ?";

    public void testMySql() throws Exception {
        Assert.assertTrue(WallUtils.isValidateMySql(sql, config));
        Assert.assertFalse(WallUtils.isValidateMySql(insert_sql, config));
        Assert.assertFalse(WallUtils.isValidateMySql(update_sql, config));
        Assert.assertFalse(WallUtils.isValidateMySql(delete_sql, config));
    }
    
    public void testORACLE() throws Exception {
        Assert.assertTrue(WallUtils.isValidateOracle(sql, config));
        Assert.assertFalse(WallUtils.isValidateOracle(insert_sql, config));
        Assert.assertFalse(WallUtils.isValidateOracle(update_sql, config));
        Assert.assertFalse(WallUtils.isValidateOracle(delete_sql, config));
    }
}
