/*
 * Copyright 1999-2011 Alibaba Group Holding Ltd.
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

import com.alibaba.druid.wall.WallUtils;

/**
 * 这个场景，检测可疑的Having条件
 * @author wenshao
 *
 */
public class WallSelectWhereTest0 extends TestCase {
    private String sql = "SELECT F1, F2 WHERE 1 = 1 OR F1 = ?";

    public void testMySql() throws Exception {
        Assert.assertTrue(WallUtils.isValidateMySql(sql));
    }
    
    public void testORACLE() throws Exception {
        Assert.assertTrue(WallUtils.isValidateOracle(sql));
    }
}
