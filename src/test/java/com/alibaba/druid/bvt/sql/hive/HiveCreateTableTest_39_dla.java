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

public class HiveCreateTableTest_39_dla extends OracleTest {

    public void test_0() throws Exception {
        String sql =  "--\n-- Sample on record\n--     {\"c_custkey\":1,\"c_name\":\"Customer#000000001\",\"c_address\":\"IVhzIApeRb ot,c,E\",\"c_nationkey\":15,\"c_phone\":\"25-989-741-2988\",\"c_acctbal\":711.56,\"c_mktsegment\":\"BUILDING\",\"c_comment\":\"to theeven, regular platelets. regular, ironic epitaphs nag e\"}\n\nCREATE EXTERNAL TABLE IF NOT EXISTS `mengdou_test`.`ddl_customer_json`(`c_acctbal` double, `c_address` string, `c_comment` string, `c_custkey` int, `c_mktsegment` string, `c_name` string, `c_nationkey` int, `c_phone` string, ) STORED AS JSON LOCATION 'oss://oss-cn-shanghai-for-openanalytics/datasets/tpch/0.01x/json_date/customer_json/' TBLPROPERTIES ( \n'skip.header.line.count'='0',\n'recursive.directories'='false');";

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, DbType.hive, SQLParserFeature.KeepComments);

        assertEquals("--\n" +
                "-- Sample on record\n" +
                "--     {\"c_custkey\":1,\"c_name\":\"Customer#000000001\",\"c_address\":\"IVhzIApeRb ot,c,E\",\"c_nationkey\":15,\"c_phone\":\"25-989-741-2988\",\"c_acctbal\":711.56,\"c_mktsegment\":\"BUILDING\",\"c_comment\":\"to theeven, regular platelets. regular, ironic epitaphs nag e\"}\n" +
                "--\n" +
                "-- Sample on record\n" +
                "--     {\"c_custkey\":1,\"c_name\":\"Customer#000000001\",\"c_address\":\"IVhzIApeRb ot,c,E\",\"c_nationkey\":15,\"c_phone\":\"25-989-741-2988\",\"c_acctbal\":711.56,\"c_mktsegment\":\"BUILDING\",\"c_comment\":\"to theeven, regular platelets. regular, ironic epitaphs nag e\"}\n" +
                "CREATE EXTERNAL TABLE IF NOT EXISTS `mengdou_test`.`ddl_customer_json` (\n" +
                "\t`c_acctbal` double,\n" +
                "\t`c_address` string,\n" +
                "\t`c_comment` string,\n" +
                "\t`c_custkey` int,\n" +
                "\t`c_mktsegment` string,\n" +
                "\t`c_name` string,\n" +
                "\t`c_nationkey` int,\n" +
                "\t`c_phone` string\n" +
                ")\n" +
                "STORED AS JSON\n" +
                "LOCATION 'oss://oss-cn-shanghai-for-openanalytics/datasets/tpch/0.01x/json_date/customer_json/'\n" +
                "TBLPROPERTIES (\n" +
                "\t'skip.header.line.count' = '0',\n" +
                "\t'recursive.directories' = 'false'\n" +
                ");", SQLUtils.toSQLString(statementList, JdbcConstants.HIVE));

    }

}
