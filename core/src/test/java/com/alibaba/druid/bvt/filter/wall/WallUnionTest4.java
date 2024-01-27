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

import com.alibaba.druid.wall.WallConfig;
import com.alibaba.druid.wall.WallUtils;
import junit.framework.TestCase;
import org.junit.Assert;

/**
 * 这个场景，被攻击者用于测试当前SQL拥有多少字段
 *
 * @author wenshao
 */
public class WallUnionTest4 extends TestCase {

    public static final String UNION_SQL1 = "SELECT id, product FROM T1 t where id=1 UNION (SELECT * FROM (SELECT 1,'x') X)";
    public static final String UNION_SQL2 = "SELECT id, product FROM T1 t where id=1 UNION (SELECT * FROM (SELECT 1,'x') X) -- ";

    public void testMySql() throws Exception {
        final WallConfig config = new WallConfig();
        config.setSelectUnionCheck(true);
        Assert.assertFalse(WallUtils.isValidateMySql(UNION_SQL1, config));
        Assert.assertFalse(WallUtils.isValidateMySql(UNION_SQL2, config));

        config.setSelectUnionCheck(false);
        config.setSelectWhereAlwayTrueCheck(false);
        config.setCommentAllow(true);
        Assert.assertTrue(WallUtils.isValidateMySql(UNION_SQL1, config));
        Assert.assertTrue(WallUtils.isValidateMySql(UNION_SQL2, config));

    }

    public void testORACLE() throws Exception {
        final WallConfig config = new WallConfig();
        config.setSelectUnionCheck(true);
        Assert.assertFalse(WallUtils.isValidateOracle(UNION_SQL1, config));
        Assert.assertFalse(WallUtils.isValidateOracle(UNION_SQL2, config));

        config.setSelectUnionCheck(false);
        config.setSelectWhereAlwayTrueCheck(false);
        config.setCommentAllow(true);
        Assert.assertTrue(WallUtils.isValidateOracle(UNION_SQL1, config));
        Assert.assertTrue(WallUtils.isValidateOracle(UNION_SQL2, config));
    }
}
