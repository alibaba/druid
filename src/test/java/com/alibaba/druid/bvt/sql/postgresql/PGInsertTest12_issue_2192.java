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

public class PGInsertTest12_issue_2192 extends PGTest {

    public void test_0() throws Exception {
        String sql = "insert into test1 as xx (tid,tid1,tvalue)\n" +
                "values (1,1,1),(2,2,2)\n" +
                "on conflict(tid,tid1) do update\n" +
                "set tvalue = xx.tvalue + excluded.tvalue;";

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

        assertTrue(visitor.getTables().containsKey(new TableStat.Name("test1")));
        assertEquals(3, visitor.getColumns().size());
//
//        assertTrue(visitor.getColumns().contains(new TableStat.Column("distributors", "did")));
//        assertTrue(visitor.getColumns().contains(new TableStat.Column("distributors", "dname")));

        assertEquals("INSERT INTO test1 xx (tid, tid1, tvalue)\n" +
                "VALUES (1, 1, 1), \n" +
                "(2, 2, 2)\n" +
                "ON CONFLICT (tid, tid1) UPDATE SET tvalue = xx.tvalue + excluded.tvalue;", stmt.toString());
    }

}
