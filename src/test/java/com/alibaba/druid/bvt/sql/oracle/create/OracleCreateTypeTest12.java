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
package com.alibaba.druid.bvt.sql.oracle.create;

import com.alibaba.druid.sql.OracleTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import com.alibaba.druid.util.JdbcConstants;

import java.util.List;

public class OracleCreateTypeTest12 extends OracleTest {

    public void test_types() throws Exception {
        String sql = "CREATE OR REPLACE\n" +
                "TYPE mybb wrapped\n" +
                "a000000\n" +
                "1\n" +
                "abcd\n" +
                "abcd\n" +
                "abcd\n" +
                "abcd\n" +
                "abcd\n" +
                "abcd\n" +
                "abcd\n" +
                "abcd\n" +
                "abcd\n" +
                "abcd\n" +
                "abcd\n" +
                "abcd\n" +
                "abcd\n" +
                "abcd\n" +
                "abcd\n" +
                "d\n" +
                "c4 ae\n" +
                "4YeykduDwWpKI7JeaWrDQ5SWiVgwg5n0dLhcWvou/6GX6rh0K6W/m8Ayy8xQjwlppZmB8L8z\n" +
                "vbLLUrKeK2fhZ1JJscqkTZ4s6sZ86o5CkZQ1+fmoCEHRnn2mVV986ixuyTCSREZA25hXQFfU\n" +
                "W757xtr4B5ICysO43qOCpqYu0laZ;";

        System.out.println(sql);


        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
        print(statementList);

        assertEquals(1, statementList.size());

        assertEquals("CREATE OR REPLACE TYPE mybb WRAPPED\n" +
                        "a000000\n" +
                        "1\n" +
                        "abcd\n" +
                        "abcd\n" +
                        "abcd\n" +
                        "abcd\n" +
                        "abcd\n" +
                        "abcd\n" +
                        "abcd\n" +
                        "abcd\n" +
                        "abcd\n" +
                        "abcd\n" +
                        "abcd\n" +
                        "abcd\n" +
                        "abcd\n" +
                        "abcd\n" +
                        "abcd\n" +
                        "d\n" +
                        "c4 ae\n" +
                        "4YeykduDwWpKI7JeaWrDQ5SWiVgwg5n0dLhcWvou/6GX6rh0K6W/m8Ayy8xQjwlppZmB8L8z\n" +
                        "vbLLUrKeK2fhZ1JJscqkTZ4s6sZ86o5CkZQ1+fmoCEHRnn2mVV986ixuyTCSREZA25hXQFfU\n" +
                        "W757xtr4B5ICysO43qOCpqYu0laZ;",//
                            SQLUtils.toSQLString(stmt, JdbcConstants.ORACLE));

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        stmt.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());
        System.out.println("orderBy : " + visitor.getOrderByColumns());

        assertEquals(0, visitor.getTables().size());

        assertEquals(0, visitor.getColumns().size());

        SQLUtils.toPGString(stmt);

//        assertTrue(visitor.getColumns().contains(new TableStat.Column("orders", "order_total")));
    }
}
