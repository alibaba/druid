/*
 * Copyright 1999-2017 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.druid.bvt.sql.mysql.select;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;

public class MySqlSelectTest_309
        extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "SELECT *\n" +
                "FROM `yeahmobi`.`new_eagle_log_parquet`\n" +
                "AND part = '2020-07-07'\n" +
                "AND hour = '09'\n" +
                "LIMIT 20";

        try {
            SQLStatement stmt = SQLUtils
                    .parseSingleStatement(sql, DbType.mysql);
            fail();
        } catch (Exception e) {

        }
    }
}