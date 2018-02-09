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
package com.alibaba.druid.bvt.sql.postgresql;

import com.alibaba.druid.sql.PGTest;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.postgresql.parser.PGSQLStatementParser;
import com.alibaba.druid.sql.dialect.postgresql.visitor.PGSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;

import java.util.List;

public class PGInsertTest13 extends PGTest {

    public void test_0() throws Exception {
        String sql = "INSERT INTO profile_group_execution(\n" +
                "     \n" +
                "     gmt_create, gmt_modified \n" +
                "    ,\n" +
                "     \n" +
                "    group_id, mode, sql, status, count, error_msg, error_code\n" +
                "   \n" +
                "   \n" +
                "    )\n" +
                "    VALUES (\n" +
                "     \n" +
                "         \n" +
                "    now(), now(), ?, ?, ?, ?, ?, ?, ?\n" +
                "     \n" +
                "   \n" +
                "    )\n" +
                "    RETURNING\n" +
                "     \n" +
                "     \n" +
                "    id,\n" +
                "     gmt_create, gmt_modified \n" +
                "   \n" +
                "    ,\n" +
                "     \n" +
                "    group_id, mode, sql, status, count, error_msg, error_code";

        PGSQLStatementParser parser = new PGSQLStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
//        print(statementList);

        assertEquals(1, statementList.size());

        PGSchemaStatVisitor visitor = new PGSchemaStatVisitor();
        stmt.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
//        System.out.println("coditions : " + visitor.getConditions());

        assertTrue(visitor.containsTable("profile_group_execution"));
        assertEquals(9, visitor.getColumns().size());
//
//        assertTrue(visitor.getColumns().contains(new TableStat.Column("distributors", "did")));
//        assertTrue(visitor.getColumns().contains(new TableStat.Column("distributors", "dname")));

        assertEquals("INSERT INTO profile_group_execution\n" +
                "\t(gmt_create, gmt_modified, group_id, mode, sql\n" +
                "\t, status, count, error_msg, error_code)\n" +
                "VALUES (now(), now(), ?, ?, ?\n" +
                "\t, ?, ?, ?, ?)\n" +
                "RETURNING (id, gmt_create, gmt_modified, group_id, mode, sql, status, count, error_msg, error_code)", stmt.toString());
    }

}
