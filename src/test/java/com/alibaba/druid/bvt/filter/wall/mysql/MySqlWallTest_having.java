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

import com.alibaba.druid.wall.WallUtils;

/**
 * SQLServerWallTest
 * 
 * @author RaymondXiu
 * @version 1.0, 2012-3-18
 * @see
 */
public class MySqlWallTest_having extends TestCase {

    public void test_having() throws Exception {
        Assert.assertTrue(WallUtils.isValidateMySql(//
        "select id, count(*) from t group by id having 1 = 1"));
    }
    
    public void test_having_true_first() throws Exception {
        Assert.assertTrue(WallUtils.isValidateMySql(//
        "select id, count(*) from t group by id having 1 = 1 AND count(*) > 2"));
    }

    public void test_having_false() throws Exception {
        Assert.assertFalse(WallUtils.isValidateMySql(//
        "select id, count(*) from t group by id having count(*) > 2 OR 1 = 1"));
    }
}
