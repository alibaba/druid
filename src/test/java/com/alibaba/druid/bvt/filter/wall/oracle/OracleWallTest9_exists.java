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
package com.alibaba.druid.bvt.filter.wall.oracle;

import com.alibaba.druid.wall.WallUtils;
import junit.framework.TestCase;
import org.junit.Assert;

public class OracleWallTest9_exists extends TestCase {

    public void test_true() throws Exception {
        String sql = //
        "select *\n" +
                "  from V v\n" +
                " where  v.u_id in (select id from U where wx_b = 1)\n" +
                "   and not exists (select 1 from M where code = v.code)\n" +
                "   and rownum < 1000";
        Assert.assertTrue(WallUtils.isValidateOracle(sql));
    }
}
