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
package com.alibaba.druid.bvt.sql.sqlserver;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.sql.visitor.ParameterizedOutputVisitorUtils;
import com.alibaba.druid.util.JdbcConstants;

public class SQLServerParameterizedOutputVisitorTest_2 extends TestCase {

    public void test_0() throws Exception {
        String sql = "CREATE TABLE dbo.AO_B9A0F0_APPLIED_TEMPLATE ( ID INTEGER IDENTITY(1,1) NOT NULL, PROJECT_ID BIGINT CONSTRAINT df_AO_B9A0F0_APPLIED_TEMPLATE_PROJECT_ID DEFAULT 0, PROJECT_TEMPLATE_MODULE_KEY VARCHAR(255), PROJECT_TEMPLATE_WEB_ITEM_KEY VARCHAR(255), CONSTRAINT pk_AO_B9A0F0_APPLIED_TEMPLATE_ID PRIMARY KEY(ID) )";
        Assert.assertSame(ParameterizedOutputVisitorUtils.parameterize(sql, JdbcConstants.SQL_SERVER), sql);
    }

}
