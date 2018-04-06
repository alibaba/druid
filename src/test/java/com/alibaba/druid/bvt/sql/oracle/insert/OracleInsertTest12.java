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
package com.alibaba.druid.bvt.sql.oracle.insert;

import java.util.List;

import org.junit.Assert;

import com.alibaba.druid.sql.OracleTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import com.alibaba.druid.util.JdbcConstants;

public class OracleInsertTest12 extends OracleTest {

    public void test_0() throws Exception {
        String sql = "INSERT INTO employees (employee_id, last_name, email, " //
                     + "      hire_date, job_id, salary, commission_pct) "//
                     + "   VALUES (207, 'Gregory', 'pgregory@example.com', "//
                     + "      sysdate, 'PU_CLERK', 1.2E3, NULL);";

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
        print(statementList);

        Assert.assertEquals(1, statementList.size());

        Assert.assertEquals("INSERT INTO employees" //
                            + "\n\t(employee_id, last_name, email, hire_date, job_id" //
                            + "\n\t, salary, commission_pct)" //
                            + "\nVALUES (207, 'Gregory', 'pgregory@example.com', SYSDATE, 'PU_CLERK'" //
                            + "\n\t, 1.2E3, NULL);",//
                            SQLUtils.toSQLString(stmt, JdbcConstants.ORACLE));

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        stmt.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());

        Assert.assertEquals(1, visitor.getTables().size());
        Assert.assertEquals(7, visitor.getColumns().size());

        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("employees")));

         Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("employees", "employee_id")));
    }

}
