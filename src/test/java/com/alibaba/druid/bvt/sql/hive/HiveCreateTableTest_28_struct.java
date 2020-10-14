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

import com.alibaba.druid.sql.OracleTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.util.JdbcConstants;
import org.junit.Assert;

import java.util.List;

public class HiveCreateTableTest_28_struct extends OracleTest {

    public void test_0() throws Exception {
        String sql = //
                "CREATE EXTERNAL TABLE `json_table_1`(\n" +
                        "  `docid` string COMMENT 'from deserializer', \n" +
                        "  `user_1` struct<id:int,username:string,name:string,shippingaddress:struct<address1:string,address2:string,city:string,state:string>,orders:array<struct<itemid:int,orderdate:string>>> COMMENT 'from deserializer')\n" +
                        "ROW FORMAT SERDE \n" +
                        "  'org.apache.hive.hcatalog.data.JsonSerDe' \n" +
                        "STORED AS INPUTFORMAT \n" +
                        "  'org.apache.hadoop.mapred.TextInputFormat' \n" +
                        "OUTPUTFORMAT \n" +
                        "  'org.apache.hadoop.hive.ql.io.HiveIgnoreKeyTextOutputFormat'\n" +
                        "LOCATION\n" +
                        "  'oss://acs:ram::1013022312866336:role&aliyunopenanalyticsaccessingossrole@oss-cn-beijing-for-openanalytics-test/datasets/test/json/hcatalog_serde/table_1'\n" +
                        "TBLPROPERTIES (\n" +
                        "  'COLUMN_STATS_ACCURATE'='false', \n" +
                        "  'numFiles'='1', \n" +
                        "  'numRows'='-1', \n" +
                        "  'rawDataSize'='-1', \n" +
                        "  'totalSize'='347', \n" +
                        "  'transient_lastDdlTime'='1530879306')"; //

        List<SQLStatement> statementList = SQLUtils.toStatementList(sql, JdbcConstants.HIVE);
        SQLStatement stmt = statementList.get(0);
        System.out.println(stmt.toString());

        Assert.assertEquals(1, statementList.size());

        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(JdbcConstants.HIVE);
        stmt.accept(visitor);

        {
            String text = SQLUtils.toSQLString(stmt, JdbcConstants.HIVE);

            assertEquals("CREATE EXTERNAL TABLE `json_table_1` (\n" +
                    "\t`docid` string COMMENT 'from deserializer',\n" +
                    "\t`user_1` STRUCT<id:int, username:string, name:string, shippingaddress:STRUCT<address1:string, address2:string, city:string, state:string>, orders:ARRAY<STRUCT<itemid:int, orderdate:string>>> COMMENT 'from deserializer'\n" +
                    ")\n" +
                    "ROW FORMAT\n" +
                    "\tSERDE 'org.apache.hive.hcatalog.data.JsonSerDe'\n" +
                    "STORED AS\n" +
                    "\tINPUTFORMAT 'org.apache.hadoop.mapred.TextInputFormat'\n" +
                    "\tOUTPUTFORMAT 'org.apache.hadoop.hive.ql.io.HiveIgnoreKeyTextOutputFormat'\n" +
                    "LOCATION 'oss://acs:ram::1013022312866336:role&aliyunopenanalyticsaccessingossrole@oss-cn-beijing-for-openanalytics-test/datasets/test/json/hcatalog_serde/table_1'\n" +
                    "TBLPROPERTIES (\n" +
                    "\t'COLUMN_STATS_ACCURATE' = 'false',\n" +
                    "\t'numFiles' = '1',\n" +
                    "\t'numRows' = '-1',\n" +
                    "\t'rawDataSize' = '-1',\n" +
                    "\t'totalSize' = '347',\n" +
                    "\t'transient_lastDdlTime' = '1530879306'\n" +
                    ")", text);
        }

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());
        System.out.println("orderBy : " + visitor.getOrderByColumns());

        assertEquals(1, visitor.getTables().size());
        assertEquals(2, visitor.getColumns().size());
        assertEquals(0, visitor.getConditions().size());
        assertEquals(0, visitor.getRelationships().size());
        assertEquals(0, visitor.getOrderByColumns().size());

        assertTrue(visitor.containsTable("json_table_1"));

    }

}
