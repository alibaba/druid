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
package com.alibaba.druid.bvt.sql.oracle;

import junit.framework.TestCase;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.util.JdbcUtils;

public class OracleFormatTest2 extends TestCase {

    public void test_formatOracle() throws Exception {
        String text = "UPDATE MEMBER SET GMT_MODIFIED = SYSDATE, STATUS = ?, email = CASE WHEN status = ? THEN rtrim(email, ? || id || ?) ELSE email END WHERE ID IN (?) AND STATUS <> ?";

        String formatedText = SQLUtils.format(text, JdbcUtils.ORACLE);
        System.out.println(formatedText);
        String formatedText2 = SQLUtils.format(text, JdbcUtils.ALI_ORACLE);
        System.out.println(formatedText2);
    }
}
