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
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;

public class MySqlSelectTest_json extends MysqlTest {

    public void test_0() throws Exception {
        parseTrue("SELECT json_size('{\"x\": {\"a\": 1, \"b\": 2}}', '$.x')",
                "SELECT json_size('{\"x\": {\"a\": 1, \"b\": 2}}', '$.x')");

        parseTrue("SELECT regexp_extract(fld11,'\"campaign_group_id\":\"?([-+]?[0-9]{1,19}+)',1)\n" +
                "FROM rmlog_push_dla_poc WHERE day='20190301' and hour='00' LIMIT 10",
                "SELECT regexp_extract(fld11, '\"campaign_group_id\":\"?([-+]?[0-9]{1,19}+)', 1)\n" +
                        "FROM rmlog_push_dla_poc\n" +
                        "WHERE day = '20190301'\n" +
                        "\tAND hour = '00'\n" +
                        "LIMIT 10");
    }
}
