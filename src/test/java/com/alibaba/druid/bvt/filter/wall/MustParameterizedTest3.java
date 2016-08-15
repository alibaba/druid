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

public class MustParameterizedTest3 extends TestCase {

    private WallConfig config = new WallConfig();

    protected void setUp() throws Exception {
        config.setMustParameterized(true);
    }

    public void testMySql() throws Exception {
        Assert.assertFalse(WallUtils.isValidateMySql("select * from t where id  = (3 + 5 - 2 - 1)", config));
        Assert.assertFalse(WallUtils.isValidateMySql("select * from t where id  != id + 3", config));
        Assert.assertFalse(WallUtils.isValidateMySql("delete from t where id  != id + 3", config));
        Assert.assertFalse(WallUtils.isValidateMySql("delete from t where id = 'aa' + 'bbb'", config));
        Assert.assertTrue(WallUtils.isValidateMySql("select * from t where id  = ? ORDER BY 1", config));
        Assert.assertTrue(WallUtils.isValidateMySql("select 1, 2, 3 from t where id  = ?", config));
        Assert.assertFalse(WallUtils.isValidateMySql("select 1, 2, 3 from t where id  = 7", config));
        Assert.assertTrue(WallUtils.isValidateMySql("select 1, 2, 3 from t where id  = ? union select * from t", config));
        Assert.assertFalse(WallUtils.isValidateMySql("select 1, 2, 3 from t where id  = ? union select 1, 2, 3 --", config));
        Assert.assertFalse(WallUtils.isValidateMySql("select 1, 2, 3 from t where id  = ? union select * from t fid = fid", config));
        Assert.assertFalse(WallUtils.isValidateMySql("select 1, 2, 3 from t where id  = ? union select * from t fid > 5", config));
    }

}
