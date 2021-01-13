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
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.util.JdbcConstants;
import org.junit.Assert;

import java.util.List;

public class HiveCreateTableTest_30 extends OracleTest {

    public void test_0() throws Exception {
        String sql = //
                "CREATE EXTERNAL TABLE `nation`(\n" +
                        "  `n_nationkey` int,\n" +
                        "  `n_name` string,\n" +
                        "  `n_regionkey` int,\n" +
                        "  `n_comment` string)\n" +
                        "ROW FORMAT SERDE\n" +
                        "  'org.apache.hadoop.hive.serde2.lazy.LazySimpleSerDe'\n" +
                        "WITH SERDEPROPERTIES (\n" +
                        "  'field.delim'='|',\n" +
                        "  'serialization.format'='|')\n" +
                        "STORED AS INPUTFORMAT\n" +
                        "  'org.apache.hadoop.mapred.TextInputFormat'\n" +
                        "OUTPUTFORMAT\n" +
                        "  'org.apache.hadoop.hive.ql.io.HiveIgnoreKeyTextOutputFormat'\n" +
                        "LOCATION\n" +
                        "  'oss://test-bucket-julian-1/tpch_100m/nation'\n" +
                        "TBLPROPERTIES (\n" +
                        "  'COLUMN_STATS_ACCURATE'='false',\n" +
                        "  'numFiles'='1',\n" +
                        "  'numRows'='-1',\n" +
                        "  'rawDataSize'='-1',\n" +
                        "  'totalSize'='2224',\n" +
                        "  'transient_lastDdlTime'='1528440011')\n"; //

        List<SQLStatement> statementList = SQLUtils.toStatementList(sql, JdbcConstants.HIVE);
        SQLStatement stmt = statementList.get(0);
        System.out.println(stmt.toString());

        Assert.assertEquals(1, statementList.size());

        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(JdbcConstants.HIVE);
        stmt.accept(visitor);

        {
            String text = SQLUtils.toSQLString(stmt, JdbcConstants.HIVE);

            assertEquals("CREATE EXTERNAL TABLE `nation` (\n" +
                    "\t`n_nationkey` int,\n" +
                    "\t`n_name` string,\n" +
                    "\t`n_regionkey` int,\n" +
                    "\t`n_comment` string\n" +
                    ")\n" +
                    "ROW FORMAT\n" +
                    "\tSERDE 'org.apache.hadoop.hive.serde2.lazy.LazySimpleSerDe'\n" +
                    "WITH SERDEPROPERTIES (\n" +
                    "\t'field.delim' = '|',\n" +
                    "\t'serialization.format' = '|'\n" +
                    ")\n" +
                    "STORED AS\n" +
                    "\tINPUTFORMAT 'org.apache.hadoop.mapred.TextInputFormat'\n" +
                    "\tOUTPUTFORMAT 'org.apache.hadoop.hive.ql.io.HiveIgnoreKeyTextOutputFormat'\n" +
                    "LOCATION 'oss://test-bucket-julian-1/tpch_100m/nation'\n" +
                    "TBLPROPERTIES (\n" +
                    "\t'COLUMN_STATS_ACCURATE' = 'false',\n" +
                    "\t'numFiles' = '1',\n" +
                    "\t'numRows' = '-1',\n" +
                    "\t'rawDataSize' = '-1',\n" +
                    "\t'totalSize' = '2224',\n" +
                    "\t'transient_lastDdlTime' = '1528440011'\n" +
                    ")", text);
        }

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());
        System.out.println("orderBy : " + visitor.getOrderByColumns());

        assertEquals(1, visitor.getTables().size());
        assertEquals(4, visitor.getColumns().size());
        assertEquals(0, visitor.getConditions().size());
        assertEquals(0, visitor.getRelationships().size());
        assertEquals(0, visitor.getOrderByColumns().size());

