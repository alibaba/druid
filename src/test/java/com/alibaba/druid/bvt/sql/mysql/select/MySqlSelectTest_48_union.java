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
package com.alibaba.druid.bvt.sql.mysql.select;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.util.JdbcConstants;
import org.junit.Assert;

import java.util.List;

public class MySqlSelectTest_48_union extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "select sum(hd.paid_amount) from\n" +
                "(\n" +
                "SELECT 'fl',cast(a.hosted_ymd as date) as hosted_ymd,a.user_id,'boss1',a.paid_amount,m.user_id,m.create_date,m.pmcode,n.type_name,n.product_name,n.acctype_id,n.acctype_name,n.account_name,n.plan\n" +
                "FROM hive.bdc_dwd.dw_mk_copyright_order a\n" +
                "left join hive.bdc_dwd.dw_mk_chance b on (a.chance_id=b.chance_id and b.acct_day='03')\n" +
                "left join hive.bdc_dwd.dw_lg_stat_user m on (a.user_id =m.user_id and m.acct_day='03')\n" +
                "left join hive.bdc_dwd.dw_lg_pmcode n on (m.pmcode=n.id and n.acct_day='03')\n" +
                "where a.hosted_ymd between '2016-01-01' and '2017-06-30' and b.chance_type_group=3\n" +
                "and a.acct_day='03'\n" +
                "union\n" +
                "select 'fl',b.paid_date as hosted_ymd,b.user_id,'boss2',b.paid_amount,m.user_id,m.create_date,m.pmcode,n.type_name,n.product_name,n.acctype_id,n.acctype_name,n.account_name,n.plan\n" +
                "from hive.bdc_dwd.dw_fx_chance a\n" +
                "left join hive.bdc_dwd.dw_fx_chance_order b on (a.chance_id=b.chance_id and b.acct_day='03')\n" +
                "left join hive.bdc_dwd.dw_lg_stat_user m on (a.user_id =m.user_id and m.acct_day='03')\n" +
                "left join hive.bdc_dwd.dw_lg_pmcode n on (m.pmcode=n.id and n.acct_day='03')\n" +
                "where a.project_id=1 and b.paid_amount>0 and cast(b.paid_date as varchar(10))>='2016-01-01' and\n" +
                "cast(b.paid_date as varchar(10))<='2017-06-30' and b.state=1 and a.acct_day='03'\n" +
                ")hd";


        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL, true);
        SQLStatement stmt = statementList.get(0);

        assertEquals(1, statementList.size());

        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(JdbcConstants.MYSQL);
        stmt.accept(visitor);

//        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
//        System.out.println("coditions : " + visitor.getConditions());
//        System.out.println("orderBy : " + visitor.getOrderByColumns());
        
