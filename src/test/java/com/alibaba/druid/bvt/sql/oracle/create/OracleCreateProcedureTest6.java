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

import java.util.List;

public class OracleCreateProcedureTest6 extends OracleTest {

    public void test_0() throws Exception {
        String sql = "CREATE PROCEDURE wraptest wrapped \n" +
                "a000000\n" +
                "b2\n" +
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
                "7\n" +
                "121 134\n" +
                "Pf3/wD+9ncRZhp3XxTMUO3yIRvswg+nQ7UhqfHRG2vg+SD7x9XzsDUFWbdwCJVEOLKBBRuH6\n" +
                "VMoRHfX6apzfyMkvWhzQLCYvAcq6Zu7++E7PrXNxUJzk/FZW8P9eRgyyyMFnDj53aP1cDje9\n" +
                "ZdGr2VmJHIw0ZNHBYhDdR+du5U5Yy47a6dJHXFW9eNyxBHtXZDuiWYTUtlnueHQV9iYDwE+r\n" +
                "jFn+eZm4jgDcTLTEzfmIVtPDRNhYCY3xhPo7vJeS8M1AvP+4xh9+uO35XsRIsRl1PTFVrGwg\n" +
                "6iuxETwA5Pu2mwx3";
        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        print(statementList);

        assertEquals(1, statementList.size());

        SQLStatement stmt = (SQLStatement) statementList.get(0);

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        stmt.accept(visitor);

        assertEquals("CREATE PROCEDURE wraptest WRAPPED \n" +
                "b2\n" +
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
                "7\n" +
                "121 134\n" +
                "Pf3/wD+9ncRZhp3XxTMUO3yIRvswg+nQ7UhqfHRG2vg+SD7x9XzsDUFWbdwCJVEOLKBBRuH6\n" +
                "VMoRHfX6apzfyMkvWhzQLCYvAcq6Zu7++E7PrXNxUJzk/FZW8P9eRgyyyMFnDj53aP1cDje9\n" +
                "ZdGr2VmJHIw0ZNHBYhDdR+du5U5Yy47a6dJHXFW9eNyxBHtXZDuiWYTUtlnueHQV9iYDwE+r\n" +
                "jFn+eZm4jgDcTLTEzfmIVtPDRNhYCY3xhPo7vJeS8M1AvP+4xh9+uO35XsRIsRl1PTFVrGwg\n" +
                "6iuxETwA5Pu2mwx3", stmt.toString());

        assertEquals("CREATE PROCEDURE wraptest WRAPPED \n" +
                "b2\n" +
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
                "7\n" +
                "121 134\n" +
                "Pf3/wD+9ncRZhp3XxTMUO3yIRvswg+nQ7UhqfHRG2vg+SD7x9XzsDUFWbdwCJVEOLKBBRuH6\n" +
                "VMoRHfX6apzfyMkvWhzQLCYvAcq6Zu7++E7PrXNxUJzk/FZW8P9eRgyyyMFnDj53aP1cDje9\n" +
                "ZdGr2VmJHIw0ZNHBYhDdR+du5U5Yy47a6dJHXFW9eNyxBHtXZDuiWYTUtlnueHQV9iYDwE+r\n" +
                "jFn+eZm4jgDcTLTEzfmIVtPDRNhYCY3xhPo7vJeS8M1AvP+4xh9+uO35XsRIsRl1PTFVrGwg\n" +
                "6iuxETwA5Pu2mwx3", SQLUtils.toPGString(stmt));

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