        assertTrue(visitor.containsTable("nation"));

    }
    public void test_1() throws Exception {
        String sql = //
                "CREATE EXTERNAL TABLE nation_test "
                + "(  n_nationkey INT NOT NULL COMMENT 'xxx', "
                + "n_name STRING NULL COMMENT 'yyy', "
                + "n_regionkey INT NULL COMMENT 'zzz', "
                + "n_comment STRING NULL COMMENT 'hhh',  "
                + "PRIMARY KEY (n_nationkey)) "
                + "TBLPROPERTIES ( TABLE_MAPPING = 'nation', COLUMN_MAPPING = 'n_nationkey,N_NATIONKEY; n_name,N_NAME;n_regionkey,N_REGIONKEY; n_comment,N_COMMENT; ' ) "
                + "COMMENT '萌豆'"; //

        List<SQLStatement> statementList = SQLUtils.toStatementList(sql, JdbcConstants.HIVE);
        SQLStatement stmt = statementList.get(0);
        System.out.println(stmt.toString());

        Assert.assertEquals(1, statementList.size());

        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(JdbcConstants.HIVE);
        stmt.accept(visitor);

        {
            String text = SQLUtils.toSQLString(stmt, JdbcConstants.HIVE);

            assertEquals("CREATE EXTERNAL TABLE nation_test (\n" + "\tn_nationkey INT NOT NULL COMMENT 'xxx',\n"
                         + "\tn_name STRING NULL COMMENT 'yyy',\n" + "\tn_regionkey INT NULL COMMENT 'zzz',\n"
                         + "\tn_comment STRING NULL COMMENT 'hhh',\n" + "\tPRIMARY KEY (n_nationkey)\n" + ")\n"
                         + "COMMENT '萌豆'\n" + "TBLPROPERTIES (\n" + "\t'TABLE_MAPPING' = 'nation',\n"
                         + "\t'COLUMN_MAPPING' = 'n_nationkey,N_NATIONKEY; n_name,N_NAME;n_regionkey,N_REGIONKEY; n_comment,N_COMMENT; '\n"
                         + ")", text);
        }

    }
    public void test_2() throws Exception {
        String sql = //
                "CREATE EXTERNAL TABLE `tpch_100m_text`.`nation_line_terminated` (\n"
                + "  `n_nationkey` int,\n"
                + "  `n_name` string,\n"
                + "  `n_regionkey` int,\n"
                + "  `n_comment` string\n"
                + ")\n"
                + "ROW FORMAT DELIMITED\n"
                + "  FIELDS TERMINATED BY '|'  \n"
                + "    ESCAPED BY '\\\\' \n"
                + "    LINES TERMINATED BY ';' \n"
                + "STORED AS `TEXTFILE`\n"
                + "LOCATION 'oss://oss-cn-beijing-for-openanalytics/datasets/tpch/0_1x/text/nation_line_terminated/'"; //

        List<SQLStatement> statementList = SQLUtils.toStatementList(sql, JdbcConstants.HIVE);
        SQLStatement stmt = statementList.get(0);

        {
            String text = SQLUtils.toSQLString(stmt, JdbcConstants.HIVE);

            assertEquals("CREATE EXTERNAL TABLE `tpch_100m_text`.`nation_line_terminated` (\n"
                         + "\t`n_nationkey` int,\n" + "\t`n_name` string,\n" + "\t`n_regionkey` int,\n"
                         + "\t`n_comment` string\n" + ")\n" + "ROW FORMAT DELIMITED\n" + "\tFIELDS TERMINATED BY '|'\n"
                         + "\tESCAPED BY '\\\\'\n" + "\tLINES TERMINATED BY ';'\n" + "STORED AS `TEXTFILE`\n"
                         + "LOCATION 'oss://oss-cn-beijing-for-openanalytics/datasets/tpch/0_1x/text/nation_line_terminated/'", text);
        }

    }
    public void test_3() throws Exception {
        String sql = "-- 234234\ncreate table a(id varchar)";

        SQLStatement statement = SQLUtils.parseSingleStatement(sql, DbType.hive, SQLParserFeature.KeepComments);
        assertEquals("-- 234234\n" + "CREATE TABLE a (\n" + "\tid varchar\n" + ")",
                     SQLUtils.toSQLString(statement, JdbcConstants.HIVE));

    }
    public void test_4() throws Exception {
        String sql = "alter table partition_text_nation add partition (p=101,q) location 'oss://oss-cn-beijing-for-openanalytics-test/datasets/test/test_partition/text_table/part101/';";

        SQLStatement statement = SQLUtils.parseSingleStatement(sql, DbType.hive, SQLParserFeature.KeepComments);
        assertEquals("ALTER TABLE partition_text_nation\n"
                     + "\tADD PARTITION (p = 101, q) LOCATION 'oss://oss-cn-beijing-for-openanalytics-test/datasets/test/test_partition/text_table/part101/';",
                     SQLUtils.toSQLString(statement, JdbcConstants.HIVE));

    }
    public void test_5() throws Exception {
        String sql = "alter table partition_text_nation add partition (p, q=101) location 'oss://oss-cn-beijing-for-openanalytics-test/datasets/test/test_partition/text_table/part101/';";

        SQLStatement statement = SQLUtils.parseSingleStatement(sql, DbType.hive, SQLParserFeature.KeepComments);
        assertEquals("ALTER TABLE partition_text_nation\n"
                     + "\tADD PARTITION (p, q = 101) LOCATION 'oss://oss-cn-beijing-for-openanalytics-test/datasets/test/test_partition/text_table/part101/';",
                     SQLUtils.toSQLString(statement, JdbcConstants.HIVE));


    }

}
