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

public class HiveCreateTableTest_27 extends OracleTest {

    public void test_0() throws Exception {
        String sql = //
                "CREATE EXTERNAL TABLE `customer_case.tradelist_csv`(\n" +
                        "  `t_userid` string COMMENT '??ID', \n" +
                        "  `t_dealdate` string COMMENT '????', \n" +
                        "  `t_businflag` string COMMENT '????', \n" +
                        "  `t_cdate` string COMMENT '????', \n" +
                        "  `t_date` string COMMENT '????', \n" +
                        "  `t_serialno` string COMMENT '????', \n" +
                        "  `t_agencyno` string COMMENT '?????', \n" +
                        "  `t_netno` string COMMENT '????', \n" +
                        "  `t_fundacco` string COMMENT '????', \n" +
                        "  `t_tradeacco` string COMMENT '????', \n" +
                        "  `t_fundcode` string COMMENT '????', \n" +
                        "  `t_sharetype` string COMMENT '????', \n" +
                        "  `t_confirmbalance` double COMMENT '????', \n" +
                        "  `t_tradefare` double COMMENT '???', \n" +
                        "  `t_backfare` double COMMENT '?????', \n" +
                        "  `t_otherfare1` double COMMENT '????1', \n" +
                        "  `t_remark` string COMMENT '??')\n" +
                        "ROW FORMAT DELIMITED \n" +
                        "  FIELDS TERMINATED BY ',' \n" +
                        "STORED AS INPUTFORMAT \n" +
                        "  'org.apache.hadoop.mapred.TextInputFormat' \n" +
                        "OUTPUTFORMAT \n" +
                        "  'org.apache.hadoop.hive.ql.io.HiveIgnoreKeyTextOutputFormat'\n" +
                        "LOCATION\n" +
                        "  'oss://acs:ram::1:role&role@oss-cn-hangzhou-for-openanalytics/datasets/basic/customer_20180526/trade'\n" +
                        "TBLPROPERTIES (\n" +
                        "  'COLUMN_STATS_ACCURATE'='false', \n" +
                        "  'numFiles'='1', \n" +
                        "  'numRows'='-1', \n" +
                        "  'rawDataSize'='-1', \n" +
                        "  'totalSize'='1870175', \n" +
                        "  'transient_lastDdlTime'='1527408051')"; //

        List<SQLStatement> statementList = SQLUtils.toStatementList(sql, JdbcConstants.HIVE);
        SQLStatement stmt = statementList.get(0);
        System.out.println(stmt.toString());

        Assert.assertEquals(1, statementList.size());

        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(JdbcConstants.HIVE);
        stmt.accept(visitor);

        {
            String text = SQLUtils.toSQLString(stmt, JdbcConstants.HIVE);

            assertEquals("CREATE EXTERNAL TABLE `customer_case.tradelist_csv` (\n" +
                    "\t`t_userid` string COMMENT '??ID',\n" +
                    "\t`t_dealdate` string COMMENT '????',\n" +
                    "\t`t_businflag` string COMMENT '????',\n" +
                    "\t`t_cdate` string COMMENT '????',\n" +
                    "\t`t_date` string COMMENT '????',\n" +
                    "\t`t_serialno` string COMMENT '????',\n" +
                    "\t`t_agencyno` string COMMENT '?????',\n" +
                    "\t`t_netno` string COMMENT '????',\n" +
                    "\t`t_fundacco` string COMMENT '????',\n" +
                    "\t`t_tradeacco` string COMMENT '????',\n" +
                    "\t`t_fundcode` string COMMENT '????',\n" +
                    "\t`t_sharetype` string COMMENT '????',\n" +
                    "\t`t_confirmbalance` double COMMENT '????',\n" +
                    "\t`t_tradefare` double COMMENT '???',\n" +
                    "\t`t_backfare` double COMMENT '?????',\n" +
                    "\t`t_otherfare1` double COMMENT '????1',\n" +
                    "\t`t_remark` string COMMENT '??'\n" +
                    ")\n" +
                    "ROW FORMAT DELIMITED\n" +
                    "\tFIELDS TERMINATED BY ','\n" +
                    "STORED AS\n" +
                    "\tINPUTFORMAT 'org.apache.hadoop.mapred.TextInputFormat'\n" +
                    "\tOUTPUTFORMAT 'org.apache.hadoop.hive.ql.io.HiveIgnoreKeyTextOutputFormat'\n" +
                    "LOCATION 'oss://acs:ram::1:role&role@oss-cn-hangzhou-for-openanalytics/datasets/basic/customer_20180526/trade'\n" +
                    "TBLPROPERTIES (\n" +
                    "\t'COLUMN_STATS_ACCURATE' = 'false',\n" +
                    "\t'numFiles' = '1',\n" +
                    "\t'numRows' = '-1',\n" +
                    "\t'rawDataSize' = '-1',\n" +
                    "\t'totalSize' = '1870175',\n" +
                    "\t'transient_lastDdlTime' = '1527408051'\n" +
                    ")", text);
        }

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());
        System.out.println("orderBy : " + visitor.getOrderByColumns());

        assertEquals(1, visitor.getTables().size());
        assertEquals(17, visitor.getColumns().size());
        assertEquals(0, visitor.getConditions().size());
        assertEquals(0, visitor.getRelationships().size());
        assertEquals(0, visitor.getOrderByColumns().size());

        assertTrue(visitor.containsTable("customer_case.tradelist_csv"));

    }

}
