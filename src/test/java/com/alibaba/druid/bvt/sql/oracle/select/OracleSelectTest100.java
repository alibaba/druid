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
package com.alibaba.druid.bvt.sql.oracle.select;

import com.alibaba.druid.sql.OracleTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import org.junit.Assert;

import java.util.List;

public class OracleSelectTest100 extends OracleTest {

    public void test_0() throws Exception {
        String sql = //
                "SELECT EXTRACT(YEAR FROM SYSTIMESTAMP) AS \"YEAR\" FROM DUAL;\n" +
                        "SELECT EXTRACT(MONTH FROM SYSTIMESTAMP) \"MONTH\" FROM DUAL;" +
                        "SELECT EXTRACT(DAY  FROM SYSTIMESTAMP)  AS \"DAY\" FROM DUAL;" +
                        "SELECT EXTRACT(HOUR  FROM SYSTIMESTAMP)  AS \"HOUR\" FROM DUAL;" +
                        "SELECT EXTRACT(MINUTE  FROM SYSTIMESTAMP)  AS \"MINUTE\" FROM DUAL;" +
                        "SELECT EXTRACT(SECOND  FROM SYSTIMESTAMP)  AS \"SECOND\" FROM DUAL;" +
                        "SELECT EXTRACT(TIMEZONE_HOUR  FROM SYSTIMESTAMP)  AS \"TIMEZONE_HOUR\" FROM DUAL;" +
                        "SELECT EXTRACT(TIMEZONE_MINUTE  FROM SYSTIMESTAMP)  AS \"TIMEZONE_MINUTE\" FROM DUAL;" +
                        "SELECT EXTRACT(TIMEZONE_REGION FROM SYSTIMESTAMP)  AS \"TIMEZONE_REGION\" FROM DUAL;" +
                        "SELECT EXTRACT(TIMEZONE_ABBR FROM SYSTIMESTAMP)  AS \"TIMEZONE_ABBR\" FROM DUAL;";
        System.out.println(sql);

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLSelectStatement stmt = (SQLSelectStatement) statementList.get(0);
        System.out.println(stmt.toString());

        Assert.assertEquals(10, statementList.size());

//        SQLMethodInvokeExpr expr = (SQLMethodInvokeExpr) stmt.getSelect().getQueryBlock().getSelectList().get(0).getExpr();
//        SQLMethodInvokeExpr param0 = (SQLMethodInvokeExpr) expr.getParameters().get(0);
//        assertTrue(param0.getParameters().get(0)
//                instanceof SQLAggregateExpr);

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        stmt.accept(visitor);

        {
            String text = SQLUtils.toOracleString(stmt);

            assertEquals("SELECT last_name, hire_date, salary, SUM(salary) OVER (ORDER BY hire_date RANGE NUMTOYMINTERVAL(1, 'year') PRECEDING) AS t_sal\n" +
                    "FROM employees;", text);
        }

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());
        System.out.println("orderBy : " + visitor.getOrderByColumns());

        assertEquals(1, visitor.getTables().size());
        assertEquals(3, visitor.getColumns().size());
        assertEquals(0, visitor.getConditions().size());
        assertEquals(0, visitor.getRelationships().size());
        assertEquals(1, visitor.getOrderByColumns().size());
    }

   
}
