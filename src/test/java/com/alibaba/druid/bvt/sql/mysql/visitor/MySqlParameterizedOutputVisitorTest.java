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
package com.alibaba.druid.bvt.sql.mysql.visitor;

import junit.framework.TestCase;

import com.alibaba.druid.sql.visitor.ParameterizedOutputVisitorUtils;

public class MySqlParameterizedOutputVisitorTest extends TestCase {

    public void test_0() throws Exception {
        String sql = "SELECT appsheetserialno FROM app_trans WHERE nodeid = _gbk '619' "
                     + " AND alino = _gbk '2013110900031031001700thfund00163619'"
                     + " AND apserialno = _gbk '201405120002300002170013205458'";
        System.out.println(ParameterizedOutputVisitorUtils.parameterize(sql, "mysql"));
    }

}
