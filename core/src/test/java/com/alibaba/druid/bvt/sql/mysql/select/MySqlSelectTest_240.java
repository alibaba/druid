/*
 * Copyright 1999-2023 Alibaba Group Holding Ltd.
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


public class MySqlSelectTest_240 extends MysqlTest {
    public void test_0() throws Exception {
        String sql = "select sum(file_size / 1024. / 1024.) / 1024.\n" +
                "from mssql_oss_download_list";


        SQLSelectStatement stmt = (SQLSelectStatement) SQLUtils.parseSingleMysqlStatement(sql);

        assertEquals("SELECT sum(file_size / 1024. / 1024.) / 1024.\n" +
                "FROM mssql_oss_download_list", stmt.toString());
    }


}