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

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

import java.util.List;

public class HiveSelectTest_41 extends TestCase {

    public void test_0() throws Exception {
        String sql = "select\n" +
                "field['id'].string_type as banner_id\n" +
                ",field['index'].string_type as banner_index\n" +
                ",field['pageName'].string_type as pageName\n" +
                "from\n" +
                "bdm_book.midu_xcx_web_main_log\n" +
                "where day = '2019-01-09'\n" +
                "and eventid = '151'\n" +
                "and cmd in ('26002','26003')\n" +
                "group by field['id'].string_type\n" +
                ",field['index'].string_type\n" +
                ",field['pageName'].string_type ;";

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.HIVE);
        SQLSelectStatement stmt = (SQLSelectStatement) statementList.get(0);

        assertEquals("SELECT field['id'].string_type AS banner_id, field['index'].string_type AS banner_index, field['pageName'].string_type AS pageName\n" +
                "FROM bdm_book.midu_xcx_web_main_log\n" +
                "WHERE day = '2019-01-09'\n" +
                "\tAND eventid = '151'\n" +
                "\tAND cmd IN ('26002', '26003')\n" +
                "GROUP BY field['id'].string_type, field['index'].string_type, field['pageName'].string_type;", stmt.toString());

        assertEquals("select field['id'].string_type as banner_id, field['index'].string_type as banner_index, field['pageName'].string_type as pageName\n" +
                "from bdm_book.midu_xcx_web_main_log\n" +
                "where day = '2019-01-09'\n" +
                "\tand eventid = '151'\n" +
                "\tand cmd in ('26002', '26003')\n" +
                "group by field['id'].string_type, field['index'].string_type, field['pageName'].string_type;", stmt.toLowerCaseString());

        assertEquals(1, statementList.size());

        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(JdbcConstants.HIVE);
        stmt.accept(visitor);

//        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("orderBy : " + visitor.getOrderByColumns());
        System.out.println("groupBy : " + visitor.getGroupByColumns());

        assertEquals(1, visitor.getTables().size());
        assertEquals(4, visitor.getColumns().size());
        assertEquals(3, visitor.getConditions().size());
        assertEquals(0, visitor.getGroupByColumns().size());

//        assertTrue(visitor.containsColumn("source", "*"));
    }
}
