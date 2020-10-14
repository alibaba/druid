/*
 * Copyright 1999-2017 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.bvt.sql.mysql.param;

import com.alibaba.druid.sql.visitor.ParameterizedOutputVisitorUtils;
import com.alibaba.druid.util.JdbcConstants;
import org.junit.Assert;

public class MySqlParameterizedOutputVisitorTest_3 extends com.alibaba.druid.bvt.sql.mysql.param.MySQLParameterizedTest {
    protected void setUp() throws Exception {
        System.setProperty("fastsql.parameterized.shardingSupport", "false");
    }
    
    protected void tearDown() throws Exception {
        System.clearProperty("fastsql.parameterized.shardingSupport");
    }

    public void test_0() throws Exception {
        String sql = "delete from alerts where not (exists (select metric1_.id from metrics metric1_ where id=alerts.metric_id))";
        Assert.assertSame(ParameterizedOutputVisitorUtils.parameterize(sql, JdbcConstants.MYSQL), sql);
    }
}
