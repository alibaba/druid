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
package com.alibaba.druid.bvt.sql.h2;

import com.alibaba.druid.sql.OracleTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;
import org.junit.Assert;

import java.util.List;

public class H2_CreateTableTest_0 extends TestCase {

    public void test_0() throws Exception {
        String sql = //
                "CREATE TABLE IF NOT EXISTS `DTS_EXECUTION_COUNTER` (        `id` bigint(20) NOT NULL AUTO_INCREMENT,        `gmt_create` datetime DEFAULT NULL,        `gmt_modified` datetime DEFAULT NULL,        `job_id` bigint(20) DEFAULT NULL,        `job_instance_id` bigint(20) DEFAULT NULL,        `receive_node` varchar(255) DEFAULT NULL,        `task_name` varchar(255) DEFAULT NULL,        `total_counter` bigint(20),        `queued_counter` bigint(20),        `running_counter` bigint(20),        `success_counter` bigint(20),        `fail_counter` bigint(20),        PRIMARY KEY (`id`)        ) ; "; //

        List<SQLStatement> statementList = SQLUtils.toStatementList(sql, JdbcConstants.H2);
        SQLStatement stmt = statementList.get(0);

        Assert.assertEquals(1, statementList.size());

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        stmt.accept(visitor);

        {
            String text = SQLUtils.toSQLString(stmt, JdbcConstants.H2);

            assertEquals("CREATE TABLE IF NOT EXISTS `DTS_EXECUTION_COUNTER` (\n" +
                    "\t`id` bigint(20) AUTO_INCREMENT NOT NULL,\n" +
                    "\t`gmt_create` datetime DEFAULT NULL,\n" +
                    "\t`gmt_modified` datetime DEFAULT NULL,\n" +
                    "\t`job_id` bigint(20) DEFAULT NULL,\n" +
                    "\t`job_instance_id` bigint(20) DEFAULT NULL,\n" +
                    "\t`receive_node` varchar(255) DEFAULT NULL,\n" +
                    "\t`task_name` varchar(255) DEFAULT NULL,\n" +
                    "\t`total_counter` bigint(20),\n" +
                    "\t`queued_counter` bigint(20),\n" +
                    "\t`running_counter` bigint(20),\n" +
                    "\t`success_counter` bigint(20),\n" +
                    "\t`fail_counter` bigint(20),\n" +
                    "\tPRIMARY KEY (`id`)\n" +
                    ");", text);
        }

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());
        System.out.println("orderBy : " + visitor.getOrderByColumns());

        assertEquals(1, visitor.getTables().size());
        assertEquals(12, visitor.getColumns().size());
        assertEquals(0, visitor.getConditions().size());
        assertEquals(0, visitor.getRelationships().size());
        assertEquals(0, visitor.getOrderByColumns().size());

        Assert.assertTrue(visitor.containsTable("DTS_EXECUTION_COUNTER"));

    }
}
