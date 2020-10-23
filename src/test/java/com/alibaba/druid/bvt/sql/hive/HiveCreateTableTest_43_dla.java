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

public class HiveCreateTableTest_43_dla extends OracleTest {

    public void test_0() throws Exception {
        String sql =  "CREATE EXTERNAL TABLE special_char1(\n" +
                "    data_time_str string,\n" +
                "    entity_id string,\n" +
                "    thread string,\n" +
                "    logger_name string,\n" +
                "    msg string\n" +
                ") ROW FORMAT SERDE 'org.apache.hadoop.hive.contrib.serde2.MultiDelimitSerDe'\n" +
                "with serdeproperties(\n" +
                "\"field.delim\"=\"\\u2605\"\n" +
                ")\n" +
                "STORED AS TEXTFILE\n" +
                "LOCATION 'oss://oss-cn-beijing-for-openanalytics-test/datasets/test/customer_case/fuzhike/special_char1.log'\n" +
                "TBLPROPERTIES ('textinputformat.record.delimiter'='\\u25bc');";

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, DbType.hive, SQLParserFeature.KeepComments);

        assertEquals("CREATE EXTERNAL TABLE special_char1 (\n" +
                "\tdata_time_str string,\n" +
                "\tentity_id string,\n" +
                "\tthread string,\n" +
                "\tlogger_name string,\n" +
                "\tmsg string\n" +
                ")\n" +
                "ROW FORMAT\n" +
                "\tSERDE 'org.apache.hadoop.hive.contrib.serde2.MultiDelimitSerDe'\n" +
                "WITH SERDEPROPERTIES (\n" +
                "\t\"field.delim\" = '\\u2605'\n" +
                ")\n" +
                "STORED AS TEXTFILE\n" +
                "LOCATION 'oss://oss-cn-beijing-for-openanalytics-test/datasets/test/customer_case/fuzhike/special_char1.log'\n" +
                "TBLPROPERTIES (\n" +
                "\t'textinputformat.record.delimiter' = '\\u25bc'\n" +
                ");", statementList.get(0).toString());
    }

}
