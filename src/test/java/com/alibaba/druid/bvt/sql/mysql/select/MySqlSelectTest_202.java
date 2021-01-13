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
package com.alibaba.druid.bvt.sql.mysql.select;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLCurrentTimeExpr;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

import java.util.List;

public class MySqlSelectTest_202 extends TestCase {

    public void test_0() throws Exception {
        String sql = "SELECT CURRENT_DATE, CURRENT_TIMESTAMP, CURRENT_TIME, CURDATE, LOCALTIME, LOCALTIMESTAMP;";

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
        SQLSelectStatement stmt = (SQLSelectStatement) statementList.get(0);
        assertEquals(SQLCurrentTimeExpr.class, stmt.getSelect().getQueryBlock().getSelectItem(0).getExpr().getClass());
        assertEquals(SQLCurrentTimeExpr.class, stmt.getSelect().getQueryBlock().getSelectItem(1).getExpr().getClass());
        assertEquals(SQLCurrentTimeExpr.class, stmt.getSelect().getQueryBlock().getSelectItem(2).getExpr().getClass());
        assertEquals(SQLCurrentTimeExpr.class, stmt.getSelect().getQueryBlock().getSelectItem(3).getExpr().getClass());
        assertEquals(SQLCurrentTimeExpr.class, stmt.getSelect().getQueryBlock().getSelectItem(4).getExpr().getClass());
        assertEquals(SQLCurrentTimeExpr.class, stmt.getSelect().getQueryBlock().getSelectItem(5).getExpr().getClass());

        assertEquals("SELECT CURRENT_DATE, CURRENT_TIMESTAMP, CURRENT_TIME, CURDATE, LOCALTIME\n" +
                "\t, LOCALTIMESTAMP;", stmt.toString());

        assertEquals("select current_date, current_timestamp, current_time, curdate, localtime\n" +
                "\t, localtimestamp;", stmt.toLowerCaseString());

        assertEquals(1, statementList.size());

        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(JdbcConstants.HIVE);
        stmt.accept(visitor);

//        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("orderBy : " + visitor.getOrderByColumns());
        System.out.println("groupBy : " + visitor.getGroupByColumns());

        assertEquals(0, visitor.getTables().size());
        assertEquals(0, visitor.getColumns().size());
        assertEquals(0, visitor.getConditions().size());
        assertEquals(0, visitor.getGroupByColumns().size());

//        assertTrue(visitor.containsColumn("source", "*"));
    }
}
