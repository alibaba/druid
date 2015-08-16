/*
 * Copyright 1999-2101 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.bvt.sql.oracle;

import org.junit.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.test.TestUtils;

public class OraclePriorTest extends TestCase {

    public void test_oracle() throws Exception {
        String sql = "SELECT employee_id, last_name, manager_id FROM employees CONNECT BY PRIOR employee_id = manager_id;";

        String expect = "SELECT employee_id, last_name, manager_id\n" + "FROM employees\n"
                        + "CONNECT BY PRIOR employee_id = manager_id;\n";

        OracleStatementParser parser = new OracleStatementParser(sql);
        SQLSelectStatement stmt = (SQLSelectStatement) parser.parseStatementList().get(0);

        String text = TestUtils.outputOracle(stmt);

        Assert.assertEquals(expect, text);

        System.out.println(text);
    }

    public void test_oracle_2() throws Exception {
        String sql = "SELECT last_name, employee_id, manager_id, LEVEL\n" + "FROM employees\n"
                     + "START WITH employee_id = 100\n" + "CONNECT BY PRIOR employee_id = manager_id\n"
                     + "ORDER SIBLINGS BY last_name;";

        String expect = "SELECT last_name, employee_id, manager_id, LEVEL\n" + "FROM employees\n"
                        + "START WITH employee_id = 100\n" + "CONNECT BY PRIOR employee_id = manager_id\n"
                        + "ORDER SIBLINGS BY last_name;\n";

        OracleStatementParser parser = new OracleStatementParser(sql);
        SQLSelectStatement stmt = (SQLSelectStatement) parser.parseStatementList().get(0);

        String text = TestUtils.outputOracle(stmt);

        Assert.assertEquals(expect, text);

        System.out.println(text);
    }
}
