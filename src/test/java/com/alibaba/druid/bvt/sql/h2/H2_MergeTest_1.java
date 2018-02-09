/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
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
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.util.JdbcConstants;
import org.junit.Assert;

import java.util.List;

public class H2_MergeTest_1 extends OracleTest {

    public void test_0() throws Exception {
        String sql = //
                "MERGE INTO CUSTOMER KEY (ID) VALUES (8, 'Loki', 32, 'Hyderabad', 3000);"; //

//        System.out.println(sql);

        List<SQLStatement> stmtList = SQLUtils.toStatementList(sql, JdbcConstants.H2);
        SQLStatement stmt = stmtList.get(0);

        Assert.assertEquals(1, stmtList.size());

        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(JdbcConstants.H2);
        stmt.accept(visitor);

        {
            String text = SQLUtils.toSQLString(stmt, JdbcConstants.H2);

            assertEquals("MERGE INTO CUSTOMER KEY (ID)\n" +
                    "VALUES (8, 'Loki', 32, 'Hyderabad', 3000);", text);
        }

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());
        System.out.println("orderBy : " + visitor.getOrderByColumns());

        assertEquals(1, visitor.getTables().size());
        assertEquals(1, visitor.getColumns().size());
        assertEquals(0, visitor.getConditions().size());
        assertEquals(0, visitor.getRelationships().size());
        assertEquals(0, visitor.getOrderByColumns().size());

        Assert.assertTrue(visitor.containsTable("CUSTOMER"));

    }
}
