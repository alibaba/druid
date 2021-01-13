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

public class MySqlParameterizedOutputVisitorTest_6 extends com.alibaba.druid.bvt.sql.mysql.param.MySQLParameterizedTest {

    public void test_0() throws Exception {
        String sql = "SET autocommit=1";
        String paramSql = "SET autocommit = ?";
        Assert.assertEquals(ParameterizedOutputVisitorUtils.parameterize(sql, JdbcConstants.MYSQL), paramSql);

        paramaterizeAST(sql, paramSql);
    }

    public void test_1() throws Exception {
        String sql = "SET GLOBAL sort_buffer_size=1000000, SESSION sort_buffer_size=1000000;";
        String paramSql = "SET @@global.sort_buffer_size = ?, @@session.sort_buffer_size = ?;";
        Assert.assertEquals(paramSql, ParameterizedOutputVisitorUtils.parameterize(sql, JdbcConstants.MYSQL));

        paramaterizeAST(sql, paramSql);
    }
}
