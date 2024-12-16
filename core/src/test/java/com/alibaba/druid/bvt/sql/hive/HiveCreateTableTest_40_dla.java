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
import com.alibaba.druid.util.JdbcConstants;

import java.util.List;

public class HiveCreateTableTest_40_dla extends OracleTest {
    public void test_0() throws Exception {
        String sql = "CREATE EXTERNAL TABLE IF NOT EXISTS `customer_case`.`shangjian_6e958954-f2c7-11e8-94b7-0c54159e4818.json.snappy` (\n" +
                "  `batch_date` string,\n" +
                "  `data` STRUCT<`goods_name`:STRING, `thumb_url`:STRING, `country`:STRING, `is_app`:INT, `sales_tip`:STRING, `image_url`:STRING, `cnt`:INT, `goods_id`:BIGINT, `hd_thumb_url`:STRING, `is_use_promotion`:INT, `event_type`:INT, `normal_price`:INT, `market_price`:INT, `short_name`:STRING, group:STRUCT<`price`:INT, `customer_num`:INT>>,\n" +
                "  `goods_id` bigint,\n" +
                "  `gtime` bigint,\n" +
                "  `hot_tag` int,\n" +
                "  `mall_id` int,\n" +
                "  `uuid` string\n" +
                ")\n" +
                "STORED AS JSON\n" +
                "LOCATION 'oss://oss-cn-hangzhou-for-openanalytics/datasets/test/customer_case/'\n" +
                "TBLPROPERTIES (\n" +
                "  'skip.header.line.count' = '0',\n" +
                "  'recursive.directories' = 'false',\n" +
                "  'file.filter' = 'shangjian_6e958954-f2c7-11e8-94b7-0c54159e4818.json.snappy'\n" +
                ");\n";

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, DbType.hive, SQLParserFeature.KeepComments);

        assertEquals("CREATE EXTERNAL TABLE IF NOT EXISTS `customer_case`.`shangjian_6e958954-f2c7-11e8-94b7-0c54159e4818.json.snappy` (\n" +
                "\t`batch_date` string,\n" +
                "\t`data` STRUCT<\n" +
                "\t\t`goods_name`:STRING,\n" +
                "\t\t`thumb_url`:STRING,\n" +
                "\t\t`country`:STRING,\n" +
                "\t\t`is_app`:INT,\n" +
                "\t\t`sales_tip`:STRING,\n" +
                "\t\t`image_url`:STRING,\n" +
                "\t\t`cnt`:INT,\n" +
                "\t\t`goods_id`:BIGINT,\n" +
                "\t\t`hd_thumb_url`:STRING,\n" +
                "\t\t`is_use_promotion`:INT,\n" +
                "\t\t`event_type`:INT,\n" +
                "\t\t`normal_price`:INT,\n" +
                "\t\t`market_price`:INT,\n" +
                "\t\t`short_name`:STRING,\n" +
                "\t\tgroup:STRUCT<`price`:INT, `customer_num`:INT>\n" +
                "\t>,\n" +
                "\t`goods_id` bigint,\n" +
                "\t`gtime` bigint,\n" +
                "\t`hot_tag` int,\n" +
                "\t`mall_id` int,\n" +
                "\t`uuid` string\n" +
                ")\n" +
                "STORED AS JSON\n" +
                "LOCATION 'oss://oss-cn-hangzhou-for-openanalytics/datasets/test/customer_case/'\n" +
                "TBLPROPERTIES (\n" +
                "\t'skip.header.line.count' = '0',\n" +
                "\t'recursive.directories' = 'false',\n" +
                "\t'file.filter' = 'shangjian_6e958954-f2c7-11e8-94b7-0c54159e4818.json.snappy'\n" +
                ");", SQLUtils.toSQLString(statementList, JdbcConstants.HIVE));

    }

}
