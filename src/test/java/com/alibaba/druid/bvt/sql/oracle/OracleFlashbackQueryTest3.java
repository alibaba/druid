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
package com.alibaba.druid.bvt.sql.oracle;

import org.junit.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.test.TestUtils;

public class OracleFlashbackQueryTest3 extends TestCase {

    public void test_isEmpty() throws Exception {
        String sql = "SELECT DECODE(GROUPING(department_name), 1, 'All Departments', department_name) AS department,"
                     + "DECODE(GROUPING(job_id), 1, 'All Jobs', job_id) AS job, COUNT(*) AS \"Total Empl\", AVG(salary) * 12 AS \"Average Sal\" "
                     + "FROM employees e, departments d\n" + "WHERE d.department_id = e.department_id\n"
                     + "GROUP BY ROLLUP (department_name, job_id);\n";

        String expect = "SELECT DECODE(GROUPING(department_name), 1, 'All Departments', department_name) AS department\n" +
                "\t, DECODE(GROUPING(job_id), 1, 'All Jobs', job_id) AS job\n" +
                "\t, COUNT(*) AS \"Total Empl\"\n" +
                "\t, AVG(salary) * 12 AS \"Average Sal\"\n" +
                "FROM employees e, departments d\n" +
                "WHERE d.department_id = e.department_id\n" +
                "GROUP BY ROLLUP (department_name, job_id);";

        OracleStatementParser parser = new OracleStatementParser(sql);
        SQLSelectStatement stmt = (SQLSelectStatement) parser.parseStatementList().get(0);

        String text = TestUtils.outputOracle(stmt);

        assertEquals(expect, text);

        System.out.println(text);
    }
}
