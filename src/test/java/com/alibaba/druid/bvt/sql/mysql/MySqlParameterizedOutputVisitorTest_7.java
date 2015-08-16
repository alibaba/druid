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
package com.alibaba.druid.bvt.sql.mysql;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.sql.visitor.ParameterizedOutputVisitorUtils;
import com.alibaba.druid.util.JdbcConstants;

public class MySqlParameterizedOutputVisitorTest_7 extends TestCase {

    public void test_hints() throws Exception {
        String sql = "select id from t where id = 3 /*!30000union all select 2*/";
        String psql = ParameterizedOutputVisitorUtils.parameterize(sql, JdbcConstants.MYSQL);
        Assert.assertEquals("SELECT id" //
                            + "\nFROM t"//
                            + "\nWHERE id = ?/*!30000union all select 2*/", psql);
    }
}
