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
package com.alibaba.druid.bvt.sql.oracle.create;

import com.alibaba.druid.sql.OracleTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;

import java.util.List;

public class OracleCreateProcedureTest5 extends OracleTest {

    public void test_0() throws Exception {
        String sql = "CREATE OR REPLACE PROCEDURE proc_helloworld\n" +
                "IS\n" +
                "BEGIN\n" +
                "   DBMS_OUTPUT.put_line ('Hello World!');\n" +
                "END;";
        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        print(statementList);

        assertEquals(1, statementList.size());

        SQLStatement stmt = (SQLStatement) statementList.get(0);

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        stmt.accept(visitor);

        assertEquals("CREATE OR REPLACE PROCEDURE proc_helloworld\n" +
                "IS\n" +
                "BEGIN\n" +
                "\tDBMS_OUTPUT.put_line('Hello World!');\n" +
                "END;", stmt.toString());

        assertEquals("CREATE OR REPLACE PROCEDURE proc_helloworld\n" +
                "IS\n" +
                "BEGIN\n" +
                "\tDBMS_OUTPUT.put_line('Hello World!');\n" +
                "END;", SQLUtils.toPGString(stmt));

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());
        System.out.println("orderBy : " + visitor.getOrderByColumns());

        assertEquals(0, visitor.getTables().size());

//        assertTrue(visitor.getTables().containsKey(new TableStat.Name("fact_brand_provider")));

        assertEquals(0, visitor.getColumns().size());
        assertEquals(0, visitor.getConditions().size());
        assertEquals(0, visitor.getRelationships().size());

//        assertTrue(visitor.containsColumn("fact_brand_provider", "gyscode"));
//        assertTrue(visitor.containsColumn("fact_brand_provider", "gysname"));
    }
}
