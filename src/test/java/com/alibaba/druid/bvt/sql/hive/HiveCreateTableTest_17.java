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

public class HiveCreateTableTest_17 extends OracleTest {

    public void test_0() throws Exception {
        String sql = //
                "create table dim_ad_sem_kw_intl(\n" +
                        "id bigint,\n" +
                        "iid string,\n" +
                        "url_domain string,\n" +
                        "sem_domain string,\n" +
                        "keyword string,\n" +
                        "landing_page string,\n" +
                        "campaign_type string,\n" +
                        "create_user string,\n" +
                        "gmt_created timestamp,\n" +
                        "campaign_url string,\n" +
                        "is_deleted bigint,\n" +
                        "campaign_plan string,\n" +
                        "campaign_name string\n" +
                        ") partitioned by(ds string) stored as ORC;"; //

        List<SQLStatement> statementList = SQLUtils.toStatementList(sql, JdbcConstants.HIVE);
        SQLStatement stmt = statementList.get(0);
        System.out.println(stmt.toString());

        Assert.assertEquals(1, statementList.size());

        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(JdbcConstants.HIVE);
        stmt.accept(visitor);

        {
            String text = SQLUtils.toSQLString(stmt, JdbcConstants.HIVE);

            assertEquals("CREATE TABLE dim_ad_sem_kw_intl (\n" +
                    "\tid bigint,\n" +
                    "\tiid string,\n" +
                    "\turl_domain string,\n" +
                    "\tsem_domain string,\n" +
                    "\tkeyword string,\n" +
                    "\tlanding_page string,\n" +
                    "\tcampaign_type string,\n" +
                    "\tcreate_user string,\n" +
                    "\tgmt_created timestamp,\n" +
                    "\tcampaign_url string,\n" +
                    "\tis_deleted bigint,\n" +
                    "\tcampaign_plan string,\n" +
                    "\tcampaign_name string\n" +
                    ")\n" +
                    "PARTITIONED BY (\n" +
                    "\tds string\n" +
                    ")\n" +
                    "STORED AS ORC;", text);
        }

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());
        System.out.println("orderBy : " + visitor.getOrderByColumns());

        assertEquals(1, visitor.getTables().size());
        assertEquals(13, visitor.getColumns().size());
        assertEquals(0, visitor.getConditions().size());
        assertEquals(0, visitor.getRelationships().size());
        assertEquals(0, visitor.getOrderByColumns().size());

        assertTrue(visitor.containsTable("dim_ad_sem_kw_intl"));

    }
}
