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

public class HiveCreateTableTest_38_dla extends OracleTest {

    public void test_0() throws Exception {
        String sql =  "-- {\"n_nationkey\":0,\"n_name\":\"ALGERIA\",\"n_regionkey\":0,\"n_comment\":\" haggle. carefully final deposits detect slyly agai\"}\n\nCREATE SCHEMA ddl_json_string with DBPROPERTIES(LOCATION = 'oss://oss-cn-hangzhou-for-openanalytics/datasets/tpch/1x/json_string/' ,catalog = 'oss');\nCREATE EXTERNAL TABLE ddl_json_string.nation_json(n_comment string, n_name string, n_nationkey int, n_regionkey int, ) STORED AS JSON LOCATION 'oss://oss-cn-hangzhou-for-openanalytics/datasets/tpch/1x/json_string/nation_json/' TBLPROPERTIES ( \n'skip.header.line.count'='0',\n'recursive.directories'='false');";

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, DbType.hive, SQLParserFeature.KeepComments);

        assertEquals("-- {\"n_nationkey\":0,\"n_name\":\"ALGERIA\",\"n_regionkey\":0,\"n_comment\":\" haggle. carefully final deposits detect slyly agai\"}\n" +
                "CREATE DATABASE ddl_json_string\n" +
                "WITH DBPROPERTIES (\n" +
                "\tLOCATION = 'oss://oss-cn-hangzhou-for-openanalytics/datasets/tpch/1x/json_string/',\n" +
                "\tcatalog = 'oss'\n" +
                ");\n" +
                "\n" +
                "CREATE EXTERNAL TABLE ddl_json_string.nation_json (\n" +
                "\tn_comment string,\n" +
                "\tn_name string,\n" +
                "\tn_nationkey int,\n" +
                "\tn_regionkey int\n" +
                ")\n" +
                "STORED AS JSON\n" +
                "LOCATION 'oss://oss-cn-hangzhou-for-openanalytics/datasets/tpch/1x/json_string/nation_json/'\n" +
                "TBLPROPERTIES (\n" +
                "\t'skip.header.line.count' = '0',\n" +
                "\t'recursive.directories' = 'false'\n" +
                ");", SQLUtils.toSQLString(statementList, JdbcConstants.HIVE));

        SQLStatement stmt = statementList.get(0);
        System.out.println(stmt.toString());

        Assert.assertEquals(2, statementList.size());

        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(JdbcConstants.HIVE);
        stmt.accept(visitor);

        {
            String text = SQLUtils.toSQLString(stmt, JdbcConstants.HIVE);

            assertEquals("-- {\"n_nationkey\":0,\"n_name\":\"ALGERIA\",\"n_regionkey\":0,\"n_comment\":\" haggle. carefully final deposits detect slyly agai\"}\n" +
                    "CREATE DATABASE ddl_json_string\n" +
                    "WITH DBPROPERTIES (\n" +
                    "\tLOCATION = 'oss://oss-cn-hangzhou-for-openanalytics/datasets/tpch/1x/json_string/',\n" +
                    "\tcatalog = 'oss'\n" +
                    ");", text);
        }

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());
        System.out.println("orderBy : " + visitor.getOrderByColumns());

        assertEquals(0, visitor.getTables().size());
        assertEquals(0, visitor.getColumns().size());
        assertEquals(0, visitor.getConditions().size());
        assertEquals(0, visitor.getRelationships().size());
        assertEquals(0, visitor.getOrderByColumns().size());

//        assertTrue(visitor.containsTable("customer_case.tradelist_csv"));

    }

}
