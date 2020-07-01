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
package com.alibaba.druid.bvt.sql.oracle;

import junit.framework.TestCase;

import com.alibaba.druid.sql.visitor.ParameterizedOutputVisitorUtils;
import com.alibaba.druid.util.JdbcUtils;

public class MergeTest extends TestCase {

    public void test_mergeCall() throws Exception {
        String sql = "{ call INTERFACE_DATA_EXTRACTION.INVOICE_INFO(?,?,?)}";

        ParameterizedOutputVisitorUtils.parameterize(sql, null);
    }

    public void test_mergeCall_oracle() throws Exception {
        String sql = "{ call INTERFACE_DATA_EXTRACTION.INVOICE_INFO(?,?,?)}";

        ParameterizedOutputVisitorUtils.parameterize(sql, JdbcUtils.ORACLE);
        ParameterizedOutputVisitorUtils.parameterize(sql, JdbcUtils.OCEANBASE_ORACLE);
        ParameterizedOutputVisitorUtils.parameterize(sql, JdbcUtils.ALI_ORACLE);
    }

    public void test_mergeCall_mysql() throws Exception {
        String sql = "{ call INTERFACE_DATA_EXTRACTION.INVOICE_INFO(?,?,?)}";

        ParameterizedOutputVisitorUtils.parameterize(sql, JdbcUtils.MYSQL);
        ParameterizedOutputVisitorUtils.parameterize(sql, JdbcUtils.OCEANBASE);
    }
}
