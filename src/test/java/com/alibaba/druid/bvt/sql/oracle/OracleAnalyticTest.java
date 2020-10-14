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
package com.alibaba.druid.bvt.sql.oracle;

import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.test.TestUtils;
import junit.framework.TestCase;
import org.junit.Assert;

public class OracleAnalyticTest extends TestCase {

    public void test_0() throws Exception {
        String sql = "SELECT last_name, salary, STDDEV(salary) OVER (ORDER BY hire_date) \"StdDev\" "
                + "FROM employees " + "WHERE department_id = 30;";

        String expect = "SELECT last_name, salary, STDDEV(salary) OVER (ORDER BY hire_date) AS \"StdDev\"\n"
                + "FROM employees\n" + "WHERE department_id = 30;";
        OracleStatementParser parser = new OracleStatementParser(sql);
        SQLSelectStatement stmt = (SQLSelectStatement) parser.parseStatementList().get(0);

        String text = TestUtils.outputOracle(stmt);

        Assert.assertEquals(expect, text);

        System.out.println(text);
    }

    public void test_1() throws Exception {
        String sql = "SELECT submit_date, num_votes, TRUNC(AVG(num_votes) OVER(PARTITION BY submit_date ORDER BY submit_date ROWS UNBOUNDED PRECEDING)) AVG_VOTE_PER_DAY\n"
                + "FROM vote_count\n" + "ORDER BY submit_date;";

        String expect = "SELECT submit_date, num_votes\n" +
                "\t, TRUNC(AVG(num_votes) OVER (PARTITION BY submit_date ORDER BY submit_date ROWS UNBOUNDED PRECEDING)) AS AVG_VOTE_PER_DAY\n" +
                "FROM vote_count\n" +
                "ORDER BY submit_date;";
        OracleStatementParser parser = new OracleStatementParser(sql);
        SQLSelectStatement stmt = (SQLSelectStatement) parser.parseStatementList().get(0);

        String text = TestUtils.outputOracle(stmt);

        Assert.assertEquals(expect, text);

        System.out.println(text);
    }
}
