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
import com.alibaba.druid.util.JdbcConstants;
import org.junit.Assert;

import java.util.List;

public class OracleCreateViewTest9_check_option extends OracleTest {

    public void test_types() throws Exception {
        String sql = //
        " CREATE VIEW clerk AS\n" +
                "   SELECT employee_id, last_name, department_id, job_id \n" +
                "   FROM employees\n" +
                "   WHERE job_id = 'PU_CLERK' \n" +
                "      or job_id = 'SH_CLERK' \n" +
                "      or job_id = 'ST_CLERK'\n" +
                "   WITH CHECK OPTION;";

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
        print(statementList);

        assertEquals(1, statementList.size());

        assertEquals("CREATE VIEW clerk\n" +
                        "AS\n" +
                        "SELECT employee_id, last_name, department_id, job_id\n" +
                        "FROM employees\n" +
                        "WHERE job_id = 'PU_CLERK'\n" +
                        "\tOR job_id = 'SH_CLERK'\n" +
                        "\tOR job_id = 'ST_CLERK'\n" +
                        "WITH CHECK OPTION;",//
                            SQLUtils.toSQLString(stmt, JdbcConstants.ORACLE));

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        stmt.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());
        System.out.println("orderBy : " + visitor.getOrderByColumns());

        assertEquals(1, visitor.getTables().size());

        assertEquals(4, visitor.getColumns().size());

        assertTrue(visitor.getColumns().contains(new TableStat.Column("employees", "last_name")));
    }
}
