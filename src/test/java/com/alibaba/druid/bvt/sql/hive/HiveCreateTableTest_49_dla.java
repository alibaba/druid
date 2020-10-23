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

public class HiveCreateTableTest_49_dla
        extends OracleTest {

    public void test_0() throws Exception {
        String sql = "create external  table `dla_result`.`sgm_add`\n" +
                "STORED AS parquet \n" +
                "LOCATION  \n" +
                "'oss://aliyun-oa-query-results-1408722774623865-oss-cn-zhangjiakou/DLA_Result/sgm_add/'\n" +
                "TBLPROPERTIES ('auto.create.location' = 'true') like select pure_date,(substr(regexp_extract(fld11, '\"sgm_add\":\"?([^\"}]*)', 1), 1, 10)) AS sgm_add from \n" +
                "log_push_1912 where pure_date = 20191120" ;
        SQLStatement stmt =  SQLUtils.parseSingleStatement(sql, DbType.hive, SQLParserFeature.KeepComments);

        SQLStatement clone = stmt.clone();

        assertEquals("CREATE EXTERNAL TABLE `dla_result`.`sgm_add`\n" +
                "STORED AS parquet\n" +
                "LOCATION 'oss://aliyun-oa-query-results-1408722774623865-oss-cn-zhangjiakou/DLA_Result/sgm_add/'\n" +
                "TBLPROPERTIES (\n" +
                "\t'auto.create.location' = 'true'\n" +
                ")\n" +
                "LIKE\n" +
                "SELECT pure_date\n" +
                "\t, substr(regexp_extract(fld11, '\"sgm_add\":\"?([^\"}]*)', 1), 1, 10) AS sgm_add\n" +
                "FROM log_push_1912\n" +
                "WHERE pure_date = 20191120", clone.toString());

        assertEquals(stmt.toString(), SQLUtils.parseSingleStatement(stmt.toString(), DbType.hive, SQLParserFeature.KeepComments).toString());
    }

}
