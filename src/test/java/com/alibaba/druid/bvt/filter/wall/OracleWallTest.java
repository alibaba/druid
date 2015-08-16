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

public class OracleWallTest extends TestCase {

    public void testWall() throws Exception {
        WallConfig config = new WallConfig();
        config.setSelectUnionCheck(true);
        Assert.assertTrue(WallUtils.isValidateOracle("select f1, f2 from t where c=1 union select 1, 2",config));
        Assert.assertFalse(WallUtils.isValidateOracle("select f1, f2 from t where c=1 union select 1, 2 --",config));
        
        Assert.assertFalse(WallUtils.isValidateOracle("SELECT * FROM T UNION select * from TAB"));
        Assert.assertFalse(WallUtils.isValidateOracle("SELECT * FROM T UNION select * from ALL_TABLES where (1=1 or (1+1)=2) and (4=8 or 1=1)"));
    }
}
