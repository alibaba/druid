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
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import org.junit.Assert;

import java.util.List;

public class OracleSelectTest62 extends OracleTest {

    public void test_0() throws Exception {
        String sql = //
                "SELECT channel_desc, calendar_month_desc, co.country_id,\n" +
                        "      TO_CHAR(sum(amount_sold) , '9,999,999,999') SALES$\n" +
                        "   FROM sales, customers, times, channels, countries co\n" +
                        "   WHERE sales.time_id=times.time_id \n" +
                        "      AND sales.cust_id=customers.cust_id \n" +
                        "      AND sales.channel_id= channels.channel_id \n" +
                        "      AND customers.country_id = co.country_id\n" +
                        "      AND channels.channel_desc IN ('Direct Sales', 'Internet') \n" +
                        "      AND times.calendar_month_desc IN ('2000-09', '2000-10')\n" +
                        "      AND co.country_iso_code IN ('UK', 'US')\n" +
                        "  GROUP BY GROUPING SETS( \n" +
                        "      (channel_desc, calendar_month_desc, co.country_id), \n" +
                        "      (channel_desc, co.country_id), \n" +
                        "      (calendar_month_desc, co.country_id) );"; //

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
        print(statementList);

        Assert.assertEquals(1, statementList.size());

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        stmt.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());
        System.out.println("orderBy : " + visitor.getOrderByColumns());

        Assert.assertEquals(5, visitor.getTables().size());

        Assert.assertEquals(14, visitor.getColumns().size());

        {
            String text = SQLUtils.toOracleString(stmt);

            assertEquals("SELECT channel_desc, calendar_month_desc, co.country_id\n" +
                    "\t, TO_CHAR(sum(amount_sold), '9,999,999,999') AS SALES$\n" +
                    "FROM sales, customers, times, channels, countries co\n" +
                    "WHERE sales.time_id = times.time_id\n" +
                    "\tAND sales.cust_id = customers.cust_id\n" +
                    "\tAND sales.channel_id = channels.channel_id\n" +
                    "\tAND customers.country_id = co.country_id\n" +
                    "\tAND channels.channel_desc IN ('Direct Sales', 'Internet')\n" +
                    "\tAND times.calendar_month_desc IN ('2000-09', '2000-10')\n" +
                    "\tAND co.country_iso_code IN ('UK', 'US')\n" +
                    "GROUP BY GROUPING SETS ((channel_desc, calendar_month_desc, co.country_id), (channel_desc, co.country_id), (calendar_month_desc, co.country_id));", text);
        }

        {
            String text = SQLUtils.toOracleString(stmt, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION);

            Assert.assertEquals("select channel_desc, calendar_month_desc, co.country_id\n" +
                    "\t, TO_CHAR(sum(amount_sold), '9,999,999,999') as SALES$\n" +
                    "from sales, customers, times, channels, countries co\n" +
                    "where sales.time_id = times.time_id\n" +
                    "\tand sales.cust_id = customers.cust_id\n" +
                    "\tand sales.channel_id = channels.channel_id\n" +
                    "\tand customers.country_id = co.country_id\n" +
                    "\tand channels.channel_desc in ('Direct Sales', 'Internet')\n" +
                    "\tand times.calendar_month_desc in ('2000-09', '2000-10')\n" +
                    "\tand co.country_iso_code in ('UK', 'US')\n" +
                    "group by grouping sets ((channel_desc, calendar_month_desc, co.country_id), (channel_desc, co.country_id), (calendar_month_desc, co.country_id));", text);
        }
        // Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("acduser.vw_acd_info", "xzqh")));

        // Assert.assertTrue(visitor.getOrderByColumns().contains(new TableStat.Column("employees", "last_name")));
    }
}
