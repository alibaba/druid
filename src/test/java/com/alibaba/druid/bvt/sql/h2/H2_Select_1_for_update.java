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

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

import java.util.List;

public class H2_Select_1_for_update extends TestCase {

    public void test_0() throws Exception {
        String sql = //
                "SELECT * FROM QRTZ_LOCKS WHERE SCHED_NAME = 'adminQuartzScheduler' AND LOCK_NAME = ? FOR UPDATE"; //

//        System.out.println(sql);

        List<SQLStatement> stmtList = SQLUtils.toStatementList(sql, JdbcConstants.H2);
        SQLStatement stmt = stmtList.get(0);

        assertEquals(1, stmtList.size());

        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(JdbcConstants.H2);
        stmt.accept(visitor);

        assertEquals("SELECT *\n" +
                "FROM QRTZ_LOCKS\n" +
                "WHERE SCHED_NAME = 'adminQuartzScheduler'\n" +
                "\tAND LOCK_NAME = ?\n" +
                "FOR UPDATE", stmt.toString());

        assertEquals("select *\n" +
                "from QRTZ_LOCKS\n" +
                "where SCHED_NAME = 'adminQuartzScheduler'\n" +
                "\tand LOCK_NAME = ?\n" +
                "for update", stmt.toLowerCaseString());

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());
        System.out.println("orderBy : " + visitor.getOrderByColumns());

        assertEquals(1, visitor.getTables().size());
        assertEquals(3, visitor.getColumns().size());
        assertEquals(2, visitor.getConditions().size());
        assertEquals(0, visitor.getRelationships().size());
        assertEquals(0, visitor.getOrderByColumns().size());

        assertTrue(visitor.containsTable("QRTZ_LOCKS"));

    }
}