//        assertEquals(1, visitor.getTables().size());
//        assertEquals(1, visitor.getColumns().size());
//        assertEquals(0, visitor.getConditions().size());
//        assertEquals(0, visitor.getOrderByColumns().size());
        
        {
            String output = SQLUtils.toMySqlString(stmt);
            assertEquals("SELECT SUM(hd.paid_amount)\n" +
                            "FROM (\n" +
                            "\tSELECT 'fl', CAST(a.hosted_ymd AS date) AS hosted_ymd, a.user_id, 'boss1', a.paid_amount\n" +
                            "\t\t, m.user_id, m.create_date, m.pmcode, n.type_name, n.product_name\n" +
                            "\t\t, n.acctype_id, n.acctype_name, n.account_name, n.plan\n" +
                            "\tFROM hive.bdc_dwd.dw_mk_copyright_order a\n" +
                            "\t\tLEFT JOIN hive.bdc_dwd.dw_mk_chance b\n" +
                            "\t\tON a.chance_id = b.chance_id\n" +
                            "\t\t\tAND b.acct_day = '03'\n" +
                            "\t\tLEFT JOIN hive.bdc_dwd.dw_lg_stat_user m\n" +
                            "\t\tON a.user_id = m.user_id\n" +
                            "\t\t\tAND m.acct_day = '03'\n" +
                            "\t\tLEFT JOIN hive.bdc_dwd.dw_lg_pmcode n\n" +
                            "\t\tON m.pmcode = n.id\n" +
                            "\t\t\tAND n.acct_day = '03'\n" +
                            "\tWHERE a.hosted_ymd BETWEEN '2016-01-01' AND '2017-06-30'\n" +
                            "\t\tAND b.chance_type_group = 3\n" +
                            "\t\tAND a.acct_day = '03'\n" +
                            "\tUNION\n" +
                            "\tSELECT 'fl', b.paid_date AS hosted_ymd, b.user_id, 'boss2', b.paid_amount\n" +
                            "\t\t, m.user_id, m.create_date, m.pmcode, n.type_name, n.product_name\n" +
                            "\t\t, n.acctype_id, n.acctype_name, n.account_name, n.plan\n" +
                            "\tFROM hive.bdc_dwd.dw_fx_chance a\n" +
                            "\t\tLEFT JOIN hive.bdc_dwd.dw_fx_chance_order b\n" +
                            "\t\tON a.chance_id = b.chance_id\n" +
                            "\t\t\tAND b.acct_day = '03'\n" +
                            "\t\tLEFT JOIN hive.bdc_dwd.dw_lg_stat_user m\n" +
                            "\t\tON a.user_id = m.user_id\n" +
                            "\t\t\tAND m.acct_day = '03'\n" +
                            "\t\tLEFT JOIN hive.bdc_dwd.dw_lg_pmcode n\n" +
                            "\t\tON m.pmcode = n.id\n" +
                            "\t\t\tAND n.acct_day = '03'\n" +
                            "\tWHERE a.project_id = 1\n" +
                            "\t\tAND b.paid_amount > 0\n" +
                            "\t\tAND CAST(b.paid_date AS varchar(10)) >= '2016-01-01'\n" +
                            "\t\tAND CAST(b.paid_date AS varchar(10)) <= '2017-06-30'\n" +
                            "\t\tAND b.state = 1\n" +
                            "\t\tAND a.acct_day = '03'\n" +
                            ") hd", //
                                output);
        }
        {
            String output = SQLUtils.toMySqlString(stmt, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION);
            assertEquals("select sum(hd.paid_amount)\n" +
                            "from (\n" +
                            "\tselect 'fl', cast(a.hosted_ymd as date) as hosted_ymd, a.user_id, 'boss1', a.paid_amount\n" +
                            "\t\t, m.user_id, m.create_date, m.pmcode, n.type_name, n.product_name\n" +
                            "\t\t, n.acctype_id, n.acctype_name, n.account_name, n.plan\n" +
                            "\tfrom hive.bdc_dwd.dw_mk_copyright_order a\n" +
                            "\t\tleft join hive.bdc_dwd.dw_mk_chance b\n" +
                            "\t\ton a.chance_id = b.chance_id\n" +
                            "\t\t\tand b.acct_day = '03'\n" +
                            "\t\tleft join hive.bdc_dwd.dw_lg_stat_user m\n" +
                            "\t\ton a.user_id = m.user_id\n" +
                            "\t\t\tand m.acct_day = '03'\n" +
                            "\t\tleft join hive.bdc_dwd.dw_lg_pmcode n\n" +
                            "\t\ton m.pmcode = n.id\n" +
                            "\t\t\tand n.acct_day = '03'\n" +
                            "\twhere a.hosted_ymd between '2016-01-01' and '2017-06-30'\n" +
                            "\t\tand b.chance_type_group = 3\n" +
                            "\t\tand a.acct_day = '03'\n" +
                            "\tunion\n" +
                            "\tselect 'fl', b.paid_date as hosted_ymd, b.user_id, 'boss2', b.paid_amount\n" +
                            "\t\t, m.user_id, m.create_date, m.pmcode, n.type_name, n.product_name\n" +
                            "\t\t, n.acctype_id, n.acctype_name, n.account_name, n.plan\n" +
                            "\tfrom hive.bdc_dwd.dw_fx_chance a\n" +
                            "\t\tleft join hive.bdc_dwd.dw_fx_chance_order b\n" +
                            "\t\ton a.chance_id = b.chance_id\n" +
                            "\t\t\tand b.acct_day = '03'\n" +
                            "\t\tleft join hive.bdc_dwd.dw_lg_stat_user m\n" +
                            "\t\ton a.user_id = m.user_id\n" +
                            "\t\t\tand m.acct_day = '03'\n" +
                            "\t\tleft join hive.bdc_dwd.dw_lg_pmcode n\n" +
                            "\t\ton m.pmcode = n.id\n" +
                            "\t\t\tand n.acct_day = '03'\n" +
                            "\twhere a.project_id = 1\n" +
                            "\t\tand b.paid_amount > 0\n" +
                            "\t\tand cast(b.paid_date as varchar(10)) >= '2016-01-01'\n" +
                            "\t\tand cast(b.paid_date as varchar(10)) <= '2017-06-30'\n" +
                            "\t\tand b.state = 1\n" +
                            "\t\tand a.acct_day = '03'\n" +
                            ") hd", //
                                output);
        }

        {
            String output = SQLUtils.toMySqlString(stmt, new SQLUtils.FormatOption(true, true, true));
            assertEquals("SELECT SUM(hd.paid_amount)\n" +
                            "FROM (\n" +
                            "\tSELECT ?, CAST(a.hosted_ymd AS date) AS hosted_ymd, a.user_id, ?, a.paid_amount\n" +
                            "\t\t, m.user_id, m.create_date, m.pmcode, n.type_name, n.product_name\n" +
                            "\t\t, n.acctype_id, n.acctype_name, n.account_name, n.plan\n" +
                            "\tFROM hive.bdc_dwd.dw_mk_copyright_order a\n" +
                            "\t\tLEFT JOIN hive.bdc_dwd.dw_mk_chance b\n" +
                            "\t\tON a.chance_id = b.chance_id\n" +
                            "\t\t\tAND b.acct_day = ?\n" +
                            "\t\tLEFT JOIN hive.bdc_dwd.dw_lg_stat_user m\n" +
                            "\t\tON a.user_id = m.user_id\n" +
                            "\t\t\tAND m.acct_day = ?\n" +
                            "\t\tLEFT JOIN hive.bdc_dwd.dw_lg_pmcode n\n" +
                            "\t\tON m.pmcode = n.id\n" +
                            "\t\t\tAND n.acct_day = ?\n" +
                            "\tWHERE a.hosted_ymd BETWEEN ? AND ?\n" +
                            "\t\tAND b.chance_type_group = ?\n" +
                            "\t\tAND a.acct_day = ?\n" +
                            "\tUNION\n" +
                            "\tSELECT ?, b.paid_date AS hosted_ymd, b.user_id, ?, b.paid_amount\n" +
                            "\t\t, m.user_id, m.create_date, m.pmcode, n.type_name, n.product_name\n" +
                            "\t\t, n.acctype_id, n.acctype_name, n.account_name, n.plan\n" +
                            "\tFROM hive.bdc_dwd.dw_fx_chance a\n" +
                            "\t\tLEFT JOIN hive.bdc_dwd.dw_fx_chance_order b\n" +
                            "\t\tON a.chance_id = b.chance_id\n" +
                            "\t\t\tAND b.acct_day = ?\n" +
                            "\t\tLEFT JOIN hive.bdc_dwd.dw_lg_stat_user m\n" +
                            "\t\tON a.user_id = m.user_id\n" +
                            "\t\t\tAND m.acct_day = ?\n" +
                            "\t\tLEFT JOIN hive.bdc_dwd.dw_lg_pmcode n\n" +
                            "\t\tON m.pmcode = n.id\n" +
                            "\t\t\tAND n.acct_day = ?\n" +
                            "\tWHERE a.project_id = ?\n" +
                            "\t\tAND b.paid_amount > ?\n" +
                            "\t\tAND CAST(b.paid_date AS varchar(10)) >= ?\n" +
                            "\t\tAND CAST(b.paid_date AS varchar(10)) <= ?\n" +
                            "\t\tAND b.state = ?\n" +
                            "\t\tAND a.acct_day = ?\n" +
                            ") hd", //
                    output);
        }
    }
}
