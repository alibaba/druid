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

public class OracleGroupingSetTest extends TestCase {

    public void test_interval() throws Exception {
        String sql = "SELECT channel_desc, calendar_month_desc, co.country_id, "
                     + "TO_CHAR(sum(amount_sold) , '9,999,999,999') AS SALES$\n"
                     + "FROM sales, customers, times, channels, countries co\n"
                     + "WHERE sales.time_id = times.time_id AND sales.cust_id = customers.cust_id AND sales.channel_id = channels.channel_id "
                     + "AND customers.country_id = co.country_id AND channels.channel_desc IN ('Direct Sales', 'Internet') "
                     + "AND times.calendar_month_desc IN ('2000-09', '2000-10') "
                     + "AND co.country_id IN ('UK', 'US')\n"
                     + "GROUP BY GROUPING SETS((channel_desc, calendar_month_desc, co.country_id), (channel_desc, co.country_id), "
                     + "( calendar_month_desc, co.country_id) );\n";

        String expected = "SELECT channel_desc, calendar_month_desc, co.country_id, " //
                          + "TO_CHAR(SUM(amount_sold), '9,999,999,999') AS SALES$\n" //
                          + "FROM sales, customers, times, channels, countries co\n" //
                          + "WHERE sales.time_id = times.time_id" //
                          + "\n\tAND sales.cust_id = customers.cust_id" //
                          + "\n\tAND sales.channel_id = channels.channel_id" //
                          + "\n\tAND customers.country_id = co.country_id" //
                          + "\n\tAND channels.channel_desc IN ('Direct Sales', 'Internet')" //
                          + "\n\tAND times.calendar_month_desc IN ('2000-09', '2000-10')"
                          + "\n\tAND co.country_id IN ('UK', 'US')\n"
                          + "GROUP BY GROUPING SETS ((channel_desc, calendar_month_desc, co.country_id), (channel_desc, co.country_id), "
                          + "(calendar_month_desc, co.country_id));\n";

        OracleStatementParser parser = new OracleStatementParser(sql);
        SQLSelectStatement stmt = (SQLSelectStatement) parser.parseStatementList().get(0);

        String text = TestUtils.outputOracle(stmt);

        Assert.assertEquals(expected, text);

        System.out.println(text);
    }

}
