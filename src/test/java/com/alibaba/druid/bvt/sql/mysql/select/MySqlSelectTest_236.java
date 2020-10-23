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
package com.alibaba.druid.bvt.sql.mysql.select;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;


public class MySqlSelectTest_236 extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "SELECT count(1) AS userCount,count(t.telephone) AS userWithPhoneCount,count(t.email) AS userWithEmailCount FROM (SELECT t.user_id , dw.telephone,dw.email FROM (SELECT dw.userid AS user_id FROM dw_user_property_wide_table_merged_v2 dw WHERE ((dw.is_sub = true))) t INNER JOIN dw_user_property_wide_table_merged_v2 dw ON t.user_id = dw.userid) t /*+META({\"s\": \"com.qunhe.logcomplex.userinformation.mapper.ads.UserPropertyMapper.countUser\"})*/";


        SQLSelectStatement stmt = (SQLSelectStatement) SQLUtils.parseSingleMysqlStatement(sql);

        assertEquals("SELECT count(1) AS userCount, count(t.telephone) AS userWithPhoneCount\n" +
                "\t, count(t.email) AS userWithEmailCount\n" +
                "FROM (\n" +
                "\tSELECT t.user_id, dw.telephone, dw.email\n" +
                "\tFROM (\n" +
                "\t\tSELECT dw.userid AS user_id\n" +
                "\t\tFROM dw_user_property_wide_table_merged_v2 dw\n" +
                "\t\tWHERE dw.is_sub = true\n" +
                "\t) t\n" +
                "\t\tINNER JOIN dw_user_property_wide_table_merged_v2 dw ON t.user_id = dw.userid\n" +
                ") t/*+META({\"s\": \"com.qunhe.logcomplex.userinformation.mapper.ads.UserPropertyMapper.countUser\"})*/", stmt.toString());
    }



}