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
package com.alibaba.druid.bvt.sql.hive;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.OracleTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.parser.SQLParserFeature;

import java.util.List;

public class HiveCreateTableTest_44 extends OracleTest {

    public void test_0() throws Exception {
        String sql = "--\n-- Sample on record\n" +
                "--     {\"bid\":\"360\",\"request_id\":\"12c6b4b7c7d590fc\",\"hour\":\"2018121315\",\"time\":\"1544684400\",\"tagid\":\"2256906\",\"plan_id\":\"636102\",\"sid\":\"288\",\"creative_id\":\"198\",\"cookie_id\":\"7af422884e2fab197c9dfd068181ac0d\",\"ip\":\"117.136.89.121\",\"price\":\"0.2800\",\"bid_price\":\"0.1400\",\"user_agent\":\"Dalvik/2.1.0 (Linux; U; Android 6.0.1; OPPO A57 Build/MMB29M)\",\"refer\":\"\"}\n\n" +
                "CREATE EXTERNAL TABLE IF NOT EXISTS `data2`.`table1`(`bid` int, `bid_price` double, `cookie_id` binary, `creative_id` int, `hour` bigint, `ip` string, `plan_id` bigint, `price` double, `refer` binary, `request_id` binary, `sid` int, `tagid` bigint, `time` bigint, `user_agent` string, ) STORED AS JSON LOCATION 'oss://aliyun-oa-query-results-1863300811734283-cn-hangzhou/data/json_data/' TBLPROPERTIES ( \n'skip.header.line.count'='0',\n'recursive.directories'='true');";

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, DbType.hive, SQLParserFeature.KeepComments);

        SQLStatement stmt = statementList.get(0);
        assertEquals("--\n" +
                "-- Sample on record\n" +
                "--     {\"bid\":\"360\",\"request_id\":\"12c6b4b7c7d590fc\",\"hour\":\"2018121315\",\"time\":\"1544684400\",\"tagid\":\"2256906\",\"plan_id\":\"636102\",\"sid\":\"288\",\"creative_id\":\"198\",\"cookie_id\":\"7af422884e2fab197c9dfd068181ac0d\",\"ip\":\"117.136.89.121\",\"price\":\"0.2800\",\"bid_price\":\"0.1400\",\"user_agent\":\"Dalvik/2.1.0 (Linux; U; Android 6.0.1; OPPO A57 Build/MMB29M)\",\"refer\":\"\"}\n" +
                "CREATE EXTERNAL TABLE IF NOT EXISTS `data2`.`table1` (\n" +
                "\t`bid` int,\n" +
                "\t`bid_price` double,\n" +
                "\t`cookie_id` binary,\n" +
                "\t`creative_id` int,\n" +
                "\t`hour` bigint,\n" +
                "\t`ip` string,\n" +
                "\t`plan_id` bigint,\n" +
                "\t`price` double,\n" +
                "\t`refer` binary,\n" +
                "\t`request_id` binary,\n" +
                "\t`sid` int,\n" +
                "\t`tagid` bigint,\n" +
                "\t`time` bigint,\n" +
                "\t`user_agent` string\n" +
                ")\n" +
                "STORED AS JSON\n" +
                "LOCATION 'oss://aliyun-oa-query-results-1863300811734283-cn-hangzhou/data/json_data/'\n" +
                "TBLPROPERTIES (\n" +
                "\t'skip.header.line.count' = '0',\n" +
                "\t'recursive.directories' = 'true'\n" +
                ");", stmt.toString());
    }

}
